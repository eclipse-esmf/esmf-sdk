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

package examples;

// tag::imports[]
import java.util.List;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import io.vavr.control.Try;
import java.io.File;
import org.junit.jupiter.api.Test;
// end::imports[]

public class ValidateAspectModel {
   @Test
   public void validate() {
      // tag::validate[]
      // Try<VersionedModel> as returned by the AspectModelResolver
      final Try<VersionedModel> tryModel = // ...
            // end::validate[]
            AspectModelResolver.loadAndResolveModel(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::validate[]

      final List<Violation> violations = new AspectModelValidator().validateModel( tryModel );
      if ( violations.isEmpty() ) {
         // Aspect Model is valid!
         return;
      }

      final String validationReport = new ViolationFormatter().apply( violations ); // <1>
      final String detailedReport = new DetailedViolationFormatter().apply( violations );

      class MyViolationVisitor implements Violation.Visitor<String> { // <2>
         // ...
         // end::validate[]
         @Override
         public String visit( final Violation violation ) {
            return null;
         }
         // tag::validate[]
      }

      // Turn the list of Violations into a list of custom descriptions
      final Violation.Visitor<String> visitor = new MyViolationVisitor();
      final List<String> result = violations.stream()
            .map( violation -> violation.accept( visitor ) )
            .toList();
      // end::validate[]
   }
}
