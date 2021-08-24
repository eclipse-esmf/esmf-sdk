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

import io.openmanufacturing.sds.metamodel.LocaleConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultLocaleConstraint;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class LocaleConstraintInstantiator extends Instantiator<LocaleConstraint> {
   public LocaleConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, LocaleConstraint.class );
   }

   @Override
   public LocaleConstraint apply( final Resource localeConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( localeConstraint );
      final String localeCode = propertyValue( localeConstraint, bammc.localeCode() ).getString();
      return new DefaultLocaleConstraint( metaModelBaseAttributes, Locale.forLanguageTag( localeCode ) );
   }
}
