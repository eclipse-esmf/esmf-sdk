/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package io.openmanufacturing.sds.aspectmodel.serializer;

import static org.apache.jena.rdf.model.ResourceFactory.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidModelException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.DataType;
import io.openmanufacturing.sds.aspectmodel.resolver.services.ExtendedXsdDataType;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMMC;
import io.openmanufacturing.sds.aspectmodel.vocabulary.Namespace;
import io.openmanufacturing.sds.aspectmodel.vocabulary.UNIT;
import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Code;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.Duration;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.EncodingConstraint;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.Event;
import io.openmanufacturing.sds.metamodel.FixedPointConstraint;
import io.openmanufacturing.sds.metamodel.HasProperties;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.metamodel.LanguageConstraint;
import io.openmanufacturing.sds.metamodel.LengthConstraint;
import io.openmanufacturing.sds.metamodel.LocaleConstraint;
import io.openmanufacturing.sds.metamodel.Measurement;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Quantifiable;
import io.openmanufacturing.sds.metamodel.QuantityKind;
import io.openmanufacturing.sds.metamodel.RangeConstraint;
import io.openmanufacturing.sds.metamodel.RegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.Set;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.State;
import io.openmanufacturing.sds.metamodel.StructuredValue;
import io.openmanufacturing.sds.metamodel.TimeSeries;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

/**
 * AspectVisitor that translates an {@link Aspect} into the corresponding {@link Model}.
 *
 * The usual usage is calling {@link #visitAspect(Aspect, Base)}.
 * The context (i.e., the second argument of the visit methods) refers to the parent element in the model graph
 * traversal.
 */
@SuppressWarnings( "squid:S3655" ) // Optional<AspectModelUrn> is checked with isEmpty()
public class RdfModelCreatorVisitor implements AspectVisitor<Model, Base>, Function<Aspect, Model> {
   private final KnownVersion metaModelVersion;
   private final BAMM bamm;
   private final BAMMC bammc;
   private final UNIT unitNamespace;
   private final Namespace namespace;
   private final Map<IsDescribed, Resource> anonymousResources = new HashMap<>();
   private final List<Resource> resourceList = new LinkedList<>();
   private final List<ComplexType> hasVisited = new LinkedList<>();

   /**
    * Constructor.
    *
    * @param metaModelVersion The meta model version to use in the serialized RDF model
    * @param namespace The namespace the model root element itself uses for its child elements
    */
   public RdfModelCreatorVisitor( final KnownVersion metaModelVersion, final Namespace namespace ) {
      this.metaModelVersion = metaModelVersion;
      bamm = new BAMM( metaModelVersion );
      bammc = new BAMMC( metaModelVersion );
      unitNamespace = new UNIT( metaModelVersion, bamm );
      this.namespace = namespace;
   }

   private Literal serializeLocalizedString( final Map.Entry<Locale, String> localizedString ) {
      final Locale languageTag = localizedString.getKey();
      final String value = localizedString.getValue();
      return ResourceFactory.createLangLiteral( value, languageTag.toLanguageTag() );
   }

   private Map.Entry<Resource, Model> serializeEntityInstance( final Entity entity, final Map<String, Object> entityInstance ) {
      final Model model = ModelFactory.createDefaultModel();
      final String nameKey = bamm.name().toString();
      final Resource resource = namespace.resource( entityInstance.get( nameKey ).toString() );
      model.add( resource, RDF.type, createResource( entity.getUrn() ) );

      entity.getProperties().stream()
            .map( property -> {
               final Model propertyModel = ModelFactory.createDefaultModel();
               property.getDataType().ifPresent( propertyType -> {
                  final String instanceKey = property.getName();
                  final Object instanceValue = entityInstance.get( instanceKey );
                  final org.apache.jena.rdf.model.Property instanceProperty = namespace.property( instanceKey );
                  if ( propertyType.isComplex() ) {
                     @SuppressWarnings( "unchecked" ) final Map.Entry<Resource, Model> nestedInstance =
                           serializeEntityInstance( (Entity) propertyType, (Map<String, Object>) instanceValue );
                     propertyModel.add( nestedInstance.getValue() );
                     propertyModel.add( resource, instanceProperty, nestedInstance.getKey() );
                  } else {
                     if ( instanceValue != null ) {
                        propertyModel.add( resource, instanceProperty, serializeTypedValue( instanceValue, propertyType ) );
                     }
                  }
               } );
               return propertyModel;
            } ).forEach( model::add );

      return new AbstractMap.SimpleEntry<>( resource, model );
   }

