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
package org.eclipse.esmf.turtle.languageserver.graphical.protocol;
import org.eclipse.lsp4j.Location;
/** Fresh source location, or one controlled warning. */
public record GraphicalViewResolveTargetResult( Location location, GraphicalViewResolveTargetWarning warning ) {
   public static GraphicalViewResolveTargetResult warning( final GraphicalViewResolveTargetWarning warning ) { return new GraphicalViewResolveTargetResult( null, warning ); }
}
