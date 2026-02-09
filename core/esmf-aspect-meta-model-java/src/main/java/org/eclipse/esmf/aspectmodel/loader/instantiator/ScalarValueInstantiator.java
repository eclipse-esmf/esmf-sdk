/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;

import org.apache.jena.rdf.model.Resource;

public class ScalarValueInstantiator extends Instantiator<ScalarValue> {
   public ScalarValueInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, ScalarValue.class );
   }

   @Override
   public ScalarValue apply( final Resource value ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( value );
      return new DefaultScalarValue( metaModelBaseAttributes, value, new DefaultScalar( value.getURI() ) );
   }
}