   private Literal serializePlainString( final String string ) {
      return ResourceFactory.createPlainLiteral( string );
   }

   private Literal serializeTypedValue( final String value, final RDFDatatype rdfDatatype ) {
      return ResourceFactory.createTypedLiteral( value, rdfDatatype );
   }

   private Literal serializeTypedValue( final Object value, final Type type ) {
      return serializeTypedValue( value, Optional.of( type ) );
   }

   private Literal serializeTypedValue( final Object value, final Optional<Type> optionalType ) {
      final Optional<RDFDatatype> targetType = optionalType.flatMap( type ->
            DataType.getAllSupportedTypesForMetaModelVersion( metaModelVersion ).stream()
                  .filter( dataType -> dataType.getURI().equals( type.getUrn() ) ).findAny() );
      if ( targetType.isEmpty() || optionalType.map( type -> type.getUrn().equals( XSD.xstring.getURI() ) ).orElse( false ) ) {
         return ResourceFactory.createStringLiteral( value.toString() );
      }
      return ResourceFactory.createTypedLiteral( targetType.get().unparse( value ), targetType.get() );
   }

   private Literal serializeBoolean( final Boolean value ) {
      return ResourceFactory.createTypedLiteral( value );
   }

   private boolean isLocalElement( final IsDescribed element ) {
      return element.getAspectModelUrn().isEmpty()
            || element.getAspectModelUrn().get().getUrnPrefix().equals( namespace.getNamespace() );
   }

   private Type getEffectiveDatatype( final Characteristic characteristic ) {
      final Optional<Type> type = characteristic.getDataType();
      return type.orElseGet( () -> getEffectiveDatatype( ((Trait) characteristic).getBaseCharacteristic() ) );
   }

   private Resource getElementResource( final IsDescribed element ) {
      if ( element.getAspectModelUrn().isEmpty() ) {
         return anonymousResources.computeIfAbsent( element, key -> createResource() );
      }
      return createResource( element.getAspectModelUrn().get().toString() );
   }

   private Model serializeDescriptions( final Resource elementResource, final IsDescribed element ) {
      final Model model = ModelFactory.createDefaultModel();
      element.getSee().forEach( seeValue -> model.add( elementResource, bamm.see(), ResourceFactory.createResource( seeValue ) ) );
      element.getPreferredNames().entrySet().stream().map( this::serializeLocalizedString ).forEach( preferredName ->
            model.add( elementResource, bamm.preferredName(), preferredName ) );
      element.getDescriptions().entrySet().stream().map( this::serializeLocalizedString ).forEach( description ->
            model.add( elementResource, bamm.description(), description ) );
      return model;
   }

   @SuppressWarnings( "squid:S2250" )
   // Amount of elements in list is regarding amount of properties in Aspect Model. Even in bigger aspects this should not lead to performance issues
   private Model serializeProperties( final Resource elementResource, final HasProperties element ) {
      final Model model = ModelFactory.createDefaultModel();
      final List<RDFNode> propertiesList = new ArrayList<>();
      if ( resourceList.contains( elementResource ) ) {
         return model;
      }
      resourceList.add( elementResource );
      element.getProperties().stream()
            .filter( this::isLocalElement )
            .map( property -> {
               final Model propertyModel = ModelFactory.createDefaultModel();
               final Resource propertyResource = getElementResource( property );
               propertyModel.add( property.accept( this, element ) );

               if ( property.isOptional() || property.isNotInPayload() || !property.getName().equals( property.getPayloadName() ) ) {
                  final Resource anonymousPropertyNode = serializeAnonymousPropertyNodes( property, propertyModel, propertyResource );
                  propertiesList.add( anonymousPropertyNode );
                  return propertyModel;
               }

               propertiesList.add( propertyResource );
               return propertyModel;
            } ).forEach( model::add );

      model.add( elementResource, bamm.properties(), model.createList( propertiesList.iterator() ) );
      return model;
   }

