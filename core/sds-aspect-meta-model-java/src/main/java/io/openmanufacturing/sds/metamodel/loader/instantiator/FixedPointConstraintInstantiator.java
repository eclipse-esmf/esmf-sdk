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

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.metamodel.FixedPointConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultFixedPointConstraint;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class FixedPointConstraintInstantiator extends Instantiator<FixedPointConstraint> {
   public FixedPointConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, FixedPointConstraint.class );
   }

   @Override
   public FixedPointConstraint apply( final Resource fixedPointConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( fixedPointConstraint );
      final Integer scale = propertyValue( fixedPointConstraint, bammc.scale() ).getLiteral().getInt();
      final Integer integer = propertyValue( fixedPointConstraint, bammc.integer() ).getLiteral().getInt();
      return new DefaultFixedPointConstraint( metaModelBaseAttributes, scale, integer );
   }
}
