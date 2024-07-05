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

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultFixedPointConstraint;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class FixedPointConstraintInstantiator extends Instantiator<FixedPointConstraint> {
   public FixedPointConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, FixedPointConstraint.class );
   }

   @Override
   public FixedPointConstraint apply( final Resource fixedPointConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( fixedPointConstraint );
      final Integer scale = attributeValue( fixedPointConstraint, SammNs.SAMMC.scale() ).getLiteral().getInt();
      final Integer integer = attributeValue( fixedPointConstraint, SammNs.SAMMC.integer() ).getLiteral().getInt();
      return new DefaultFixedPointConstraint( metaModelBaseAttributes, scale, integer );
   }
}
