/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.serializer;

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

import org.eclipse.esmf.aspectmodel.resolver.services.DataType;
import org.eclipse.esmf.aspectmodel.resolver.services.ExtendedXsdDataType;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMMC;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMME;
import org.eclipse.esmf.aspectmodel.vocabulary.UNIT;
import org.eclipse.esmf.characteristic.Code;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.Duration;
import org.eclipse.esmf.characteristic.Either;
import org.eclipse.esmf.characteristic.Enumeration;
import org.eclipse.esmf.characteristic.Measurement;
import org.eclipse.esmf.characteristic.Quantifiable;
import org.eclipse.esmf.characteristic.Set;
import org.eclipse.esmf.characteristic.SingleEntity;
import org.eclipse.esmf.characteristic.SortedSet;
import org.eclipse.esmf.characteristic.State;
import org.eclipse.esmf.characteristic.StructuredValue;
import org.eclipse.esmf.characteristic.TimeSeries;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.constraint.EncodingConstraint;
import org.eclipse.esmf.constraint.FixedPointConstraint;
import org.eclipse.esmf.constraint.LanguageConstraint;
import org.eclipse.esmf.constraint.LengthConstraint;
import org.eclipse.esmf.constraint.LocaleConstraint;
import org.eclipse.esmf.constraint.RangeConstraint;
import org.eclipse.esmf.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.HasProperties;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.impl.BoundDefinition;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

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

/**
 * AspectVisitor that translates an {@link Aspect} into the corresponding {@link Model}.
 * The usual usage is calling {@link #visitAspect(Aspect, ModelElement)}.
 * The context (i.e., the second argument of the visit methods) refers to the parent element in the model graph
 * traversal.
 */
@SuppressWarnings( "squid:S3655" ) // Optional<AspectModelUrn> is checked with isEmpty()
public class RdfModelCreatorVisitor implements AspectVisitor<RdfModelCreatorVisitor.ElementModel, ModelElement>, Function<Aspect, Model> {
   private final SAMM samm;
   private final SAMMC sammc;
   private final SAMME samme;
   private final UNIT unitNamespace;
   private final Namespace namespace;
   private final Map<NamedElement, Resource> anonymousResources = new HashMap<>();
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
      samm = new SAMM( metaModelVersion );
      sammc = new SAMMC( metaModelVersion );
      samme = new SAMME( metaModelVersion, samm );
      unitNamespace = new UNIT( metaModelVersion, samm );
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

   private boolean isLocalElement( final NamedElement element ) {
      return element.getAspectModelUrn().isEmpty()
            || element.getAspectModelUrn().get().getUrnPrefix().equals( namespace.getNamespace() );
   }

   private Resource getElementResource( final NamedElement element ) {
      if ( element.getAspectModelUrn().isEmpty() ) {
         return anonymousResources.computeIfAbsent( element, key -> createResource() );
      }
      return createResource( element.getAspectModelUrn().get().toString() );
   }

   private Model serializeDescriptions( final Resource elementResource, final NamedElement element ) {
      final Model model = ModelFactory.createDefaultModel();
      element.getSee().forEach( seeValue -> model.add( elementResource, samm.see(), ResourceFactory.createResource( seeValue ) ) );
      element.getPreferredNames().stream().map( this::serializeLocalizedString ).forEach( preferredName ->
            model.add( elementResource, samm.preferredName(), preferredName ) );
      element.getDescriptions().stream().map( this::serializeLocalizedString ).forEach( description ->
            model.add( elementResource, samm.description(), description ) );
      return model;
   }

   @SuppressWarnings( "squid:S2250" )
   // Amount of elements in list is regarding amount of properties in Aspect Model. Even in bigger aspects this should not lead to
   // performance issues
   private Model serializePropertiesOrParameters( final Resource elementResource, final HasProperties element,
         final org.apache.jena.rdf.model.Property theProperty ) {
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

      model.add( elementResource, theProperty, model.createList( propertiesList.iterator() ) );
      return model;
   }

   private Model serializeParameters( final Resource elementResource, final HasProperties element ) {
      return serializePropertiesOrParameters( elementResource, element, samm.parameters() );
   }

   private Model serializeProperties( final Resource elementResource, final HasProperties element ) {
      return serializePropertiesOrParameters( elementResource, element, samm.properties() );
   }

