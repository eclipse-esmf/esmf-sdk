package org.eclipse.esmf.aspectmodel.java.performance;

import java.net.URI;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

public interface ResolutionStrategyWithDefaultModelSource extends ResolutionStrategy {

   @Override
   public default Stream<URI> listContents() {
      return Stream.empty();
   }

   @Override
   default Stream<URI> listContentsForNamespace( final AspectModelUrn aspectModelUrn ) {
      return Stream.empty();
   }

   @Override
   default Stream<AspectModelFile> loadContents() {
      return Stream.empty();
   }

   @Override
   default Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn aspectModelUrn ) {
      return Stream.empty();
   }
}