   private Resource serializeAnonymousPropertyNodes( final Property property, final Model propertyModel, final Resource propertyResource ) {
      final Resource anonymousPropertyNode = createResource();
      if ( property.isOptional() ) {
         propertyModel.add( anonymousPropertyNode, bamm.property(), propertyResource );
         propertyModel.add( anonymousPropertyNode, bamm.optional(), serializeBoolean( true ) );
      }
      if ( property.isNotInPayload() ) {
         propertyModel.add( anonymousPropertyNode, bamm.property(), propertyResource );
         propertyModel.add( anonymousPropertyNode, bamm.notInPayload(), serializeBoolean( true ) );
      }
      if ( !property.getName().equals( property.getPayloadName() ) ) {
         propertyModel.add( anonymousPropertyNode, bamm.property(), propertyResource );
         propertyModel.add( anonymousPropertyNode, bamm.payloadName(), serializePlainString( property.getPayloadName() ) );
      }
      return anonymousPropertyNode;
   }

   private Model createCharacteristicsModel( final Characteristic characteristic ) {
      final Model model = ModelFactory.createDefaultModel();

      if ( !isLocalElement( characteristic ) ) {
         return model;
      }

      final Resource resource = getElementResource( characteristic );
      if ( !characteristic.hasSyntheticName() ) {
         model.add( resource, bamm.name(), serializePlainString( characteristic.getName() ) );
      }

      characteristic.getDataType().ifPresent( type -> {
         model.add( resource, bamm.dataType(), createResource( type.getUrn() ) );
         if ( type.isComplex() ) {
            model.add( type.accept( this, characteristic ) );
         }
      } );

      model.add( serializeDescriptions( resource, characteristic ) );
      return model;
   }

   @Override
   public Model visitBase( final Base base, final Base context ) {
      return ModelFactory.createDefaultModel();
   }

   @Override
   public Model visitCollection( final Collection collection, final Base context ) {
      final Model model = createCharacteristicsModel( collection );
      final Resource resource = getElementResource( collection );
      model.add( resource, RDF.type, bammc.Collection() );
      return model;
   }

   @Override
   public Model visitList( final io.openmanufacturing.sds.metamodel.List list, final Base context ) {
      final Model model = createCharacteristicsModel( list );
      final Resource resource = getElementResource( list );
      model.add( resource, RDF.type, bammc.List() );
      return model;
   }

   @Override
   public Model visitSet( final Set set, final Base context ) {
      final Model model = createCharacteristicsModel( set );
      final Resource resource = getElementResource( set );
      model.add( resource, RDF.type, bammc.Set() );
      return model;
   }

   @Override
   public Model visitSortedSet( final SortedSet sortedSet, final Base context ) {
      final Model model = createCharacteristicsModel( sortedSet );
      final Resource resource = getElementResource( sortedSet );
      model.add( resource, RDF.type, bammc.SortedSet() );
      return model;
   }

   @Override
   public Model visitTimeSeries( final TimeSeries timeSeries, final Base context ) {
      final Model model = createCharacteristicsModel( timeSeries );
      final Resource resource = getElementResource( timeSeries );
      model.add( resource, RDF.type, bammc.TimeSeries() );
      return model;
   }

   @Override
   public Model visitConstraint( final Constraint constraint, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      if ( !isLocalElement( constraint ) ) {
         return model;
      }
      final Resource resource = getElementResource( constraint );
      if ( !constraint.hasSyntheticName() ) {
         model.add( resource, bamm.name(), serializePlainString( constraint.getName() ) );
      }
      model.add( serializeDescriptions( resource, constraint ) );
      return model;
   }

