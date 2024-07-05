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

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.constraint.LanguageConstraint;
import org.eclipse.esmf.metamodel.constraint.impl.DefaultLanguageConstraint;
import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;

import org.apache.jena.rdf.model.Resource;

public class LanguageConstraintInstantiator extends Instantiator<LanguageConstraint> {
   public LanguageConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, LanguageConstraint.class );
   }

   @Override
   public LanguageConstraint apply( final Resource languageConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( languageConstraint );
      final String languageCode = attributeValue( languageConstraint, SammNs.SAMMC.languageCode() ).getString();
      return new DefaultLanguageConstraint( metaModelBaseAttributes, Locale.forLanguageTag( languageCode ) );
   }
}
