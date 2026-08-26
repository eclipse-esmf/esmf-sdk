/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.lsp.request;
import com.fasterxml.jackson.annotation.JsonValue;
/** Closed render outcome domain. */
public enum GraphicalViewRenderWarning {
   UNSUPPORTED_URI( "unsupportedUri" ), MISSING_DOCUMENT( "missingDocument" ), MODEL_TOO_LARGE( "modelTooLarge" ), TIMEOUT( "timeout" ), TEMPORARILY_UNRESOLVABLE( "temporarilyUnresolvable" );
   private final String wireValue; GraphicalViewRenderWarning( final String wireValue ) { this.wireValue = wireValue; }
   @JsonValue public String wireValue() { return wireValue; }
}