   @Override
   public Model visitEncodingConstraint( final EncodingConstraint encodingConstraint, final Base context ) {
      final Model model = visitConstraint( encodingConstraint, null );
      final Resource resource = getElementResource( encodingConstraint );
      model.add( resource, RDF.type, bammc.EncodingConstraint() );
      model.add( resource, bamm.value(), serializePlainString( encodingConstraint.getValue().name() ) );
      return model;
   }

   @Override
   public Model visitLanguageConstraint( final LanguageConstraint languageConstraint, final Base context ) {
      final Model model = visitConstraint( languageConstraint, null );
      final Resource resource = getElementResource( languageConstraint );
      model.add( resource, RDF.type, bammc.LanguageConstraint() );
      model.add( resource, bammc.languageCode(), serializePlainString( languageConstraint.getLanguageCode().toLanguageTag() ) );
      return model;
   }

   @Override
   public Model visitLocaleConstraint( final LocaleConstraint localeConstraint, final Base context ) {
      final Model model = visitConstraint( localeConstraint, null );
      final Resource resource = getElementResource( localeConstraint );
      model.add( resource, RDF.type, bammc.LocaleConstraint() );
      model.add( resource, bammc.localeCode(), serializePlainString( localeConstraint.getLocaleCode().toLanguageTag() ) );
      return model;
   }

   @Override
   public Model visitLengthConstraint( final LengthConstraint lengthConstraint, final Base context ) {
      final Model model = visitConstraint( lengthConstraint, null );
      final Resource resource = getElementResource( lengthConstraint );
      lengthConstraint.getMinValue().stream().map( minValue ->
                  createStatement( resource, bammc.minValue(), serializeTypedValue( minValue.toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) ) )
            .forEach( model::add );
      lengthConstraint.getMinValue().stream().map( maxValue ->
                  createStatement( resource, bammc.maxValue(), serializeTypedValue( maxValue.toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) ) )
            .forEach( model::add );
      model.add( resource, RDF.type, bammc.LengthConstraint() );
      return model;
   }

   @Override
   public Model visitRangeConstraint( final RangeConstraint rangeConstraint, final Base context ) {
      final Model model = visitConstraint( rangeConstraint, null );
      final Resource resource = getElementResource( rangeConstraint );
      final Trait parentTrait = (Trait) context;
      final Type type = getEffectiveDatatype( parentTrait );
      model.add( resource, RDF.type, bammc.RangeConstraint() );
      rangeConstraint.getMinValue().stream()
            .map( minValue -> serializeTypedValue( minValue, type ) )
            .map( literal -> createStatement( resource, bammc.minValue(), literal ) )
            .forEach( model::add );
      rangeConstraint.getMinValue().stream()
            .map( maxValue -> serializeTypedValue( maxValue, type ) )
            .map( literal -> createStatement( resource, bammc.maxValue(), literal ) )
            .forEach( model::add );
      model.add( resource, bammc.lowerBoundDefinition(),
            bammc.resource( rangeConstraint.getLowerBoundDefinition().toString().replace( " ", "_" ).toUpperCase() ) );
      model.add( resource, bammc.upperBoundDefinition(),
            bammc.resource( rangeConstraint.getUpperBoundDefinition().toString().replace( " ", "_" ).toUpperCase() ) );
      return model;
   }

