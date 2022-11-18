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

import java.nio.charset.Charset;

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.constraint.EncodingConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultEncodingConstraint;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class EncodingConstraintInstantiator extends Instantiator<EncodingConstraint> {
   public EncodingConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, EncodingConstraint.class );
   }

   @Override
   public EncodingConstraint apply( final Resource encodingConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( encodingConstraint );
      final String value = encodingConstraint.getProperty( bamm.value() ).getObject().toString();
      final String encoding = value.substring( value.indexOf( '#' ) + 1 );
      return new DefaultEncodingConstraint( metaModelBaseAttributes, Charset.forName( encoding ) );
   }
}
