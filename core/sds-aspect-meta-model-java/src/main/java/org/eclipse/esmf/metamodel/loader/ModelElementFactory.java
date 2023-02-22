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

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.BAMM;
import org.eclipse.esmf.aspectmodel.vocabulary.BAMMC;
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

import io.openmanufacturing.sds.metamodel.QuantityKinds;
import io.openmanufacturing.sds.metamodel.Units;

public class ModelElementFactory extends AttributeValueRetriever {
   private final KnownVersion metaModelVersion;
   private final Model model;
   private final BAMMC bammc;
   private final UNIT unit;
   private final Map<Resource, Instantiator<?>> instantiators = new HashMap<>();
   private final Map<Resource, ModelElement> loadedElements = new HashMap<>();
   private Set<ModelNamespace> namespaces;

   public ModelElementFactory( final KnownVersion metaModelVersion, final Model model, final Map<Resource, Instantiator<?>> additionalInstantiators ) {
      super( new BAMM( metaModelVersion ) );
      this.metaModelVersion = metaModelVersion;
      this.model = model;
      bammc = new BAMMC( metaModelVersion );
      unit = new UNIT( metaModelVersion, bamm );

      registerInstantiator( bamm.AbstractEntity(), new AbstractEntityInstantiator( this ) );
      registerInstantiator( bamm.AbstractProperty(), new PropertyInstantiator( this ) );
      registerInstantiator( bamm.Aspect(), new AspectInstantiator( this ) );
      registerInstantiator( bamm.Characteristic(), new CharacteristicInstantiator( this ) );
      registerInstantiator( bamm.Constraint(), new ConstraintInstantiator( this ) );
      registerInstantiator( bamm.Entity(), new EntityInstantiator( this ) );
      registerInstantiator( bamm.Event(), new EventInstantiator( this ) );
      registerInstantiator( bamm.Operation(), new OperationInstantiator( this ) );
      registerInstantiator( bamm.Property(), new PropertyInstantiator( this ) );

      registerInstantiator( bammc.Code(), new CodeInstantiator( this ) );
      registerInstantiator( bammc.Collection(), new CollectionInstantiator( this ) );
      registerInstantiator( bammc.Duration(), new DurationInstantiator( this ) );
      registerInstantiator( bammc.Either(), new EitherInstantiator( this ) );
      registerInstantiator( bammc.EncodingConstraint(), new EncodingConstraintInstantiator( this ) );
      registerInstantiator( bammc.Enumeration(), new EnumerationInstantiator( this ) );
      registerInstantiator( bammc.FixedPointConstraint(), new FixedPointConstraintInstantiator( this ) );
      registerInstantiator( bammc.LanguageConstraint(), new LanguageConstraintInstantiator( this ) );
      registerInstantiator( bammc.LengthConstraint(), new LengthConstraintInstantiator( this ) );
      registerInstantiator( bammc.List(), new ListInstantiator( this ) );
      registerInstantiator( bammc.LocaleConstraint(), new LocaleConstraintInstantiator( this ) );
      registerInstantiator( bammc.Measurement(), new MeasurementInstantiator( this ) );
      registerInstantiator( bammc.Quantifiable(), new QuantifiableInstantiator( this ) );
      registerInstantiator( bammc.RangeConstraint(), new RangeConstraintInstantiator( this ) );
      registerInstantiator( bammc.RegularExpressionConstraint(), new RegularExpressionConstraintInstantiator( this ) );
      registerInstantiator( bammc.Set(), new SetInstantiator( this ) );
      registerInstantiator( bammc.SingleEntity(), new SingleEntityInstantiator( this ) );
      registerInstantiator( bammc.SortedSet(), new SortedSetInstantiator( this ) );
      registerInstantiator( bammc.State(), new StateInstantiator( this ) );
      registerInstantiator( bammc.StructuredValue(), new StructuredValueInstantiator( this ) );
      registerInstantiator( bammc.TimeSeries(), new TimeSeriesInstantiator( this ) );
      registerInstantiator( bammc.Trait(), new TraitInstantiator( this ) );

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
      if ( bamm.Unit().equals( targetType ) ) {
         return (T) findOrCreateUnit( modelElement );
      }
      if ( bamm.QuantityKind().equals( targetType ) ) {
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
            MetaModelBaseAttributes.fromModelElement( metaModelVersion, unitResource, model, bamm ),
            optionalAttributeValue( unitResource, bamm.symbol() ).map( Statement::getString ),
            optionalAttributeValue( unitResource, bamm.commonCode() ).map( Statement::getString ),
            optionalAttributeValue( unitResource, bamm.referenceUnit() ).map( Statement::getResource ).map( Resource::getLocalName ),
            optionalAttributeValue( unitResource, bamm.conversionFactor() ).map( Statement::getString ),
            Streams.stream( model.listStatements( unitResource, bamm.quantityKind(), (RDFNode) null ) )
                  .flatMap( quantityKindStatement -> QuantityKinds.fromName( quantityKindStatement.getObject().asResource().getLocalName() ).stream() )
                  .collect( Collectors.toSet() ) );
   }

   private Resource resourceType( final Resource resource ) {
      final Supplier<Optional<Resource>> directType = () ->
            optionalAttributeValue( resource, RDF.type ).map( Statement::getResource );
      final Supplier<Optional<Resource>> propertyUsageType = () ->
            optionalAttributeValue( resource, bamm.property() ).map( statement -> resourceType( statement.getResource() ) );
      final Supplier<Optional<Resource>> subClassType = () ->
            optionalAttributeValue( resource, RDFS.subClassOf ).map( Statement::getResource ).map( this::resourceType );
      final Supplier<Optional<Resource>> extendsType = () ->
            optionalAttributeValue( resource, bamm._extends() ).map( Statement::getResource ).map( this::resourceType );

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

   protected BAMM getBamm() {
      return bamm;
   }

   protected BAMMC getBammc() {
      return bammc;
   }

   public UNIT getUnit() {
      return unit;
   }
}