   @Override
   public Model visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint, final Base context ) {
      final Model model = visitConstraint( regularExpressionConstraint, null );
      final Resource resource = getElementResource( regularExpressionConstraint );
      model.add( resource, RDF.type, bammc.RegularExpressionConstraint() );
      model.add( resource, bamm.value(), serializePlainString( regularExpressionConstraint.getValue() ) );
      return model;
   }

   @Override
   public Model visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final Base context ) {
      final Model model = visitConstraint( fixedPointConstraint, null );
      final Resource resource = getElementResource( fixedPointConstraint );
      model.add( resource, RDF.type, bammc.FixedPointConstraint() );
      model.add( resource, bammc.integer(), serializeTypedValue( fixedPointConstraint.getInteger().toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) );
      model.add( resource, bammc.scale(), serializeTypedValue( fixedPointConstraint.getScale().toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) );
      return model;
   }

   @Override
   public Model visitCode( final Code code, final Base context ) {
      final Model model = createCharacteristicsModel( code );
      final Resource resource = getElementResource( code );
      model.add( resource, RDF.type, bammc.Code() );
      return model;
   }

   @Override
   public Model visitDuration( final Duration duration, final Base context ) {
      final Model model = createCharacteristicsModel( duration );
      final Resource resource = getElementResource( duration );
      model.add( resource, RDF.type, bammc.Duration() );

      getUnitStatement( duration, resource ).ifPresent( model::add );
      duration.getUnit().map( unit -> unit.accept( this, duration ) ).ifPresent( model::add );
      return model;
   }

   @Override
   public Model visitEither( final Either either, final Base context ) {
      final Model model = createCharacteristicsModel( either );
      final Resource resource = getElementResource( either );
      model.add( resource, RDF.type, bammc.Either() );
      model.add( either.getLeft().accept( this, either ) );
      model.add( either.getRight().accept( this, either ) );
      return model;
   }

   @Override
   public Model visitEnumeration( final Enumeration enumeration, final Base context ) {
      final Model model = createCharacteristicsModel( enumeration );
      final Resource resource = getElementResource( enumeration );
      if ( !(enumeration instanceof State) ) {
         model.add( resource, RDF.type, bammc.Enumeration() );
      }
      final Type type = getEffectiveDatatype( enumeration );

      if ( type.isScalar() ) {
         model.add( resource, bammc.values(), model.createList(
               enumeration.getValues().stream().map( value -> serializeTypedValue( value, type ) ).iterator() ) );
         return model;
      }

      final Entity entity = (Entity) type;
      @SuppressWarnings( "unchecked" ) final Map<Resource, Model> instances =
            enumeration.getValues().stream().map( instanceValue -> serializeEntityInstance( entity, (Map<String, Object>) instanceValue ) )
                  .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );

      instances.values().forEach( model::add );
      model.add( resource, bammc.values(), model.createList( instances.keySet().stream().iterator() ) );

      return model;
   }

   @Override
   public Model visitState( final State state, final Base context ) {
      final Model model = visitEnumeration( state, null );
      final Resource resource = getElementResource( state );
      model.add( resource, RDF.type, bammc.State() );
      model.add( resource, bammc.defaultValue(), serializeTypedValue( state.getDefaultValue(), state.getDataType() ) );
      return model;
   }

   private Optional<Statement> getUnitStatement( final Quantifiable elementWithUnit, final Resource targetResource ) {
      return elementWithUnit.getUnit()
            .flatMap( IsDescribed::getAspectModelUrn )
            .map( AspectModelUrn::toString )
            .map( unitUrn -> createStatement( targetResource, bammc.unit(),
                  createResource( unitUrn ) ) );
   }

   @Override
   public Model visitMeasurement( final Measurement measurement, final Base context ) {
      final Model model = createCharacteristicsModel( measurement );
      final Resource resource = getElementResource( measurement );
      model.add( resource, RDF.type, bammc.Measurement() );

      getUnitStatement( measurement, resource ).ifPresent( model::add );
      measurement.getUnit().map( unit -> unit.accept( this, measurement ) ).ifPresent( model::add );
      return model;
   }

   @Override
   public Model visitQuantifiable( final Quantifiable quantifiable, final Base context ) {
      final Model model = createCharacteristicsModel( quantifiable );
      final Resource resource = getElementResource( quantifiable );
      model.add( resource, RDF.type, bammc.Quantifiable() );

      getUnitStatement( quantifiable, resource ).ifPresent( model::add );
      quantifiable.getUnit().map( unit -> unit.accept( this, quantifiable ) ).ifPresent( model::add );
      return model;
   }

   @Override
   public Model visitSingleEntity( final SingleEntity singleEntity, final Base context ) {
      final Model model = createCharacteristicsModel( singleEntity );
      final Resource resource = getElementResource( singleEntity );
      model.add( resource, RDF.type, bammc.SingleEntity() );
      return model;
   }

   @Override
   public Model visitStructuredValue( final StructuredValue structuredValue, final Base context ) {
      final Model model = createCharacteristicsModel( structuredValue );
      final Resource resource = getElementResource( structuredValue );
      model.add( resource, RDF.type, bammc.StructuredValue() );

      model.add( resource, bammc.deconstructionRule(), serializePlainString( structuredValue.getDeconstructionRule() ) );
      final RDFList elementsList = model.createList(
            structuredValue.getElements().stream().map( element -> element instanceof String ?
                  serializePlainString( (String) element ) :
                  getElementResource( (Property) element ) ).iterator() );
      model.add( resource, bammc.elements(), elementsList );

      structuredValue.getElements().stream()
            .filter( Property.class::isInstance )
            .map( Property.class::cast )
            .map( property -> property.accept( this, structuredValue ) )
            .forEach( model::add );
      return model;
   }

   @Override
   public Model visitTrait( final Trait trait, final Base context ) {
      final Model model = createCharacteristicsModel( trait );
      final Resource resource = getElementResource( trait );
      model.add( resource, RDF.type, bammc.Trait() );

      final Resource baseCharacteristicResource = getElementResource( trait.getBaseCharacteristic() );
      model.add( resource, bammc.baseCharacteristic(), baseCharacteristicResource );
      model.add( trait.getBaseCharacteristic().accept( this, trait ) );

      trait.getConstraints().forEach( constraint -> {
         final Resource constraintResource = getElementResource( constraint );
         model.add( resource, bammc.constraint(), constraintResource );
         model.add( constraint.accept( this, trait ) );
      } );
      return model;
   }

   @Override
   public Model visitAspect( final Aspect aspect, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( aspect );
      model.add( resource, RDF.type, bamm.Aspect() );
      if ( !aspect.hasSyntheticName() ) {
         model.add( resource, bamm.name(), serializePlainString( aspect.getName() ) );
      }
      model.add( serializeDescriptions( resource, aspect ) );
      model.add( serializeProperties( resource, aspect ) );
      model.add( resource, bamm.operations(), model.createList(
            aspect.getOperations().stream().map( this::getElementResource ).iterator() ) );
      aspect.getOperations().stream().map( operation -> operation.accept( this, aspect ) ).forEach( model::add );
      if ( !aspect.getEvents().isEmpty() ) {
         model.add( resource, bamm.events(), model.createList(
               aspect.getEvents().stream().map( this::getElementResource ).iterator() ) );
         aspect.getEvents().stream().map( event -> event.accept( this, aspect ) ).forEach( model::add );
      }
      return model;
   }

   @SuppressWarnings( "squid:S3655" )
   @Override
   public Model visitProperty( final Property property, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      if ( !isLocalElement( property ) ) {
         return model;
      }

      final Resource resource = getElementResource( property );
      model.add( resource, RDF.type, bamm.Property() );
      if ( !property.hasSyntheticName() ) {
         model.add( resource, bamm.name(), serializePlainString( property.getName() ) );
      }

      model.add( serializeDescriptions( resource, property ) );

      if ( property.getExampleValue().isPresent() ) {
         model.add( resource, bamm.exampleValue(), serializeTypedValue( property.getExampleValue().get(), property.getDataType() ) );
      }

      final Characteristic characteristic = property.getCharacteristic();
      final Resource characteristicResource = getElementResource( characteristic );
      model.add( characteristic.accept( this, property ) );
      model.add( resource, bamm.characteristic(), characteristicResource );

      return model;
   }

   @Override
   public Model visitOperation( final Operation operation, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( operation );
      model.add( resource, RDF.type, bamm.Operation() );
      if ( !operation.hasSyntheticName() ) {
         model.add( resource, bamm.name(), serializePlainString( operation.getName() ) );
      }
      model.add( serializeDescriptions( resource, operation ) );
      final List<Resource> inputProperties = operation.getInput().stream().map( this::getElementResource ).collect( Collectors.toList() );
      model.add( resource, bamm.input(), model.createList( inputProperties.iterator() ) );
      operation.getInput().stream().map( property -> property.accept( this, operation ) ).forEach( model::add );
      operation.getOutput().ifPresent( outputProperty -> model.add( resource, bamm.output(), getElementResource( outputProperty ) ) );
      operation.getOutput().map( outputProperty -> outputProperty.accept( this, operation ) ).ifPresent( model::add );
      return model;
   }

   @Override
   public Model visitEvent( final Event event, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( event );
      model.add( resource, RDF.type, bamm.Event() );
      if ( !event.hasSyntheticName() ) {
         model.add( resource, bamm.name(), serializePlainString( event.getName() ) );
      }
      model.add( serializeDescriptions( resource, event ) );
      model.add( serializeProperties( resource, event ) );
      return model;
   }

   @Override
   public Model visitCharacteristic( final Characteristic characteristic, final Base context ) {
      if ( !isLocalElement( characteristic ) ) {
         return ModelFactory.createDefaultModel();
      }
      final Model model = createCharacteristicsModel( characteristic );
      final Resource resource = getElementResource( characteristic );
      model.add( resource, RDF.type, bamm.Characteristic() );
      return model;
   }

   @Override
   public Model visitComplexType( final ComplexType complexType, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      if ( hasVisited.contains( complexType ) ) {
         return model;
      }
      hasVisited.add( complexType );

      final Resource resource = getElementResource( complexType );
      if ( !complexType.hasSyntheticName() ) {
         model.add( resource, bamm.name(), serializePlainString( complexType.getName() ) );
      }
      if ( complexType.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = complexType.getExtends().get();
         model.add( extendedComplexType.accept( this, extendedComplexType ) );

         final Resource extendedTypeResource = createResource( extendedComplexType.getUrn() );
         model.add( createStatement( resource, bamm._extends(), extendedTypeResource ) );
      }
      model.add( serializeProperties( resource, complexType ) );
      model.add( serializeDescriptions( resource, complexType ) );

      if ( complexType.isAbstractEntity() ) {
         return model.add( createStatement( resource, RDF.type, bamm.AbstractEntity() ) );
      }
      return model.add( createStatement( resource, RDF.type, bamm.Entity() ) );
   }

   @Override
   public Model visitAbstractEntity( final AbstractEntity abstractEntity, final Base context ) {
      final Model model = visitComplexType( abstractEntity, context );
      abstractEntity.getExtendingElements().forEach( complexType -> model.add( complexType.accept( this, complexType ) ) );
      return model;
   }

   @Override
   public Model visitUnit( final Unit unit, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final String unitUrn = unit.getAspectModelUrn().map( AspectModelUrn::toString )
            .orElseThrow( () -> new InvalidModelException( "Invalid unit without URN." ) );
      if ( !createResource( unitUrn ).getNameSpace().equals( unitNamespace.getNamespace() ) ) {
         // This is a unit defined in the scope of the Aspect model
         final Resource unitResource = getElementResource( unit );
         model.add( unitResource, RDF.type, bamm.Unit() );
         if ( !unit.hasSyntheticName() ) {
            model.add( unitResource, bamm.name(), serializePlainString( unit.getName() ) );
         }
         unit.getQuantityKinds().forEach( quantityKind ->
               model.add( unitResource, bamm.quantityKind(), unitNamespace.resource( quantityKind.getName() ) ) );
         model.add( serializeDescriptions( unitResource, unit ) );
      }
      return model;
   }

   @Override
   public Model visitQuantityKind( final QuantityKind quantityKind, final Base context ) {
      return ModelFactory.createDefaultModel();
   }

   @Override
   public Model apply( final Aspect aspect ) {
      return visitAspect( aspect, null );
   }
}
