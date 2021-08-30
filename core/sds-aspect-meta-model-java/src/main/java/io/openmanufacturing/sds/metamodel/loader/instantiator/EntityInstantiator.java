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

package io.openmanufacturing.sds.metamodel.loader.instantiator;

import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.impl.DefaultEntity;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class EntityInstantiator extends Instantiator<Entity> {
   public EntityInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Entity.class );
   }

   @Override
   public Entity apply( final Resource entity ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( entity );
      final List<Property> properties = getPropertiesModels( entity, bamm.properties() );
      final Optional<AspectModelUrn> refines = optionalPropertyValue( entity, bamm.refines() )
            .map( statement -> AspectModelUrn.fromUrn( statement.getResource().getURI() ) );
      return new DefaultEntity( metaModelBaseAttributes, properties, refines );
   }
}
