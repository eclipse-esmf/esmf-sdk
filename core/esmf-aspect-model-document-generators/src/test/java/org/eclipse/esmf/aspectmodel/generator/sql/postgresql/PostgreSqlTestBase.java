/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.sql.postgresql;

import org.eclipse.esmf.aspectmodel.generator.sql.DenormalizedSqlGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

public abstract class PostgreSqlTestBase {
   protected String applyGenerator( final Aspect aspect, final PostgreSqlGenerationConfig config ) {
      return new DenormalizedSqlGenerator<>( config, new PostgreSqlDialect( config ) ).apply( aspect, config ).getContent();
   }

   protected String sql( final TestAspect testAspect, final PostgreSqlGenerationConfig config ) {
      return applyGenerator( TestResources.load( testAspect ).aspect(), config );
   }

   protected String sql( final TestAspect testAspect ) {
      final PostgreSqlGenerationConfig config = new PostgreSqlGenerationConfig();
      return sql( testAspect, config );
   }
}
