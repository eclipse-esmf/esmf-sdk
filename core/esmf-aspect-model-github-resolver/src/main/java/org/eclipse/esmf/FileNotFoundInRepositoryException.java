package org.eclipse.esmf;

import java.io.IOException;

public class FileNotFoundInRepositoryException extends RuntimeException {
   public FileNotFoundInRepositoryException( String message ) {
      super( message );
   }
}
