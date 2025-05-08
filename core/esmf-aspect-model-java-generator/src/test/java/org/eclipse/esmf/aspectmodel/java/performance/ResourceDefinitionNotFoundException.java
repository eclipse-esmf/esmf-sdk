package org.eclipse.esmf.aspectmodel.java.performance;

public class ResourceDefinitionNotFoundException extends RuntimeException {
   public ResourceDefinitionNotFoundException( final String strategyName, final String resource ) {
      super( String.format( "%s: definition for %s not found", strategyName, resource ) );
   }
}
