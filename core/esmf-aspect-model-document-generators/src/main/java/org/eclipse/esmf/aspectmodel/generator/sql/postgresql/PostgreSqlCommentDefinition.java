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

import org.jspecify.annotations.NonNull;

/**
 * Comment literal for PostgreSQL
 *
 * @param comment the content of the comment
 */
public record PostgreSqlCommentDefinition(
      String comment
) {
   @Override
   public @NonNull String toString() {
      /*
       * Line-breaks are represented either literally, or as the escape sequence "\n". The latter can only
       * be used in PostgreSQL when the string is prefixed with "E" (for "escape"). The E-prefixed string
       * is called an "extended string literal" in PostgreSQL.
       * Single quotes either need to be escaped by a second single quote ('') or, if in extended
       * string literal, by a backslash (\'). Therefore, if the comment contains line
       * breaks or single quotes, we use an extended string literal to ensure that the comment is
       * correctly represented in SQL.
       */
      final boolean extendedString = comment.contains( "\n" ) || comment.contains( "'" );
      return ( extendedString ? "E" : "" ) + "'" + comment
            .replace( "\r\n", "\n" )
            .replace( "'", "\\'" )
            .replace( "\n", "\\n" )
            + "'";
   }
}
