/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.loader.instantiator;

import java.util.List;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultTrait;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class TraitInstantiator extends Instantiator<Trait> {
   public TraitInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Trait.class );
   }

   @Override
   public Trait apply( final Resource trait ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( trait );
      final Characteristic baseCharacteristic = modelElementFactory
            .create( Characteristic.class, attributeValue( trait, SammNs.SAMMC.baseCharacteristic() ).getResource() );

      final List<Constraint> constraints =
            model.listStatements( trait, SammNs.SAMMC.constraint(), (RDFNode) null ).mapWith( Statement::getResource )
                  .mapWith( constraintResource -> modelElementFactory.create( Constraint.class, constraintResource ) )
                  .toList();
      return new DefaultTrait( metaModelBaseAttributes, baseCharacteristic, constraints );
   }
}
