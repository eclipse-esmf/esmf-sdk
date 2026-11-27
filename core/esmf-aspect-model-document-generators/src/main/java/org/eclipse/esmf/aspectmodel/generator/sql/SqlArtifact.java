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

package org.eclipse.esmf.aspectmodel.generator.sql;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.LocalizedArtifact;
import org.eclipse.esmf.aspectmodel.generator.StringArtifact;

/**
 * Represents a generated SQL script.
 */
public class SqlArtifact extends StringArtifact implements LocalizedArtifact {
   private final Locale language;

   public <U extends SqlGenerationConfig.DialectSpecificConfig<D, C, X>,
         D extends SqlType,
         C extends SqlColumnDefinition<D, C>,
         T extends SqlTableDefinition<D, C, X>,
         X extends SqlConstraint> SqlArtifact( final String id,
               final List<T> tables, final U config, final SqlDialect<U, D, C, T, X> dialect ) {
      super( id, tables.stream().map( tableDefinition -> dialect.renderTableDefinition( tableDefinition, config ) )
            .collect( Collectors.joining( "\n" ) ) );
      language = config.commentLanguage();
   }

   @Override
   public Locale language() {
      return language;
   }
}