   private Resource serializeAnonymousPropertyNodes( final Property property, final Model propertyModel, final Resource propertyResource ) {
      final Resource anonymousPropertyNode = createResource();
      propertyModel.add( anonymousPropertyNode, samm.property(), propertyResource );
      if ( property.isOptional() ) {
         propertyModel.add( anonymousPropertyNode, samm.optional(), serializeBoolean( true ) );
      }
      if ( property.isNotInPayload() ) {
         propertyModel.add( anonymousPropertyNode, samm.notInPayload(), serializeBoolean( true ) );
      }
      if ( !property.getName().equals( property.getPayloadName() ) ) {
         propertyModel.add( anonymousPropertyNode, samm.payloadName(), serializePlainString( property.getPayloadName() ) );
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
         model.add( resource, samm.dataType(), createResource( type.getUrn() ) );
         if ( type.is( ComplexType.class ) ) {
            model.add( type.accept( this, characteristic ).getModel() );
         }
      }

      model.add( serializeDescriptions( resource, characteristic ) );
      return model;
   }

   @Override
   public ElementModel visitBase( final ModelElement modelElement, final ModelElement context ) {
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
         model.add( resource, sammc.elementCharacteristic(), getElementResource( elementCharacteristic ) );
         model.add( elementCharacteristic.accept( this, collection ).getModel() );
      } else if ( collection.getDataType().isPresent() ) {
         final Type type = collection.getDataType().get();
         model.add( resource, samm.dataType(), createResource( type.getUrn() ) );
         if ( !type.is( Scalar.class ) ) {
            model.add( type.accept( this, collection ).getModel() );
         }
      }

