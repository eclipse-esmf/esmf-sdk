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
import java.io.File;
import java.util.List;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.AspectModel;
// end::imports[]

import org.junit.jupiter.api.Test;

public class ValidateAspectModel {
   @Test
   public void validate() {
      // tag::validate[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::validate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::validate[]

      // tag::violations[]
      final List<Violation> violations = new AspectModelValidator().validateModel( aspectModel );
      if ( violations.isEmpty() ) {
         // Aspect Model is valid!
         return;
      } else {
         for (Violation violation : violations) {
            String errorCode = violation.errorCode();
            String message = violation.message();
            EvaluationContext context = violation.context();
            List<Fix> fixes = violation.fixes();
         }
      }
      // end::violations[]

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
