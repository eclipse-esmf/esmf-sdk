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

package io.openmanufacturing.sds.metamodel.loader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMMC;
import io.openmanufacturing.sds.aspectmodel.vocabulary.UNIT;
import io.openmanufacturing.sds.metamodel.ModelElement;
import io.openmanufacturing.sds.metamodel.loader.instantiator.AspectInstantiator;

public class ModelElementFactory extends AttributeValueRetriever {
   private final KnownVersion metaModelVersion;
   private final Model model;
   private final BAMMC bammc;
   private final UNIT unit;
   private final Map<Resource, Instantiator<?>> instantiators = new HashMap<>();

   public ModelElementFactory( final KnownVersion metaModelVersion, final Model model ) {
      super( new BAMM( metaModelVersion ) );
      this.metaModelVersion = metaModelVersion;
      this.model = model;
      bammc = new BAMMC( metaModelVersion );
      unit = new UNIT( metaModelVersion, bamm );
   }

   @SuppressWarnings( "unchecked" )
   public <T extends ModelElement> T create( final Class<T> clazz, final Resource modelElement ) {
      final Resource targetType = resourceType( modelElement );
      final Instantiator<T> instantiator = (Instantiator<T>) instantiators.computeIfAbsent( targetType, resource -> createInstantiator( clazz, resource ) );
      return instantiator.apply( modelElement );
   }

   @SuppressWarnings( { "unchecked", "squid:S00101" } ) // class parameter is required to fix return type
   private <T extends ModelElement> Instantiator<T> createInstantiator( final Class<T> clazz, final Resource targetType ) {
      Resource effectiveTargetType = targetType;
      try {
         if ( targetType.equals( bamm.AbstractProperty() ) ) {
            effectiveTargetType = bamm.Property();
         }
         final AspectModelUrn urn = AspectModelUrn.fromUrn( effectiveTargetType.getURI() );
         final String className = String.format( "%s.%sInstantiator", AspectInstantiator.class.getPackageName(), urn.getName() );
         return (Instantiator<T>) Class.forName( className ).getDeclaredConstructor( getClass() ).newInstance( this );
      } catch ( final Exception exception ) {
         throw new AspectLoadingException( "Aspect loading does not know type " + effectiveTargetType, exception );
      }
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
