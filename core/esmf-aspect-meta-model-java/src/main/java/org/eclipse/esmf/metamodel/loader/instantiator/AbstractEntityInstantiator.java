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

package org.eclipse.esmf.metamodel.loader.instantiator;

import java.util.*;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultAbstractEntity;
import org.eclipse.esmf.metamodel.impl.DefaultComplexType;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

public class AbstractEntityInstantiator extends Instantiator<AbstractEntity> {

   private final Set<Resource> processedExtendingElements = new HashSet<>();
   private final Map<Resource, AbstractEntity> creatingElements = new HashMap<>();

   public AbstractEntityInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, AbstractEntity.class );
   }

   /**
    * Initializes a {@link DefaultAbstractEntity}.
    * Since {@link ComplexType}s have a reference to the element which is extended by a particular {@link ComplexType},
    * and the {@link AbstractEntity} has a list of references to all elements which extend the {@link AbstractEntity},
    * a circular dependency exists between the {@link ComplexType} and the {@link AbstractEntity}.
    *
    * The reference between the {@link ComplexType}s represents the `extends` relationship between an {@link Entity} or
    * an {@link AbstractEntity} and another {@link Entity} or {@link AbstractEntity}.
    * The list of references between an {@link AbstractEntity} and all {@link ComplexType}s extending the
    * {@link AbstractEntity} has been implemented to ensure that all extending {@link ComplexType}s are loaded into the
    * {@link Aspect} whether they are directly linked to that Aspect or not. This
    * scenario may occur for example when the Aspect Model contains a {@link Collection}
    * with an {@link AbstractEntity} as its data type with multiple Entities extending the {@link AbstractEntity}.
    *
    * In order to prevent processing elements in this circular dependency more than once, causing an infinite loop,
    * the elements which are processed are tracked in the {@link AbstractEntityInstantiator#processedExtendingElements}
    * {@link Set}.
    * Using the {@link DefaultComplexType#instances} Map to check whether an
    * element has been processed does not work since the instance is only created once the `create` method of the
    * corresponding {@link ComplexType} has been called. Creating the child elements however happens before this call.
    */
   @Override
   public AbstractEntity apply( final Resource abstractEntity ) {
      if ( creatingElements.containsKey( abstractEntity ) )
         return creatingElements.get( abstractEntity );
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( abstractEntity );
      final List<Property> properties = getPropertiesModels( abstractEntity, samm.properties() );

      final Optional<ComplexType> extendedEntity = optionalAttributeValue( abstractEntity, samm._extends() ).map( Statement::getResource )
            .map( extendedEntityResource -> attributeValue( extendedEntityResource, RDF.type ) ).map( entityStatement -> {
               if ( samm.AbstractEntity().equals( entityStatement.getObject().asResource() ) ) {
                  return modelElementFactory.create( AbstractEntity.class, entityStatement.getSubject() );
               }
               return modelElementFactory.create( Entity.class, entityStatement.getSubject() );
            } );

      final List<AspectModelUrn> extendingComplexTypes = new ArrayList<>();
      DefaultAbstractEntity entity = DefaultAbstractEntity.createDefaultAbstractEntity( metaModelBaseAttributes, properties, extendedEntity,
            extendingComplexTypes );
      creatingElements.put( abstractEntity, entity );
      extendingComplexTypes.addAll( model.listSubjectsWithProperty( samm._extends(), abstractEntity )
            .mapWith( extendingComplexType -> attributeValue( extendingComplexType, RDF.type ) ).mapWith( statement -> {
               if ( processedExtendingElements.contains( statement.getSubject() ) ) {
                  return AspectModelUrn.fromUrn( statement.getSubject().getURI() );
               }
               processedExtendingElements.add( statement.getSubject() );
               if ( samm.AbstractEntity().equals( statement.getObject().asResource() ) ) {
                  return modelElementFactory.create( AbstractEntity.class, statement.getSubject() ).getAspectModelUrn().get();
               }
               return modelElementFactory.create( Entity.class, statement.getSubject() ).getAspectModelUrn().get();
            } ).toList() );
      creatingElements.remove( abstractEntity );
      return entity;
   }
}
