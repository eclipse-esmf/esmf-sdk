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

import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

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
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMME;
import io.openmanufacturing.sds.aspectmodel.vocabulary.Namespace;
import io.openmanufacturing.sds.aspectmodel.vocabulary.UNIT;
import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Code;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.CollectionValue;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.Duration;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.EncodingConstraint;
import io.openmanufacturing.sds.metamodel.EntityInstance;
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
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.Set;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.State;
import io.openmanufacturing.sds.metamodel.StructuredValue;
import io.openmanufacturing.sds.metamodel.TimeSeries;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Unit;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.datatypes.LangString;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

/**
 * AspectVisitor that translates an {@link Aspect} into the corresponding {@link Model}.
 *
 * The usual usage is calling {@link #visitAspect(Aspect, Base)}.
 * The context (i.e., the second argument of the visit methods) refers to the parent element in the model graph
 * traversal.
 */
@SuppressWarnings( "squid:S3655" ) // Optional<AspectModelUrn> is checked with isEmpty()
public class RdfModelCreatorVisitor implements AspectVisitor<RdfModelCreatorVisitor.ElementModel, Base>, Function<Aspect, Model> {
   private final BAMM bamm;
   private final BAMMC bammc;
   private final BAMME bamme;
   private final UNIT unitNamespace;
   private final Namespace namespace;
   private final Map<IsDescribed, Resource> anonymousResources = new HashMap<>();
   private final List<Resource> resourceList = new LinkedList<>();
   private final List<ComplexType> hasVisited = new LinkedList<>();

   /**
    * A {@link Model} together with an optional pointer to the resource that is defined in the model.
    * E.g., a (Jena) Model that describes an (Aspect) Model element, the focus element is the resource
    * of the described element while the rest of the model are the corresponding assertions.
    */
   @lombok.Value
   public static class ElementModel {
      Model model;
      Optional<RDFNode> focusElement;
   }

   /**
    * Constructor.
    *
    * @param metaModelVersion The meta model version to use in the serialized RDF model
    * @param namespace The namespace the model root element itself uses for its child elements
    */
   public RdfModelCreatorVisitor( final KnownVersion metaModelVersion, final Namespace namespace ) {
      bamm = new BAMM( metaModelVersion );
      bammc = new BAMMC( metaModelVersion );
      bamme = new BAMME( metaModelVersion, bamm );
      unitNamespace = new UNIT( metaModelVersion, bamm );
      this.namespace = namespace;
   }

   private Literal serializeLocalizedString( final LangString localizedString ) {
      final Locale languageTag = localizedString.getLanguageTag();
      final String value = localizedString.getValue();
      return ResourceFactory.createLangLiteral( value, languageTag.toLanguageTag() );
   }

   private Literal serializePlainString( final String string ) {
      return ResourceFactory.createPlainLiteral( string );
   }

   private RDFNode serializeTypedValue( final String value, final RDFDatatype rdfDatatype ) {
      return ResourceFactory.createTypedLiteral( value, rdfDatatype );
   }

   private Literal serializeBoolean( final Boolean value ) {
      return ResourceFactory.createTypedLiteral( value );
   }

