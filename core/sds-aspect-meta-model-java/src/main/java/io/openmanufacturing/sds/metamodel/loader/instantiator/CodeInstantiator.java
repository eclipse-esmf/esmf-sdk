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

import io.openmanufacturing.sds.characteristic.Code;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.impl.DefaultCode;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class CodeInstantiator extends Instantiator<Code> {
   public CodeInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Code.class );
   }

   @Override
   public Code apply( final Resource code ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( code );
      final Type type = getType( code );
      return new DefaultCode( metaModelBaseAttributes, type );
   }
}
