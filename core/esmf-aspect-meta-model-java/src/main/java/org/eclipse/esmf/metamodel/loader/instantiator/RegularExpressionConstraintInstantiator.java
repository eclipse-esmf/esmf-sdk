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

import org.apache.jena.rdf.model.Resource;

import org.eclipse.esmf.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.constraint.impl.DefaultRegularExpressionConstraint;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

public class RegularExpressionConstraintInstantiator extends Instantiator<RegularExpressionConstraint> {
   public RegularExpressionConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, RegularExpressionConstraint.class );
   }

   @Override
   public RegularExpressionConstraint apply( final Resource regularExpressionConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( regularExpressionConstraint );
      final String value = attributeValue( regularExpressionConstraint, SAMM.value() ).getString();
      return new DefaultRegularExpressionConstraint( metaModelBaseAttributes, value );
   }
}
