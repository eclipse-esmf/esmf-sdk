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

import java.math.BigInteger;
import java.util.Optional;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import io.openmanufacturing.sds.constraint.LengthConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultLengthConstraint;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

public class LengthConstraintInstantiator extends Instantiator<LengthConstraint> {
   public LengthConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, LengthConstraint.class );
   }

   @Override
   public LengthConstraint apply( final Resource lengthConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( lengthConstraint );
      final Optional<BigInteger> minValue = optionalAttributeValue( lengthConstraint, bammc.minValue() )
            .map( Statement::getLiteral ).map( Literal::getLong ).map( BigInteger::valueOf );
      final Optional<BigInteger> maxValue = optionalAttributeValue( lengthConstraint, bammc.maxValue() )
            .map( Statement::getLiteral ).map( Literal::getLong ).map( BigInteger::valueOf );
      return new DefaultLengthConstraint( metaModelBaseAttributes, minValue, maxValue );
   }
}
