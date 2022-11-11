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

import java.util.Locale;

import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.constraint.LanguageConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultLanguageConstraint;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class LanguageConstraintInstantiator extends Instantiator<LanguageConstraint> {
   public LanguageConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, LanguageConstraint.class );
   }

   @Override
   public LanguageConstraint apply( final Resource languageConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( languageConstraint );
      final String languageCode = attributeValue( languageConstraint, bammc.languageCode() ).getString();
      return new DefaultLanguageConstraint( metaModelBaseAttributes, Locale.forLanguageTag( languageCode ) );
   }
}
