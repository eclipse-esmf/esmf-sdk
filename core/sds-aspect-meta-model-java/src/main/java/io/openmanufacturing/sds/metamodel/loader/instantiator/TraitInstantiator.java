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

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.characteristic.Trait;
import io.openmanufacturing.sds.characteristic.impl.DefaultTrait;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class TraitInstantiator extends Instantiator<Trait> {
   public TraitInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Trait.class );
   }

   @Override
   public Trait apply( final Resource trait ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( trait );
      final Characteristic baseCharacteristic = modelElementFactory
            .create( Characteristic.class, attributeValue( trait, bammc.baseCharacteristic() ).getResource() );

      final List<Constraint> constraints =
            model.listStatements( trait, bammc.constraint(), (RDFNode) null ).mapWith( Statement::getResource )
                 .mapWith( constraintResource -> modelElementFactory.create( Constraint.class, constraintResource ) )
                 .toList();
      return new DefaultTrait( metaModelBaseAttributes, baseCharacteristic, constraints );
   }
}