      model.add( serializeDescriptions( resource, collection ) );
      return model;
   }

   @Override
   public ElementModel visitCollection( final Collection collection, final ModelElement context ) {
      final Model model = createCollectionModel( collection );
      final Resource resource = getElementResource( collection );
      model.add( resource, RDF.type, sammc.Collection() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitList( final org.eclipse.esmf.characteristic.List list, final ModelElement context ) {
      final Model model = createCollectionModel( list );
      final Resource resource = getElementResource( list );
      model.add( resource, RDF.type, sammc.List() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitSet( final Set set, final ModelElement context ) {
      final Model model = createCollectionModel( set );
      final Resource resource = getElementResource( set );
      model.add( resource, RDF.type, sammc.Set() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitSortedSet( final SortedSet sortedSet, final ModelElement context ) {
      final Model model = createCollectionModel( sortedSet );
      final Resource resource = getElementResource( sortedSet );
      model.add( resource, RDF.type, sammc.SortedSet() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitTimeSeries( final TimeSeries timeSeries, final ModelElement context ) {
      final Model model = createCollectionModel( timeSeries );
      final Resource resource = getElementResource( timeSeries );
      model.add( resource, RDF.type, sammc.TimeSeries() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitConstraint( final Constraint constraint, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      if ( !isLocalElement( constraint ) ) {
         return new ElementModel( model, Optional.empty() );
      }
      final Resource resource = getElementResource( constraint );
      model.add( serializeDescriptions( resource, constraint ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEncodingConstraint( final EncodingConstraint encodingConstraint, final ModelElement context ) {
      final Model model = visitConstraint( encodingConstraint, null ).getModel();
      final Resource resource = getElementResource( encodingConstraint );
      model.add( resource, RDF.type, sammc.EncodingConstraint() );
      final Resource encoding = samm.resource( encodingConstraint.getValue().name() );
      model.add( resource, samm.value(), encoding );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitLanguageConstraint( final LanguageConstraint languageConstraint, final ModelElement context ) {
      final Model model = visitConstraint( languageConstraint, null ).getModel();
      final Resource resource = getElementResource( languageConstraint );
      model.add( resource, RDF.type, sammc.LanguageConstraint() );
      model.add( resource, sammc.languageCode(), serializePlainString( languageConstraint.getLanguageCode().toLanguageTag() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitLocaleConstraint( final LocaleConstraint localeConstraint, final ModelElement context ) {
      final Model model = visitConstraint( localeConstraint, null ).getModel();
      final Resource resource = getElementResource( localeConstraint );
      model.add( resource, RDF.type, sammc.LocaleConstraint() );
      model.add( resource, sammc.localeCode(), serializePlainString( localeConstraint.getLocaleCode().toLanguageTag() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitLengthConstraint( final LengthConstraint lengthConstraint, final ModelElement context ) {
      final Model model = visitConstraint( lengthConstraint, null ).getModel();
      final Resource resource = getElementResource( lengthConstraint );
      lengthConstraint.getMinValue().stream().map( minValue ->
                  createStatement( resource, sammc.minValue(),
                        serializeTypedValue( minValue.toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) ) )
            .forEach( model::add );
      lengthConstraint.getMaxValue().stream().map( maxValue ->
                  createStatement( resource, sammc.maxValue(),
                        serializeTypedValue( maxValue.toString(), ExtendedXsdDataType.NON_NEGATIVE_INTEGER ) ) )
            .forEach( model::add );
      model.add( resource, RDF.type, sammc.LengthConstraint() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitRangeConstraint( final RangeConstraint rangeConstraint, final ModelElement context ) {
      final Model model = visitConstraint( rangeConstraint, null ).getModel();
      final Resource resource = getElementResource( rangeConstraint );
      model.add( resource, RDF.type, sammc.RangeConstraint() );
      rangeConstraint.getMinValue().stream()
            .flatMap( minValue -> minValue.accept( this, rangeConstraint ).getFocusElement().stream() )
            .map( literal -> createStatement( resource, sammc.minValue(), literal ) )
            .forEach( model::add );
      rangeConstraint.getMaxValue().stream()
            .flatMap( maxValue -> maxValue.accept( this, rangeConstraint ).getFocusElement().stream() )
            .map( literal -> createStatement( resource, sammc.maxValue(), literal ) )
            .forEach( model::add );
      if ( rangeConstraint.getLowerBoundDefinition() != BoundDefinition.OPEN ) {
         model.add( resource, sammc.lowerBoundDefinition(),
               sammc.resource( rangeConstraint.getLowerBoundDefinition().toString().replace( " ", "_" ).toUpperCase() ) );
      }
      if ( rangeConstraint.getUpperBoundDefinition() != BoundDefinition.OPEN ) {
         model.add( resource, sammc.upperBoundDefinition(),
               sammc.resource( rangeConstraint.getUpperBoundDefinition().toString().replace( " ", "_" ).toUpperCase() ) );
      }
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final ModelElement context ) {
      final Model model = visitConstraint( regularExpressionConstraint, null ).getModel();
      final Resource resource = getElementResource( regularExpressionConstraint );
      model.add( resource, RDF.type, sammc.RegularExpressionConstraint() );
      model.add( resource, samm.value(), serializePlainString( regularExpressionConstraint.getValue() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitFixedPointConstraint( final FixedPointConstraint fixedPointConstraint, final ModelElement context ) {
      final Model model = visitConstraint( fixedPointConstraint, null ).getModel();
      final Resource resource = getElementResource( fixedPointConstraint );
      model.add( resource, RDF.type, sammc.FixedPointConstraint() );
      model.add( resource, sammc.integer(),
            serializeTypedValue( fixedPointConstraint.getInteger().toString(), ExtendedXsdDataType.POSITIVE_INTEGER ) );
      model.add( resource, sammc.scale(),
            serializeTypedValue( fixedPointConstraint.getScale().toString(), ExtendedXsdDataType.POSITIVE_INTEGER ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitCode( final Code code, final ModelElement context ) {
      final Model model = createCharacteristicsModel( code );
      final Resource resource = getElementResource( code );
      model.add( resource, RDF.type, sammc.Code() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitDuration( final Duration duration, final ModelElement context ) {
      final Model model = createCharacteristicsModel( duration );
      final Resource resource = getElementResource( duration );
      model.add( resource, RDF.type, sammc.Duration() );

      getUnitStatement( duration, resource ).ifPresent( model::add );
      duration.getUnit().map( unit -> unit.accept( this, duration ) ).ifPresent( elementModel -> model.add( elementModel.getModel() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEither( final Either either, final ModelElement context ) {
      final Model model = createCharacteristicsModel( either );
      final Resource resource = getElementResource( either );
      model.add( resource, RDF.type, sammc.Either() );
      final ElementModel left = either.getLeft().accept( this, either );
      left.getFocusElement().ifPresent( leftCharacteristic -> model.add( resource, sammc.left(), leftCharacteristic ) );
      model.add( left.getModel() );
      final ElementModel right = either.getRight().accept( this, either );
      right.getFocusElement().ifPresent( rightCharacteristic -> model.add( resource, sammc.right(), rightCharacteristic ) );
      model.add( right.getModel() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEnumeration( final Enumeration enumeration, final ModelElement context ) {
      final Model model = createCharacteristicsModel( enumeration );
      final Resource resource = getElementResource( enumeration );
      if ( !(enumeration.is( State.class )) ) {
         model.add( resource, RDF.type, sammc.Enumeration() );
      }

      final List<RDFNode> elements = enumeration.getValues().stream().flatMap( value -> {
         final ElementModel valueElementModel = value.accept( this, enumeration );
         model.add( valueElementModel.getModel() );
         return valueElementModel.getFocusElement().stream();
      } ).toList();
      model.add( resource, sammc.values(), model.createList( elements.iterator() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEntityInstance( final EntityInstance instance, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( instance );
      model.add( resource, RDF.type, getElementResource( instance.getEntityType() ) );
      instance.getAssertions().forEach( ( key, value ) -> {
         final org.apache.jena.rdf.model.Property property = ResourceFactory.createProperty(
               key.getAspectModelUrn().orElseThrow().toString() );
         final ElementModel valueElementModel = value.accept( this, instance );
         model.add( valueElementModel.getModel() );
         valueElementModel.getFocusElement().ifPresent( elementValue -> model.add( resource, property, elementValue ) );
      } );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitScalarValue( final ScalarValue value, final ModelElement context ) {
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
      return new ElementModel( model,
            Optional.of( ResourceFactory.createTypedLiteral( targetType.get().unparse( value.getValue() ), targetType.get() ) ) );
   }

   @Override
   public ElementModel visitCollectionValue( final CollectionValue collection, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      final List<RDFNode> elements = collection.getValues().stream().flatMap( value -> {
         final ElementModel valueElementModel = value.accept( this, collection );
         model.add( valueElementModel.getModel() );
         return valueElementModel.getFocusElement().stream();
      } ).toList();
      final RDFNode rdfList = model.createList( elements.iterator() );
      return new ElementModel( model, Optional.of( rdfList ) );
   }

   @Override
   public ElementModel visitState( final State state, final ModelElement context ) {
      final Model model = visitEnumeration( state, null ).getModel();
      final Resource resource = getElementResource( state );
      model.add( resource, RDF.type, sammc.State() );
      final ElementModel defaultValueElementModel = state.getDefaultValue().accept( this, state );
      model.add( defaultValueElementModel.getModel() );
      defaultValueElementModel.getFocusElement().ifPresent( defaultValue -> model.add( resource, sammc.defaultValue(), defaultValue ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   private Optional<Statement> getUnitStatement( final Quantifiable elementWithUnit, final Resource targetResource ) {
      return elementWithUnit.getUnit()
            .flatMap( NamedElement::getAspectModelUrn )
            .map( AspectModelUrn::toString )
            .map( unitUrn -> createStatement( targetResource, sammc.unit(),
                  createResource( unitUrn ) ) );
   }

   @Override
   public ElementModel visitMeasurement( final Measurement measurement, final ModelElement context ) {
      final Model model = createCharacteristicsModel( measurement );
      final Resource resource = getElementResource( measurement );
      model.add( resource, RDF.type, sammc.Measurement() );

      measurement.getUnit().ifPresent( unit -> {
         final ElementModel unitModel = unit.accept( this, measurement );
         model.add( unitModel.getModel() );
         unitModel.getFocusElement().ifPresent( unitResource -> model.add( resource, sammc.unit(), unitResource ) );
      } );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitQuantifiable( final Quantifiable quantifiable, final ModelElement context ) {
      final Model model = createCharacteristicsModel( quantifiable );
      final Resource resource = getElementResource( quantifiable );
      model.add( resource, RDF.type, sammc.Quantifiable() );

      quantifiable.getUnit().ifPresent( unit -> {
         final ElementModel unitModel = unit.accept( this, quantifiable );
         model.add( unitModel.getModel() );
         unitModel.getFocusElement().ifPresent( unitResource -> model.add( resource, sammc.unit(), unitResource ) );
      } );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitSingleEntity( final SingleEntity singleEntity, final ModelElement context ) {
      final Model model = createCharacteristicsModel( singleEntity );
      final Resource resource = getElementResource( singleEntity );
      model.add( resource, RDF.type, sammc.SingleEntity() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitStructuredValue( final StructuredValue structuredValue, final ModelElement context ) {
      final Model model = createCharacteristicsModel( structuredValue );
      final Resource resource = getElementResource( structuredValue );
      model.add( resource, RDF.type, sammc.StructuredValue() );

      model.add( resource, sammc.deconstructionRule(), serializePlainString( structuredValue.getDeconstructionRule() ) );
      final RDFList elementsList = model.createList(
            structuredValue.getElements().stream().map( element -> element instanceof String
                  ? serializePlainString( (String) element )
                  : getElementResource( (Property) element ) ).iterator() );
      model.add( resource, sammc.elements(), elementsList );

      structuredValue.getElements().stream()
            .filter( Property.class::isInstance )
            .map( Property.class::cast )
            .map( property -> property.accept( this, structuredValue ) )
            .forEach( elementModel -> model.add( elementModel.getModel() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitTrait( final Trait trait, final ModelElement context ) {
      final Model model = createCharacteristicsModel( trait, true );
      final Resource resource = getElementResource( trait );
      model.add( resource, RDF.type, sammc.Trait() );

      final Resource baseCharacteristicResource = getElementResource( trait.getBaseCharacteristic() );
      model.add( resource, sammc.baseCharacteristic(), baseCharacteristicResource );
      model.add( trait.getBaseCharacteristic().accept( this, trait ).getModel() );

      trait.getConstraints().forEach( constraint -> {
         final Resource constraintResource = getElementResource( constraint );
         model.add( resource, sammc.constraint(), constraintResource );
         model.add( constraint.accept( this, trait ).getModel() );
      } );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitAspect( final Aspect aspect, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( aspect );
      model.add( resource, RDF.type, samm.Aspect() );
      model.add( serializeDescriptions( resource, aspect ) );
      model.add( serializeProperties( resource, aspect ) );
      model.add( resource, samm.operations(), model.createList(
            aspect.getOperations().stream().map( this::getElementResource ).iterator() ) );
      aspect.getOperations().stream().map( operation -> operation.accept( this, aspect ) )
            .forEach( elementModel -> model.add( elementModel.getModel() ) );
      if ( !aspect.getEvents().isEmpty() ) {
         model.add( resource, samm.events(), model.createList(
               aspect.getEvents().stream().map( this::getElementResource ).iterator() ) );
         aspect.getEvents().stream().map( event -> event.accept( this, aspect ) )
               .forEach( elementModel -> model.add( elementModel.getModel() ) );
      }
      return new ElementModel( model, Optional.of( resource ) );
   }

   @SuppressWarnings( "squid:S3655" )
   @Override
   public ElementModel visitProperty( final Property property, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      if ( property.getExtends().isPresent() ) {
         final Property superProperty = property.getExtends().get();
         // The Property is an instantiation of an abstract Property:
         // [ samm:extends :superProperty ; samm:characteristic ... ]
         if ( !superProperty.getCharacteristic().equals( property.getCharacteristic() ) ) {
            final Resource propertyResource = createResource();
            final Resource superPropertyResource = getElementResource( superProperty );
            final ElementModel superPropertyElementModel = superProperty.accept( this, context );
            model.add( superPropertyElementModel.getModel() );
            property.getCharacteristic().ifPresent( characteristic -> {
               final Resource characteristicResource = getElementResource( characteristic );
               model.add( characteristic.accept( this, property ).getModel() );
               model.add( propertyResource, samm.characteristic(), characteristicResource );
               model.add( propertyResource, samm._extends(), superPropertyResource );
            } );
            return new ElementModel( model, Optional.of( propertyResource ) );
         }
      }

      if ( !isLocalElement( property ) ) {
         return new ElementModel( model, Optional.empty() );
      }

      final Resource resource = getElementResource( property );
      model.add( resource, RDF.type, samm.Property() );
      model.add( serializeDescriptions( resource, property ) );

      property.getExampleValue().ifPresent( exampleValue -> {
         final ElementModel exampleValueElementModel = exampleValue.accept( this, property );
         model.add( exampleValueElementModel.getModel() );
         exampleValueElementModel.getFocusElement().ifPresent( exampleValueNode ->
               model.add( resource, samm.exampleValue(), exampleValueNode ) );
      } );

      property.getCharacteristic().ifPresent( characteristic -> {
         final Resource characteristicResource = getElementResource( characteristic );
         model.add( characteristic.accept( this, property ).getModel() );
         model.add( resource, samm.characteristic(), characteristicResource );
      } );

      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitOperation( final Operation operation, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( operation );
      model.add( resource, RDF.type, samm.Operation() );
      model.add( serializeDescriptions( resource, operation ) );
      final List<Resource> inputProperties = operation.getInput().stream().map( this::getElementResource ).toList();
      model.add( resource, samm.input(), model.createList( inputProperties.iterator() ) );
      operation.getInput().stream().map( property -> property.accept( this, operation ) )
            .forEach( elementModel -> model.add( elementModel.getModel() ) );
      operation.getOutput().ifPresent( outputProperty -> model.add( resource, samm.output(), getElementResource( outputProperty ) ) );
      operation.getOutput().map( outputProperty -> outputProperty.accept( this, operation ) )
            .ifPresent( elementModel -> model.add( elementModel.getModel() ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitEvent( final Event event, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource resource = getElementResource( event );
      model.add( resource, RDF.type, samm.Event() );
      model.add( serializeDescriptions( resource, event ) );
      model.add( serializeParameters( resource, event ) );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitCharacteristic( final Characteristic characteristic, final ModelElement context ) {
      if ( !isLocalElement( characteristic ) ) {
         return new ElementModel( ModelFactory.createDefaultModel(),
               characteristic.getAspectModelUrn().map( urn -> createResource( urn.toString() ) ) );
      }
      final Model model = createCharacteristicsModel( characteristic );
      final Resource resource = getElementResource( characteristic );
      model.add( resource, RDF.type, samm.Characteristic() );
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitComplexType( final ComplexType complexType, final ModelElement context ) {
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
         model.add( createStatement( resource, samm._extends(), extendedTypeResource ) );
      }
      model.add( serializeProperties( resource, complexType ) );
      model.add( serializeDescriptions( resource, complexType ) );

      if ( complexType.isAbstractEntity() ) {
         model.add( createStatement( resource, RDF.type, samm.AbstractEntity() ) );
      } else {
         model.add( createStatement( resource, RDF.type, samm.Entity() ) );
      }
      return new ElementModel( model, Optional.of( resource ) );
   }

   @Override
   public ElementModel visitAbstractEntity( final AbstractEntity abstractEntity, final ModelElement context ) {
      if ( abstractEntity.getUrn().startsWith( samme.getNamespace() ) ) {
         return new ElementModel( ModelFactory.createDefaultModel(),
               abstractEntity.getAspectModelUrn().map( urn -> createResource( urn.toString() ) ) );
      }
      final Model model = visitComplexType( abstractEntity, context ).getModel();
      abstractEntity.getExtendingElements().forEach( complexType -> model.add( complexType.accept( this, complexType ).getModel() ) );
      return new ElementModel( model, Optional.empty() );
   }

   @Override
   public ElementModel visitUnit( final Unit unit, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource unitResource = getElementResource( unit );
      if ( !unitResource.getNameSpace().equals( unitNamespace.getNamespace() ) ) {
         // This is a unit defined in the scope of the Aspect model
         model.add( unitResource, RDF.type, samm.Unit() );
         unit.getQuantityKinds().forEach( quantityKind -> {
            final ElementModel quantityKindModel = quantityKind.accept( this, context );
            model.add( quantityKindModel.getModel() );
            quantityKindModel.getFocusElement().ifPresent( quantityKindResource ->
                  model.add( unitResource, samm.quantityKind(), quantityKindResource ) );
         } );
         model.add( serializeDescriptions( unitResource, unit ) );
         unit.getSymbol().ifPresent( symbol -> model.add( unitResource, samm.symbol(), serializePlainString( symbol ) ) );
         return new ElementModel( model, Optional.of( unitResource ) );
      }
      return new ElementModel( model, Optional.of( unitResource ) );
   }

   @Override
   public ElementModel visitQuantityKind( final QuantityKind quantityKind, final ModelElement context ) {
      final Model model = ModelFactory.createDefaultModel();
      final Resource quantityKindResource = getElementResource( quantityKind );
      if ( !quantityKindResource.getNameSpace().equals( unitNamespace.getNamespace() ) ) {
         if ( quantityKind.getAspectModelUrn().map( quantityKindUrn ->
               quantityKindUrn.getUrnPrefix().equals( unitNamespace.getNamespace() ) ).orElse( true ) ) {
            return new ElementModel( model, Optional.empty() );
         }
         model.add( quantityKindResource, RDF.type, samm.QuantityKind() );
         model.add( quantityKindResource, samm.preferredName(), ResourceFactory.createLangLiteral( quantityKind.getLabel(), "en" ) );
      }
      return new ElementModel( model, Optional.of( quantityKindResource ) );
   }

   @Override
   public Model apply( final Aspect aspect ) {
      return visitAspect( aspect, null ).getModel();
   }
}
