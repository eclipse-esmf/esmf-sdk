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

package org.eclipse.esmf.aspectmodel.shacl.fix;

import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;

/**
 * A fix that can be applied to a violation to remove it
 */
public interface Fix {
   EvaluationContext context();

   String description();

   <T> T accept( Visitor<T> visitor );

   interface Visitor<T> {
      T visit( Fix fix );

      default T visitReplaceValue( final ReplaceValue replaceValue ) {
         return visit( replaceValue );
      }
   }
}

