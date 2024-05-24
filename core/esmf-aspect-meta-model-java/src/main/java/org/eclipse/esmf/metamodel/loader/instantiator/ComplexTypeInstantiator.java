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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.vocabulary.SammNs;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultAbstractEntity;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public abstract class ComplexTypeInstantiator<T extends ComplexType> extends Instantiator<T> {

   private final Set<Resource> processedExtendingElements = new HashSet<>();
   private final Map<Resource, T> creatingElements = new HashMap<>();

   public ComplexTypeInstantiator( final ModelElementFactory modelElementFactory, final Class<T> targetClass ) {
      super( modelElementFactory, targetClass );
   }

   /**
    * Initializes a {@link DefaultAbstractEntity}.
    * Since {@link ComplexType}s have a reference to the element which is extended by a particular {@link ComplexType},
    * and the {@link AbstractEntity} has a list of references to all elements which extend the {@link AbstractEntity},
    * a circular dependency exists between the {@link ComplexType} and the {@link AbstractEntity}.
    * The reference between the {@link ComplexType}s represents the `extends` relationship between an {@link Entity} or
    * an {@link AbstractEntity} and another {@link Entity} or {@link AbstractEntity}.
    * The list of references between an {@link AbstractEntity} and all {@link ComplexType}s extending the
    * {@link AbstractEntity} has been implemented to ensure that all extending {@link ComplexType}s are loaded into the
    * {@link Aspect} whether they are directly linked to that Aspect or not. This
    * scenario may occur for example when the Aspect Model contains a {@link Collection}
    * with an {@link AbstractEntity} as its data type with multiple Entities extending the {@link AbstractEntity}.
    */
   @Override
   public T apply( final Resource resource ) {
      if ( creatingElements.containsKey( resource ) ) {
         return creatingElements.get( resource );
      }

      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( resource );
      final List<Property> properties = getPropertiesModels( resource, SammNs.SAMM.properties() );
      final Optional<ComplexType> extendedEntity = getExtendedEntity( resource );
      final List<AspectModelUrn> extending = new ArrayList<>();
      final T entity = createDefaultEntity( metaModelBaseAttributes, properties, extendedEntity, extending );
      creatingElements.put( resource, entity );
      extending.addAll( getExtending( resource ) );
      return entity;
   }

   private List<AspectModelUrn> getExtending( final Resource resource ) {
      return model.listSubjectsWithProperty( SammNs.SAMM._extends(), resource )
            .mapWith( extendingComplexType -> attributeValue( extendingComplexType, RDF.type ) ).mapWith( statement -> {
               if ( processedExtendingElements.contains( statement.getSubject() ) ) {
                  return AspectModelUrn.fromUrn( statement.getSubject().getURI() );
               }
               processedExtendingElements.add( statement.getSubject() );
               if ( SammNs.SAMM.AbstractEntity().equals( statement.getObject().asResource() ) ) {
                  return modelElementFactory.create( AbstractEntity.class, statement.getSubject() ).urn();
               }
               return modelElementFactory.create( Entity.class, statement.getSubject() ).urn();
            } ).toList();
   }

   protected Optional<ComplexType> getExtendedEntity( final Resource resource ) {
      return optionalAttributeValue( resource, SammNs.SAMM._extends() )
            .map( Statement::getResource )
            .map( extendedEntityResource -> attributeValue( extendedEntityResource, RDF.type ) )
            .map( entityStatement -> {
               if ( SammNs.SAMM.AbstractEntity().equals( entityStatement.getObject().asResource() ) ) {
                  return modelElementFactory.create( AbstractEntity.class, entityStatement.getSubject() );
               }
               return modelElementFactory.create( Entity.class, entityStatement.getSubject() );
            } );
   }

   protected abstract T createDefaultEntity(
         MetaModelBaseAttributes metaModelBaseAttributes,
         List<Property> properties,
         Optional<ComplexType> extendedEntity,
         List<AspectModelUrn> extendingComplexTypes );
}
