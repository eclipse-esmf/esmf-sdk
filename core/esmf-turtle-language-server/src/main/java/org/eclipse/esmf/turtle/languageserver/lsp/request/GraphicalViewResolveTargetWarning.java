/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.lsp.request;
import com.fasterxml.jackson.annotation.JsonValue;
/** Closed target-resolution warning domain. */
public enum GraphicalViewResolveTargetWarning {
   NOT_FOUND( "notFound" ), AMBIGUOUS( "ambiguous" ), UNSUPPORTED_URI( "unsupportedUri" ), TEMPORARILY_UNRESOLVABLE( "temporarilyUnresolvable" );
   private final String wireValue; GraphicalViewResolveTargetWarning( final String wireValue ) { this.wireValue = wireValue; }
   @JsonValue public String wireValue() { return wireValue; }
}
