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

package org.eclipse.esmf.aspectmodel.generator.sql.databricks;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

public class DatabricksTestBase {
   protected String sql( final TestAspect testAspect, final DatabricksSqlGenerationConfig config ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      return aspect.accept( new AspectModelDatabricksDenormalizedSqlVisitor( config ),
            AspectModelDatabricksDenormalizedSqlVisitorContextBuilder.builder().build() );
   }

   protected String sql( final TestAspect testAspect ) {
      final DatabricksSqlGenerationConfig config = new DatabricksSqlGenerationConfig();
      return sql( testAspect, config );
   }
}
