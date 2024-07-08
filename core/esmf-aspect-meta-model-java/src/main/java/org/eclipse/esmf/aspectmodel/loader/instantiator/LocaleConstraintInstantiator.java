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

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.constraint.LocaleConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultLocaleConstraint;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.rdf.model.Resource;

public class LocaleConstraintInstantiator extends Instantiator<LocaleConstraint> {
   public LocaleConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, LocaleConstraint.class );
   }

   @Override
   public LocaleConstraint apply( final Resource localeConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( localeConstraint );
      final String localeCode = attributeValue( localeConstraint, SammNs.SAMMC.localeCode() ).getString();
      return new DefaultLocaleConstraint( metaModelBaseAttributes, Locale.forLanguageTag( localeCode ) );
   }
}
