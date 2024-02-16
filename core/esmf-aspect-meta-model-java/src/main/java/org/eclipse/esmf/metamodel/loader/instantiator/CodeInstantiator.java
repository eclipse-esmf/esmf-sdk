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

import org.eclipse.esmf.characteristic.Code;
import org.eclipse.esmf.characteristic.impl.DefaultCode;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

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
