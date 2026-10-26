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

package org.eclipse.esmf.aspectmodel;

import java.net.URI;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Signals that an Aspect Model file uses SAMM meta model terms that do not exist in the meta model
 * version the file declares, or that it mixes meta model namespaces of different versions.
 *
 * <p>
 * The inconsistency is detected while the file is loaded, before it is migrated to the latest meta
 * model version, because migration rewrites the meta model URIs and thereby removes the evidence.
 * Migration has no way to report a result, so the file is rejected instead. This applies to every
 * kind
 * of load, not only to loading on behalf of a validation.
 *
 * <p>
 * The exception carries one message per offending term. All problems found in the file are reported
 * together.
 */
public class MetaModelVersionException extends RuntimeException {
   private static final long serialVersionUID = 4021530871503920214L;

   public record Problem(
         String message,
         URI sourceLocation,
         RDFNode highlight
   ) {}

   private final List<Problem> problems;

   public MetaModelVersionException( final List<Problem> problems ) {
      this.problems = List.copyOf( problems );
   }

   /**
    * The messages describing the inconsistencies that were found in the file.
    *
    * @return the messages
    */
   public List<Problem> problems() {
      return problems;
   }
}