   private boolean isLocalElement( final IsDescribed element ) {
      return element.getAspectModelUrn().isEmpty()
            || element.getAspectModelUrn().get().getUrnPrefix().equals( namespace.getNamespace() );
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
      element.getPreferredNames().stream().map( this::serializeLocalizedString ).forEach( preferredName ->
            model.add( elementResource, bamm.preferredName(), preferredName ) );
      element.getDescriptions().stream().map( this::serializeLocalizedString ).forEach( description ->
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
               final ElementModel propertyResult = property.accept( this, element );
               propertyModel.add( propertyResult.getModel() );
               final Resource propertyResource = propertyResult.getFocusElement()
                     .map( RDFNode::asResource )
                     .orElseGet( () -> getElementResource( property ) );

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
      propertyModel.add( anonymousPropertyNode, bamm.property(), propertyResource );
      if ( property.isOptional() ) {
         propertyModel.add( anonymousPropertyNode, bamm.optional(), serializeBoolean( true ) );
      }
      if ( property.isNotInPayload() ) {
         propertyModel.add( anonymousPropertyNode, bamm.notInPayload(), serializeBoolean( true ) );
      }
      if ( !property.getName().equals( property.getPayloadName() ) ) {
         propertyModel.add( anonymousPropertyNode, bamm.payloadName(), serializePlainString( property.getPayloadName() ) );
      }
      return anonymousPropertyNode;
   }

   private Model createCharacteristicsModel( final Characteristic characteristic ) {
      return createCharacteristicsModel( characteristic, false );
   }

   private Model createCharacteristicsModel( final Characteristic characteristic, final boolean skipDataType ) {
      final Model model = ModelFactory.createDefaultModel();

      if ( !isLocalElement( characteristic ) ) {
         return model;
      }

      final Resource resource = getElementResource( characteristic );
      if ( !skipDataType && characteristic.getDataType().isPresent() ) {
         final Type type = characteristic.getDataType().get();
         model.add( resource, bamm.dataType(), createResource( type.getUrn() ) );
         if ( type.is( ComplexType.class ) ) {
            model.add( type.accept( this, characteristic ).getModel() );
         }
      }

      model.add( serializeDescriptions( resource, characteristic ) );
      return model;
   }

   @Override
   public ElementModel visitBase( final Base base, final Base context ) {
      return new ElementModel( ModelFactory.createDefaultModel(), Optional.empty() );
   }

   private Model createCollectionModel( final Collection collection ) {
      final Model model = ModelFactory.createDefaultModel();

      if ( !isLocalElement( collection ) ) {
         return model;
      }

      final Resource resource = getElementResource( collection );
      if ( collection.getElementCharacteristic().isPresent() ) {
         final Characteristic elementCharacteristic = collection.getElementCharacteristic().get();
         model.add( resource, bammc.elementCharacteristic(), getElementResource( elementCharacteristic ) );
         model.add( elementCharacteristic.accept( this, collection ).getModel() );
      } else if ( collection.getDataType().isPresent() ) {
         final Type type = collection.getDataType().get();
         model.add( resource, bamm.dataType(), createResource( type.getUrn() ) );
         if ( !type.is( Scalar.class ) ) {
            model.add( type.accept( this, collection ).getModel() );
         }
      }

      model.add( serializeDescriptions( resource, collection ) );
      return model;
   }

   @Override
   public ElementModel visitCollection( final Collection collection, final Base context ) {
      final Model model = createCollectionModel( collection );
      final Resource resource = getElementResource( collection );
      model.add( resource, RDF.type, bammc.Collection() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitList( final io.openmanufacturing.sds.metamodel.List list, final Base context ) {
      final Model model = createCollectionModel( list );
      final Resource resource = getElementResource( list );
      model.add( resource, RDF.type, bammc.List() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitSet( final Set set, final Base context ) {
      final Model model = createCollectionModel( set );
      final Resource resource = getElementResource( set );
      model.add( resource, RDF.type, bammc.Set() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitSortedSet( final SortedSet sortedSet, final Base context ) {
      final Model model = createCollectionModel( sortedSet );
      final Resource resource = getElementResource( sortedSet );
      model.add( resource, RDF.type, bammc.SortedSet() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitTimeSeries( final TimeSeries timeSeries, final Base context ) {
      final Model model = createCollectionModel( timeSeries );
      final Resource resource = getElementResource( timeSeries );
      model.add( resource, RDF.type, bammc.TimeSeries() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitConstraint( final Constraint constraint, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      if ( !isLocalElement( constraint ) ) {
         return new ElementModel( model, Optional.empty() );
      }
      final Resource resource = getElementResource( constraint );
      model.add( serializeDescriptions( resource, constraint ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEncodingConstraint( final EncodingConstraint encodingConstraint, final Base context ) {
      final Model model = visitConstraint( encodingConstraint, null ).getModel();
      final Resource resource = getElementResource( encodingConstraint );
      model.add( resource, RDF.type, bammc.EncodingConstraint() );
      final Resource encoding = bamm.resource( encodingConstraint.getValue().name() );
      model.add( resource, bamm.value(), encoding );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitLanguageConstraint( final LanguageConstraint languageConstraint, final Base context ) {
      final Model model = visitConstraint( languageConstraint, null ).getModel();
      final Resource resource = getElementResource( languageConstraint );
      model.add( resource, RDF.type, bammc.LanguageConstraint() );
      model.add( resource, bammc.languageCode(), serializePlainString( languageConstraint.getLanguageCode().toLanguageTag() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitLocaleConstraint( final LocaleConstraint localeConstraint, final Base context ) {
      final Model model = visitConstraint( localeConstraint, null ).getModel();
      final Resource resource = getElementResource( localeConstraint );
      model.add( resource, RDF.type, bammc.LocaleConstraint() );
      model.add( resource, bammc.localeCode(), serializePlainString( localeConstraint.getLocaleCode().toLanguageTag() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitLengthConstraint( final LengthConstraint lengthConstraint, final Base context ) {
      final Model model = visitConstraint( lengthConstraint, null ).getModel();
      final Resource resource = getElementResource( lengthConstraint );
      lengthConstraint.getMinValue().stream().map( minValue ->
                  createStatement( resource, bammc.minValue(), serializeTypedValue( minValue.toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) ) )
            .forEach( model::add );
      lengthConstraint.getMaxValue().stream().map( maxValue ->
                  createStatement( resource, bammc.maxValue(), serializeTypedValue( maxValue.toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) ) )
            .forEach( model::add );
      model.add( resource, RDF.type, bammc.LengthConstraint() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitRangeConstraint( final RangeConstraint rangeConstraint, final Base context ) {
      final Model model = visitConstraint( rangeConstraint, null ).getModel();
      final Resource resource = getElementResource( rangeConstraint );
      model.add( resource, RDF.type, bammc.RangeConstraint() );
      rangeConstraint.getMinValue().stream()
            .flatMap( minValue -> minValue.accept( this, rangeConstraint ).getFocusElement().stream() )
            .map( literal -> createStatement( resource, bammc.minValue(), literal ) )
            .forEach( model::add );
      rangeConstraint.getMaxValue().stream()
            .flatMap( maxValue -> maxValue.accept( this, rangeConstraint ).getFocusElement().stream() )
            .map( literal -> createStatement( resource, bammc.maxValue(), literal ) )
            .forEach( model::add );
      model.add( resource, bammc.lowerBoundDefinition(),
            bammc.resource( rangeConstraint.getLowerBoundDefinition().toString().replace( " ", "_" ).toUpperCase() ) );
      model.add( resource, bammc.upperBoundDefinition(),
            bammc.resource( rangeConstraint.getUpperBoundDefinition().toString().replace( " ", "_" ).toUpperCase() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint, final Base context ) {
      final Model model = visitConstraint( regularExpressionConstraint, null ).getModel();
      final Resource resource = getElementResource( regularExpressionConstraint );
      model.add( resource, RDF.type, bammc.RegularExpressionConstraint() );
      model.add( resource, bamm.value(), serializePlainString( regularExpressionConstraint.getValue() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final Base context ) {
      final Model model = visitConstraint( fixedPointConstraint, null ).getModel();
      final Resource resource = getElementResource( fixedPointConstraint );
      model.add( resource, RDF.type, bammc.FixedPointConstraint() );
      model.add( resource, bammc.integer(),
            serializeTypedValue( fixedPointConstraint.getInteger().toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) );
      model.add( resource, bammc.scale(),
            serializeTypedValue( fixedPointConstraint.getScale().toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitCode( final Code code, final Base context ) {
      final Model model = createCharacteristicsModel( code );
      final Resource resource = getElementResource( code );
      model.add( resource, RDF.type, bammc.Code() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitDuration( final Duration duration, final Base context ) {
      final Model model = createCharacteristicsModel( duration );
      final Resource resource = getElementResource( duration );
      model.add( resource, RDF.type, bammc.Duration() );

      getUnitStatement( duration, resource ).ifPresent( model::add );
      duration.getUnit().map( unit -> unit.accept( this, duration ) ).ifPresent( elementModel -> model.add( elementModel.getModel() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEither( final Either either, final Base context ) {
      final Model model = createCharacteristicsModel( either );
      final Resource resource = getElementResource( either );
      model.add( resource, RDF.type, bammc.Either() );
      final ElementModel left = either.getLeft().accept( this, either );
      left.getFocusElement().ifPresent( leftCharacteristic -> model.add( resource, bammc.left(), leftCharacteristic ) );
      model.add( left.getModel() );
      final ElementModel right = either.getRight().accept( this, either );
      right.getFocusElement().ifPresent( rightCharacteristic -> model.add( resource, bammc.right(), rightCharacteristic ) );
      model.add( right.getModel() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEnumeration( final Enumeration enumeration, final Base context ) {
      final Model model = createCharacteristicsModel( enumeration );
      final Resource resource = getElementResource( enumeration );
      if ( !(enumeration.is( State.class )) ) {
         model.add( resource, RDF.type, bammc.Enumeration() );
      }

      final List<RDFNode> elements = enumeration.getValues().stream().flatMap( value -> {
         final ElementModel valueElementModel = value.accept( this, enumeration );
         model.add( valueElementModel.getModel() );
         return valueElementModel.getFocusElement().stream();
      } ).collect( Collectors.toList() );
      model.add( resource, bammc.values(), model.createList( elements.iterator() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEntityInstance( final EntityInstance instance, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( instance );
      model.add( resource, RDF.type, getElementResource( instance.getEntityType() ) );
      instance.getAssertions().forEach( ( key, value ) -> {
         final org.apache.jena.rdf.model.Property property = ResourceFactory.createProperty( key.getAspectModelUrn().orElseThrow().toString() );
         final ElementModel valueElementModel = value.accept( this, instance );
         model.add( valueElementModel.getModel() );
         valueElementModel.getFocusElement().ifPresent( elementValue -> model.add( resource, property, elementValue ) );
      } );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitScalarValue( final ScalarValue value, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Type type = value.getType();
      if ( type.getUrn().equals( RDF.langString.getURI() ) ) {
         final LangString langString = (LangString) value.getValue();
         final Literal literal = ResourceFactory.createLangLiteral( langString.getValue(), langString.getLanguageTag().toLanguageTag() );
         return new ElementModel( model, Optional.of( literal ) );
      }

      final Optional<RDFDatatype> targetType = DataType.getAllSupportedTypesForMetaModelVersion( value.getMetaModelVersion() ).stream()
            .filter( dataType -> dataType.getURI().equals( type.getUrn() ) ).findAny();
      if ( targetType.isEmpty() || type.getUrn().equals( XSD.xstring.getURI() ) ) {
         return new ElementModel( model, Optional.of( ResourceFactory.createStringLiteral( value.getValue().toString() ) ) );
      }
      return new ElementModel( model, Optional.of( ResourceFactory.createTypedLiteral( targetType.get().unparse( value.getValue() ), targetType.get() ) ) );
   }

   @Override
   public ElementModel visitCollectionValue( final CollectionValue collection, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final List<RDFNode> elements = collection.getValues().stream().flatMap( value -> {
         final ElementModel valueElementModel = value.accept( this, collection );
         model.add( valueElementModel.getModel() );
         return valueElementModel.getFocusElement().stream();
      } ).collect( Collectors.toList() );
      final RDFNode rdfList = model.createList( elements.iterator() );
      return new ElementModel( model, Optional.of( rdfList ) );
   }

   @Override
   public ElementModel visitState( final State state, final Base context ) {
      final Model model = visitEnumeration( state, null ).getModel();
      final Resource resource = getElementResource( state );
      model.add( resource, RDF.type, bammc.State() );
      final ElementModel defaultValueElementModel = state.getDefaultValue().accept( this, state );
      model.add( defaultValueElementModel.getModel() );
      defaultValueElementModel.getFocusElement().ifPresent( defaultValue -> model.add( resource, bammc.defaultValue(), defaultValue ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   private Optional<Statement> getUnitStatement( final Quantifiable elementWithUnit, final Resource targetResource ) {
      return elementWithUnit.getUnit()
            .flatMap( IsDescribed::getAspectModelUrn )
            .map( AspectModelUrn::toString )
            .map( unitUrn -> createStatement( targetResource, bammc.unit(),
                  createResource( unitUrn ) ) );
   }

   @Override
   public ElementModel visitMeasurement( final Measurement measurement, final Base context ) {
      final Model model = createCharacteristicsModel( measurement );
      final Resource resource = getElementResource( measurement );
      model.add( resource, RDF.type, bammc.Measurement() );

      getUnitStatement( measurement, resource ).ifPresent( model::add );
      measurement.getUnit().map( unit -> unit.accept( this, measurement ) ).ifPresent( elementModel -> model.add( elementModel.getModel() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitQuantifiable( final Quantifiable quantifiable, final Base context ) {
      final Model model = createCharacteristicsModel( quantifiable );
      final Resource resource = getElementResource( quantifiable );
      model.add( resource, RDF.type, bammc.Quantifiable() );

      getUnitStatement( quantifiable, resource ).ifPresent( model::add );
      quantifiable.getUnit().map( unit -> unit.accept( this, quantifiable ) ).ifPresent( elementModel -> model.add( elementModel.getModel() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitSingleEntity( final SingleEntity singleEntity, final Base context ) {
      final Model model = createCharacteristicsModel( singleEntity );
      final Resource resource = getElementResource( singleEntity );
      model.add( resource, RDF.type, bammc.SingleEntity() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitStructuredValue( final StructuredValue structuredValue, final Base context ) {
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
            .forEach( elementModel -> model.add( elementModel.getModel() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitTrait( final Trait trait, final Base context ) {
      final Model model = createCharacteristicsModel( trait, true );
      final Resource resource = getElementResource( trait );
      model.add( resource, RDF.type, bammc.Trait() );

      final Resource baseCharacteristicResource = getElementResource( trait.getBaseCharacteristic() );
      model.add( resource, bammc.baseCharacteristic(), baseCharacteristicResource );
      model.add( trait.getBaseCharacteristic().accept( this, trait ).getModel() );

      trait.getConstraints().forEach( constraint -> {
         final Resource constraintResource = getElementResource( constraint );
         model.add( resource, bammc.constraint(), constraintResource );
         model.add( constraint.accept( this, trait ).getModel() );
      } );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitAspect( final Aspect aspect, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( aspect );
      model.add( resource, RDF.type, bamm.Aspect() );
      model.add( serializeDescriptions( resource, aspect ) );
      model.add( serializeProperties( resource, aspect ) );
      model.add( resource, bamm.operations(), model.createList(
            aspect.getOperations().stream().map( this::getElementResource ).iterator() ) );
      aspect.getOperations().stream().map( operation -> operation.accept( this, aspect ) ).forEach( elementModel -> model.add( elementModel.getModel() ) );
      if ( !aspect.getEvents().isEmpty() ) {
         model.add( resource, bamm.events(), model.createList(
               aspect.getEvents().stream().map( this::getElementResource ).iterator() ) );
         aspect.getEvents().stream().map( event -> event.accept( this, aspect ) ).forEach( elementModel -> model.add( elementModel.getModel() ) );
      }
      return new ElementModel( model, Optional.of( resource ) );
   }

   @SuppressWarnings( "squid:S3655" )
   @Override
   public ElementModel visitProperty( final Property property, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      if ( property.getExtends().isPresent() ) {
         final Property superProperty = property.getExtends().get();
         // The Property is an instantiation of an abstract Property:
         // [ bamm:extends :superProperty ; bamm:characteristic ... ]
         if ( !superProperty.getCharacteristic().equals( property.getCharacteristic() ) ) {
            final Resource propertyResource = createResource();
            final Resource superPropertyResource = getElementResource( superProperty );
            final ElementModel superPropertyElementModel = superProperty.accept( this, context );
            model.add( superPropertyElementModel.getModel() );
            final Characteristic characteristic = property.getCharacteristic();
            final Resource characteristicResource = getElementResource( characteristic );
            model.add( characteristic.accept( this, property ).getModel() );
            model.add( propertyResource, bamm.characteristic(), characteristicResource );
            model.add( propertyResource, bamm._extends(), superPropertyResource );
            return new ElementModel( model, Optional.of( propertyResource ) );
         }
      }

      if ( !isLocalElement( property ) ) {
         return new ElementModel( model, Optional.empty() );
      }

      final Resource resource = getElementResource( property );
      model.add( resource, RDF.type, bamm.Property() );

      model.add( serializeDescriptions( resource, property ) );

      if ( property.getExampleValue().isPresent() ) {
         final Value exampleValue = property.getExampleValue().get();
         final ElementModel exampleValueElementModel = exampleValue.accept( this, property );
         model.add( exampleValueElementModel.getModel() );
         exampleValueElementModel.getFocusElement().ifPresent( exampleValueNode ->
               model.add( resource, bamm.exampleValue(), exampleValueNode ) );
      }

      final Characteristic characteristic = property.getCharacteristic();
      final Resource characteristicResource = getElementResource( characteristic );
      model.add( characteristic.accept( this, property ).getModel() );
      model.add( resource, bamm.characteristic(), characteristicResource );

      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitOperation( final Operation operation, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( operation );
      model.add( resource, RDF.type, bamm.Operation() );
      model.add( serializeDescriptions( resource, operation ) );
      final List<Resource> inputProperties = operation.getInput().stream().map( this::getElementResource ).collect( Collectors.toList() );
      model.add( resource, bamm.input(), model.createList( inputProperties.iterator() ) );
      operation.getInput().stream().map( property -> property.accept( this, operation ) ).forEach( elementModel -> model.add( elementModel.getModel() ) );
      operation.getOutput().ifPresent( outputProperty -> model.add( resource, bamm.output(), getElementResource( outputProperty ) ) );
      operation.getOutput().map( outputProperty -> outputProperty.accept( this, operation ) ).ifPresent( elementModel -> model.add( elementModel.getModel() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEvent( final Event event, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( event );
      model.add( resource, RDF.type, bamm.Event() );
      model.add( serializeDescriptions( resource, event ) );
      model.add( serializeProperties( resource, event ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitCharacteristic( final Characteristic characteristic, final Base context ) {
      if ( !isLocalElement( characteristic ) ) {
         return new ElementModel( ModelFactory.createDefaultModel(), characteristic.getAspectModelUrn().map( urn -> createResource( urn.toString() ) ) );
      }
      final Model model = createCharacteristicsModel( characteristic );
      final Resource resource = getElementResource( characteristic );
      model.add( resource, RDF.type, bamm.Characteristic() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitComplexType( final ComplexType complexType, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      if ( hasVisited.contains( complexType ) ) {
         return new ElementModel( model, Optional.empty() );
      }
      hasVisited.add( complexType );

      final Resource resource = getElementResource( complexType );
      if ( complexType.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = complexType.getExtends().get();
         model.add( extendedComplexType.accept( this, extendedComplexType ).getModel() );

         final Resource extendedTypeResource = createResource( extendedComplexType.getUrn() );
         model.add( createStatement( resource, bamm._extends(), extendedTypeResource ) );
      }
      model.add( serializeProperties( resource, complexType ) );
      model.add( serializeDescriptions( resource, complexType ) );

      if ( complexType.isAbstractEntity() ) {
         model.add( createStatement( resource, RDF.type, bamm.AbstractEntity() ) );
      } else {
         model.add( createStatement( resource, RDF.type, bamm.Entity() ) );
      }
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitAbstractEntity( final AbstractEntity abstractEntity, final Base context ) {
      if ( abstractEntity.getUrn().startsWith( bamme.getNamespace() ) ) {
         return new ElementModel( ModelFactory.createDefaultModel(),
               abstractEntity.getAspectModelUrn().map( urn -> ResourceFactory.createResource( urn.toString() ) ) );
      }
      final Model model = visitComplexType( abstractEntity, context ).getModel();
      abstractEntity.getExtendingElements().forEach( complexType -> model.add( complexType.accept( this, complexType ).getModel() ) );
      return new ElementModel( model, Optional.empty() );
   }

   @Override
   public ElementModel visitUnit( final Unit unit, final Base context ) {
      final Model model = ModelFactory.createDefaultModel();
      final String unitUrn = unit.getAspectModelUrn().map( AspectModelUrn::toString )
            .orElseThrow( () -> new InvalidModelException( "Invalid unit without URN." ) );
      if ( !createResource( unitUrn ).getNameSpace().equals( unitNamespace.getNamespace() ) ) {
         // This is a unit defined in the scope of the Aspect model
         final Resource unitResource = getElementResource( unit );
         model.add( unitResource, RDF.type, bamm.Unit() );
         unit.getQuantityKinds().forEach( quantityKind ->
               model.add( unitResource, bamm.quantityKind(), unitNamespace.resource( quantityKind.getName() ) ) );
         model.add( serializeDescriptions( unitResource, unit ) );
         unit.getSymbol().ifPresent( symbol -> model.add( unitResource, bamm.symbol(), serializePlainString( symbol ) ) );
         return new ElementModel( model, Optional.of( unitResource ) );
      }
      return new ElementModel( model, Optional.empty() );
   }

   @Override
   public ElementModel visitQuantityKind( final QuantityKind quantityKind, final Base context ) {
      return new ElementModel( ModelFactory.createDefaultModel(), Optional.empty() );
   }

   @Override
   public Model apply( final Aspect aspect ) {
      return visitAspect( aspect, null ).getModel();
   }
}
