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

package org.eclipse.esmf.metamodel.loader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import com.google.common.collect.Streams;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMMC;
import org.eclipse.esmf.aspectmodel.vocabulary.UNIT;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.ModelNamespace;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.Unit;
import org.eclipse.esmf.metamodel.impl.DefaultUnit;
import org.eclipse.esmf.metamodel.loader.instantiator.AbstractEntityInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.AspectInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.CharacteristicInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.CodeInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.CollectionInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.ConstraintInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.DurationInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.EitherInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.EncodingConstraintInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.EntityInstanceInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.EntityInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.EnumerationInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.EventInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.FixedPointConstraintInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.LanguageConstraintInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.LengthConstraintInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.ListInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.LocaleConstraintInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.MeasurementInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.OperationInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.PropertyInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.QuantifiableInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.RangeConstraintInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.RegularExpressionConstraintInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.SetInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.SingleEntityInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.SortedSetInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.StateInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.StructuredValueInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.TimeSeriesInstantiator;
import org.eclipse.esmf.metamodel.loader.instantiator.TraitInstantiator;

import org.eclipse.esmf.metamodel.QuantityKinds;
import org.eclipse.esmf.metamodel.Units;

public class ModelElementFactory extends AttributeValueRetriever {
   private final KnownVersion metaModelVersion;
   private final Model model;
   private final SAMMC SAMMC;
   private final UNIT unit;
   private final Map<Resource, Instantiator<?>> instantiators = new HashMap<>();
   private final Map<Resource, ModelElement> loadedElements = new HashMap<>();
   private Set<ModelNamespace> namespaces;

   public ModelElementFactory( final KnownVersion metaModelVersion, final Model model, final Map<Resource, Instantiator<?>> additionalInstantiators ) {
      super( new SAMM( metaModelVersion ) );
      this.metaModelVersion = metaModelVersion;
      this.model = model;
      SAMMC = new SAMMC( metaModelVersion );
      unit = new UNIT( metaModelVersion, SAMM );

      registerInstantiator( SAMM.AbstractEntity(), new AbstractEntityInstantiator( this ) );
      registerInstantiator( SAMM.AbstractProperty(), new PropertyInstantiator( this ) );
      registerInstantiator( SAMM.Aspect(), new AspectInstantiator( this ) );
      registerInstantiator( SAMM.Characteristic(), new CharacteristicInstantiator( this ) );
      registerInstantiator( SAMM.Constraint(), new ConstraintInstantiator( this ) );
      registerInstantiator( SAMM.Entity(), new EntityInstantiator( this ) );
      registerInstantiator( SAMM.Event(), new EventInstantiator( this ) );
      registerInstantiator( SAMM.Operation(), new OperationInstantiator( this ) );
      registerInstantiator( SAMM.Property(), new PropertyInstantiator( this ) );

      registerInstantiator( SAMMC.Code(), new CodeInstantiator( this ) );
      registerInstantiator( SAMMC.Collection(), new CollectionInstantiator( this ) );
      registerInstantiator( SAMMC.Duration(), new DurationInstantiator( this ) );
      registerInstantiator( SAMMC.Either(), new EitherInstantiator( this ) );
      registerInstantiator( SAMMC.EncodingConstraint(), new EncodingConstraintInstantiator( this ) );
      registerInstantiator( SAMMC.Enumeration(), new EnumerationInstantiator( this ) );
      registerInstantiator( SAMMC.FixedPointConstraint(), new FixedPointConstraintInstantiator( this ) );
      registerInstantiator( SAMMC.LanguageConstraint(), new LanguageConstraintInstantiator( this ) );
      registerInstantiator( SAMMC.LengthConstraint(), new LengthConstraintInstantiator( this ) );
      registerInstantiator( SAMMC.List(), new ListInstantiator( this ) );
      registerInstantiator( SAMMC.LocaleConstraint(), new LocaleConstraintInstantiator( this ) );
      registerInstantiator( SAMMC.Measurement(), new MeasurementInstantiator( this ) );
      registerInstantiator( SAMMC.Quantifiable(), new QuantifiableInstantiator( this ) );
      registerInstantiator( SAMMC.RangeConstraint(), new RangeConstraintInstantiator( this ) );
      registerInstantiator( SAMMC.RegularExpressionConstraint(), new RegularExpressionConstraintInstantiator( this ) );
      registerInstantiator( SAMMC.Set(), new SetInstantiator( this ) );
      registerInstantiator( SAMMC.SingleEntity(), new SingleEntityInstantiator( this ) );
      registerInstantiator( SAMMC.SortedSet(), new SortedSetInstantiator( this ) );
      registerInstantiator( SAMMC.State(), new StateInstantiator( this ) );
      registerInstantiator( SAMMC.StructuredValue(), new StructuredValueInstantiator( this ) );
      registerInstantiator( SAMMC.TimeSeries(), new TimeSeriesInstantiator( this ) );
      registerInstantiator( SAMMC.Trait(), new TraitInstantiator( this ) );

      instantiators.putAll( additionalInstantiators );
   }

