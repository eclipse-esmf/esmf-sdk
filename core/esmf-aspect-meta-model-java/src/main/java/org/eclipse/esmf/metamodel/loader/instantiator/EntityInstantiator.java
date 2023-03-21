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

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

public class EntityInstantiator extends Instantiator<Entity> {
   public EntityInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Entity.class );
   }

   @Override
   public Entity apply( final Resource entity ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( entity );
      final List<Property> properties = getPropertiesModels( entity, samm.properties() );

      final Optional<ComplexType> extendedEntity = optionalAttributeValue( entity, samm._extends() )
            .map( Statement::getResource )
            .map( extendedEntityResource -> attributeValue( extendedEntityResource, RDF.type ) )
            .map( entityStatement -> {
               if ( samm.AbstractEntity().equals( entityStatement.getObject().asResource() ) ) {
                  return modelElementFactory.create( AbstractEntity.class, entityStatement.getSubject() );
               }
               return modelElementFactory.create( Entity.class, entityStatement.getSubject() );
            } );
      return DefaultEntity.createDefaultEntity( metaModelBaseAttributes, properties, extendedEntity );
   }
}