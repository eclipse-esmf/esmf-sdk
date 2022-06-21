/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator.diagram;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.function.Function;
import org.apache.jena.sparql.function.FunctionFactory;

/**
 * {@link FunctionFactory} for the {@link getElementName} SPARQL function.
 */
public class GetElementNameFunctionFactory implements FunctionFactory {

   private getElementName getElementNameFunction;

   /**
    * @param context the {@link Model} holding the Aspect model which is being processed by the SPARQL queries using the custom {@link getElementName}
    *                function.
    */
   public GetElementNameFunctionFactory( final Model context ) {
      this.getElementNameFunction = new getElementName( context );
   }

   @Override
   public Function create( String s ) {
      return getElementNameFunction;
   }
}
