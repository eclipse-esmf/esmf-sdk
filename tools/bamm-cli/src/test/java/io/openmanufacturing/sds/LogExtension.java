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

package io.openmanufacturing.sds;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JUnit Jupiter Log Extension that logs the names of the executed tests and the detailed stack traces on failure
 */
public class LogExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
   private static final Logger LOG = LoggerFactory.getLogger( LogExtension.class );

   @Override
   public void beforeTestExecution( final ExtensionContext context ) throws Exception {
      LOG.info( "Running " + context.getDisplayName() + "..." );
   }

   @Override
   public void afterTestExecution( final ExtensionContext context ) {
      final Optional<Throwable> executionException = context.getExecutionException();
      if ( executionException.isPresent() ) {
         LOG.info( "Exception was thrown in test {}", context.getDisplayName(), executionException.get() );
      } else {
         LOG.info( "        {}: success", context.getDisplayName() );
      }
   }
}
