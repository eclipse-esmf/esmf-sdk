/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.treesitterturtle;

import org.eclipse.esmf.Diagnostic;

public interface TurtleDiagnostic extends Diagnostic<Diagnostic.Code> {
   enum TurtleCode implements Diagnostic.Code {
      E0000( "No more info available" ),
      E0001( "Could not load document" ),
      E0002( "Document validation failed" ),
      E0003( "Syntax error" ),
      E0004( "Missing token" );

      private final String description;

      TurtleCode( final String description ) {
         this.description = description;
      }

      @Override
      public String code() {
         return name();
      }

      @Override
      public String description() {
         return description;
      }
   }
}
