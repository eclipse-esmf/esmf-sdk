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

import java.util.Optional;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import io.openmanufacturing.sds.metamodel.RangeConstraint;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;
import io.openmanufacturing.sds.metamodel.impl.DefaultRangeConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalar;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalarValue;
import io.openmanufacturing.sds.metamodel.loader.Instantiator;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.loader.ModelElementFactory;

@SuppressWarnings( "unused" ) // Instantiator is constructured via reflection from ModelElementFactory
public class RangeConstraintInstantiator extends Instantiator<RangeConstraint> {
   public RangeConstraintInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, RangeConstraint.class );
   }

   @Override
   public RangeConstraint apply( final Resource rangeConstraint ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( rangeConstraint );

      final Optional<ScalarValue> minValue = optionalPropertyValue( rangeConstraint, bammc.minValue() )
            .map( Statement::getLiteral )
            .map( literal -> new DefaultScalarValue( literal.getValue(),
                  new DefaultScalar( literal.getDatatypeURI(), metaModelBaseAttributes.getMetaModelVersion() ) ) );
      final Optional<ScalarValue> maxValue = optionalPropertyValue( rangeConstraint, bammc.maxValue() )
            .map( Statement::getLiteral )
            .map( literal -> new DefaultScalarValue( literal.getValue(),
                  new DefaultScalar( literal.getDatatypeURI(), metaModelBaseAttributes.getMetaModelVersion() ) ) );
      final BoundDefinition lowerBoundDefinition = getBoundDefinitionForRangeValue( minValue,
            bammc.lowerBoundDefinition(), rangeConstraint, BoundDefinition.AT_LEAST );
      final BoundDefinition upperBoundDefinition = getBoundDefinitionForRangeValue( maxValue,
            bammc.upperBoundDefinition(), rangeConstraint, BoundDefinition.AT_MOST );

      return new DefaultRangeConstraint( metaModelBaseAttributes, minValue, maxValue, lowerBoundDefinition, upperBoundDefinition );
   }

   /**
    * Retrieves the bound definition value for a given upper or lower bound.
    *
    * @param rangeValue the value given for either the lower or upper bound
    * @param boundDefinitionProperty the bound definition property for which the value will be retrieved
    * @param rangeConstraint the characteristic being processed
    * @param defaultBoundDefinitionValue the default value for the bound definition property
    * @return in case no value was given for the provided upper or lower bound, the default {@link BoundDefinition#OPEN}
    *       is returned.
    *       In case a value is given for the provided upper or lower bound and the model does not contain a value for
    *       the bound definition property, the provided default {@link BoundDefinition} is returned.
    *       In case a value is given for the provided upper or lower bound and the model does provide a value for the
    *       bound definition property, provided bound definition value is returned.
    */
   private BoundDefinition getBoundDefinitionForRangeValue( final Optional<ScalarValue> rangeValue,
         final Property boundDefinitionProperty, final Resource rangeConstraint,
         final BoundDefinition defaultBoundDefinitionValue ) {
      final Optional<String> valueForBoundDefinition = getValueForBoundDefinition( rangeConstraint, boundDefinitionProperty );
      if ( rangeValue.isPresent() && valueForBoundDefinition.isPresent() ) {
         return BoundDefinition.valueOf( valueForBoundDefinition.get() );
      }
      if ( rangeValue.isPresent() ) {
         return defaultBoundDefinitionValue;
      }
      return BoundDefinition.OPEN;
   }

   private Optional<String> getValueForBoundDefinition( final Resource rangeConstraint, final Property boundDefinitionProperty ) {
      return Optional.ofNullable( rangeConstraint.getProperty( boundDefinitionProperty ) )
            .map( Statement::getObject )
            .map( RDFNode::toString )
            .map( this::getBoundDefinitionValue );
   }

   /**
    * Retrieves the value of the bound definition from the given URN of the bound definition
    */
   private String getBoundDefinitionValue( final String boundDefinitionUrn ) {
      final int startIndexOfBoundDefinitionValue = boundDefinitionUrn.indexOf( '#' ) + 1;
      return boundDefinitionUrn.substring( startIndexOfBoundDefinitionValue );
   }
}
