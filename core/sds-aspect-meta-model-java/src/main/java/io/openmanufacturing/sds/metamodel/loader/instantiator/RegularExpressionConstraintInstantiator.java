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

import io.openmanufacturing.sds.metamodel.RegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultRegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class RegularExpressionConstraintInstantiator extends Instantiator<RegularExpressionConstraint> {
   public RegularExpressionConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, RegularExpressionConstraint.class );
   }

   @Override
   public RegularExpressionConstraint apply( final Resource regularExpressionConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( regularExpressionConstraint );
      final String value = propertyValue( regularExpressionConstraint, bamm.value() ).getString();
      return new DefaultRegularExpressionConstraint( metaModelBaseAttributes, value );
   }
}
