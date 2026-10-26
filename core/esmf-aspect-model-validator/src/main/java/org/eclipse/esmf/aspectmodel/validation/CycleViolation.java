/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.validation;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import org.eclipse.esmf.aspectmodel.ProjectInfo;
import org.eclipse.esmf.aspectmodel.ViolationCode;
import org.eclipse.esmf.aspectmodel.loader.TokenBasedElementFocussedViolation;
import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;

/**
 * Represents the violation of cycle rules: A cycle in a model (graph) is only allowed, when one of
 * the Properties on the cycle path is marked as optional
 *
 * @param path the path of properties in the model that form a cycle
 */
public record CycleViolation(
      List<Resource> path
) implements TokenBasedElementFocussedViolation {
   public static final String ERROR_CODE = "ERR_CYCLE";

   @Override
   public Code code() {
      return new ViolationCode( ERROR_CODE, ProjectInfo.esmfErrorCodeUrl( ERROR_CODE ) );
   }

   @Override
   public String message() {
      return "The Aspect Model contains a cycle. The cycle includes the following properties: "
            + path.stream().map( property -> property.getModel().shortForm( property.getURI() ) ).collect( Collectors.joining( " -> " ) );
   }

   @Override
   public RDFNode highlight() {
      return path().stream()
            .filter( property -> TokenRegistry.getToken( property.asNode() ).isPresent() )
            .map( resource -> resource.as( RDFNode.class ) )
            .findFirst()
            .orElse( path.getFirst() );
   }
}
