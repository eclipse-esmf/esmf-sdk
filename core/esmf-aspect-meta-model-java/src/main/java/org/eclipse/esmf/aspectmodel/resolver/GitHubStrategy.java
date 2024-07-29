package org.eclipse.esmf.aspectmodel.resolver;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.fs.ModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubStrategy implements ResolutionStrategy {
   private static final Logger LOG = LoggerFactory.getLogger( GitHubStrategy.class );
   protected final ModelsRoot modelsRoot;

   protected final String repositoryUrl;

   protected final String searchDirectory;

   public GitHubStrategy( final Path modelsRoot, final String repositoryUrl, final String searchDirectory ) {
      this( new StructuredModelsRoot( modelsRoot ), repositoryUrl, searchDirectory );
   }

   public GitHubStrategy( final ModelsRoot modelsRoot, final String repositoryUrl, final String searchDirectory ) {
      this.modelsRoot = modelsRoot;
      this.repositoryUrl = repositoryUrl;
      this.searchDirectory = searchDirectory; // "."
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport )
         throws ModelResolutionException {

      try {
         GitHub github = GitHub.connectAnonymously();

         GHRepository repository = github.getRepository( repositoryUrl );

         findTtlFiles( repository, searchDirectory );
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      return null;
   }

   public static void findTtlFiles( GHRepository repository, String path ) throws IOException {
      List<GHContent> contents = repository.getDirectoryContent( path );
      for ( GHContent content : contents ) {
         if ( content.isFile() && content.getName().endsWith( ".ttl" ) ) {
            System.out.println( "Found TTL file: " + content.getPath() );
         } else if ( content.isDirectory() ) {
            findTtlFiles( repository, content.getPath() );
         }
      }
   }
}
