package org.eclipse.esmf.aspectmodel.resolver.fs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class ModelsRootTest {

   private static final File EMPTY_FILE = new File( "" );

   @Test
   void resolveByCanonicalPathShouldReturnFileWhenCanonicalPathMatches() throws Exception {
      Path testPath = Paths.get( "src/test/resources/resolve", "Aspect.ttl" ).toAbsolutePath();

      File result = invokeResolveByCanonicalPath( testPath );

      assertThat( result ).isEqualTo( testPath.toFile() );
   }

   @Test
   void resolveByCanonicalPathShouldReturnEmptyFileWhenCanonicalPathDoesNotMatch() throws Exception {
      Path invalidPath = Paths.get( "src/test/resources/resolve", "aspect.ttl" ).toAbsolutePath();

      File result = invokeResolveByCanonicalPath( invalidPath );

      assertThat( result ).isEqualTo( EMPTY_FILE );
   }

   @Test
   void resolveByCanonicalPathShouldReturnEmptyFileWhenIOExceptionOccurs() throws Exception {
      Path pathCausingIOException = Paths.get( "pathCausingIOException" );

      File result = invokeResolveByCanonicalPath( pathCausingIOException );

      assertThat( result ).isEqualTo( EMPTY_FILE );
   }

   private static File invokeResolveByCanonicalPath( Path path ) throws Exception {
      Method method = ModelsRoot.class.getDeclaredMethod( "resolveByCanonicalPath", Path.class );
      method.setAccessible( true );
      return (File) method.invoke( null, path );
   }
}