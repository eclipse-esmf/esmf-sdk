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

package org.eclipse.esmf;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;

/**
 * Specific violation formatter that does highlighting for processing violations
 */
public class HighlightingViolationFormatter extends ViolationFormatter {
   @Override
   public String visitProcessingViolation( final ProcessingViolation violation ) {
      if ( violation.cause() != null
            && violation.cause() instanceof final ModelResolutionException modelResolutionException
            && !modelResolutionException.getCheckedLocations().isEmpty() ) {

         final Map<AspectModelUrn, List<ModelResolutionException.LoadingFailure>> failuresByElement =
               modelResolutionException.getCheckedLocations()
                     .stream()
                     .collect( Collectors.groupingBy( ModelResolutionException.LoadingFailure::element ) );
         final AspectModelUrn element = failuresByElement.keySet().stream().sorted().findFirst().orElseThrow();
         final List<ModelResolutionException.LoadingFailure> loadingFailures = failuresByElement.get( element );
         final String message = loadingFailures.stream()
               .map( failure -> "- %s (%s)".formatted( failure.location(),
                     new JansiRdfSyntaxHighlighter().formatError( failure.description() ).getResult() ) )
               .collect( Collectors.joining( "\n",
                     "No file containing the definition of " + new JansiRdfSyntaxHighlighter().formatIri( element.toString() ).getResult()
                           + " could be resolved. Checked locations:\n", "" ) );

         if ( modelResolutionException.getCheckedLocations().size() > 1 ) {
            return "%s%n%n%s additional elements could not be resolved. Use --details for more information.%n".formatted(
                  message,
                  new JansiRdfSyntaxHighlighter().formatIri( "" + ( modelResolutionException.getCheckedLocations().size() - 1 ) )
                        .getResult() );
         }

         return message;
      }

      return super.visitProcessingViolation( violation );
   }
}