   private void registerInstantiator( final Resource resource, final Instantiator<?> instantiator ) {
      instantiators.put( resource, instantiator );
   }

   @SuppressWarnings( "unchecked" )
   public <T extends ModelElement> T create( final Class<T> clazz, final Resource modelElement ) {
      ModelElement element = loadedElements.get( modelElement );
      if ( element != null ) {
         return (T) element;
      }
      final Resource targetType = resourceType( modelElement );
      if ( SAMM.Unit().equals( targetType ) ) {
         return (T) findOrCreateUnit( modelElement );
      }
      if ( SAMM.QuantityKind().equals( targetType ) ) {
         return (T) findQuantityKind( modelElement );
      }
      final Instantiator<T> instantiator = (Instantiator<T>) instantiators.get( targetType );
      if ( instantiator != null ) {
         element = instantiator.apply( modelElement );
         loadedElements.put( modelElement, element );
         return (T) element;
      }

      // No generic instantiator could be found. This means the element is an entity instance
      if ( !model.contains( targetType, RDF.type, (RDFNode) null ) ) {
         throw new AspectLoadingException( "Could not load " + modelElement + ": Unknown type " + targetType );
      }
      final Entity entity = create( Entity.class, targetType );
      if ( entity == null ) {
         throw new AspectLoadingException( "Could not load " + modelElement + ": Expected " + targetType + " to be an Entity" );

      }
      return (T) new EntityInstanceInstantiator( this, entity ).apply( modelElement );
   }

   public QuantityKind findQuantityKind( final Resource quantityKindResource ) {
      return QuantityKinds.fromName( quantityKindResource.getLocalName() ).orElseThrow( () ->
            new AspectLoadingException( "QuantityKind " + quantityKindResource + " is invalid" ) );
   }

   public Unit findOrCreateUnit( final Resource unitResource ) {
      if ( unit.getNamespace().equals( unitResource.getNameSpace() ) ) {
         final AspectModelUrn unitUrn = AspectModelUrn.fromUrn( unitResource.getURI() );
         return Units.fromName( unitUrn.getName(), metaModelVersion ).orElseThrow( () ->
               new AspectLoadingException( "Unit definition for " + unitUrn + " is invalid" ) );
      }

      return new DefaultUnit(
            MetaModelBaseAttributes.fromModelElement( metaModelVersion, unitResource, model, SAMM ),
            optionalAttributeValue( unitResource, SAMM.symbol() ).map( Statement::getString ),
            optionalAttributeValue( unitResource, SAMM.commonCode() ).map( Statement::getString ),
            optionalAttributeValue( unitResource, SAMM.referenceUnit() ).map( Statement::getResource ).map( Resource::getLocalName ),
            optionalAttributeValue( unitResource, SAMM.conversionFactor() ).map( Statement::getString ),
            Streams.stream( model.listStatements( unitResource, SAMM.quantityKind(), (RDFNode) null ) )
                  .flatMap( quantityKindStatement -> QuantityKinds.fromName( quantityKindStatement.getObject().asResource().getLocalName() ).stream() )
                  .collect( Collectors.toSet() ) );
   }

   private Resource resourceType( final Resource resource ) {
      final Supplier<Optional<Resource>> directType = () ->
            optionalAttributeValue( resource, RDF.type ).map( Statement::getResource );
      final Supplier<Optional<Resource>> propertyUsageType = () ->
            optionalAttributeValue( resource, SAMM.property() ).map( statement -> resourceType( statement.getResource() ) );
      final Supplier<Optional<Resource>> subClassType = () ->
            optionalAttributeValue( resource, RDFS.subClassOf ).map( Statement::getResource ).map( this::resourceType );
      final Supplier<Optional<Resource>> extendsType = () ->
            optionalAttributeValue( resource, SAMM._extends() ).map( Statement::getResource ).map( this::resourceType );

      return Stream.of( directType, propertyUsageType, subClassType, extendsType )
            .map( Supplier::get )
            .filter( Optional::isPresent )
            .map( Optional::get )
            .findFirst()
            .orElseThrow( () -> new AspectLoadingException( "Resource " + resource + " has no type" ) );
   }

   protected KnownVersion getMetaModelVersion() {
      return metaModelVersion;
   }

   protected Model getModel() {
      return model;
   }

   protected SAMM getBamm() {
      return SAMM;
   }

   protected SAMMC getBammc() {
      return SAMMC;
   }

   public UNIT getUnit() {
      return unit;
   }
}
