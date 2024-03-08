package org.eclipse.esmf.aspectmodel.resolver.github;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;

public class GitHubResolutionStrategy implements ResolutionStrategy {
   private final StructuredModelsRoot root;

   public static void install() {
      AspectModelResolver.register( null, uri -> Try.of( () -> new GitHubResolutionStrategy( uri ) ) );
   }

   public GitHubResolutionStrategy( URI baseUri ) {
      if ( !isUrl( baseUri ) || !isGithubUrl( baseUri ) )
         throw new UnsupportedOperationException( "GitHub resolution strategy does not support URN: " + baseUri );
      else {
         this.root = new StructuredModelsRoot( transformGithubUrlToContentLink( baseUri ).mapTry( URL::toURI ).get() );
      }
   }

   @Override
   public Try<Model> apply( AspectModelUrn urn ) {
      return applyUri( root.determineAspectModelFile( urn ) );
   }

   @Override
   public Try<Model> applyUri( URI uri ) {
      return AspectModelUrn.from( uri ).flatMap( this::apply )
            .recoverWith( __ ->
                  toURL( uri ).flatMap( GitHubResolutionStrategy::loadModel )
                   )
            .orElse( Try.failure( new UnsupportedOperationException( "GitHub resolution strategy does not support URI: " + uri ) ) );
   }

   @Override
   public InputStream read( URI uri ) {
      return Try.success( uri )
            .filter( GitHubResolutionStrategy::isGithubUrl )
            .flatMap( GitHubResolutionStrategy::toURL )
            .flatMap( GitHubResolutionStrategy::read )
            .getOrElse( (InputStream) null );
   }

   static Try<URL> toURL( URI uri ) {
      if ( isGithubUrl( uri ) )
         return transformGithubUrlToContentLink( uri );
      else
         return Try.of( uri::toURL );
   }

   static boolean isUrl( URI uri ) {
      try {
         //noinspection ResultOfMethodCallIgnored
         uri.toURL();
         return true;
      } catch ( MalformedURLException e ) {
         return false;
      }
   }

   static boolean isGithubUrl( URI uri ) {
      try {
         String host = uri.toURL().getHost();
         return "github.com".equals( host )
               || "raw.githubusercontent.com".equals( host );
      } catch ( Exception e ) {
         return false;
      }
   }

   static Try<Model> loadModel( URL link ) {
      return read( link )
            .flatMap( TurtleLoader::loadTurtle );
   }

   static Try<InputStream> read( URL link ) {
      return Try.withResources( link::openStream )
            .of( InputStream::readAllBytes )
            .map( ByteArrayInputStream::new );
   }

   static Try<URL> transformGithubUrlToContentLink( URI githubUrl ) {
      String url = githubUrl.toString()
            .replace( "github.com", "raw.githubusercontent.com" )
            .replace( "/blob/", "/" );
      return Try.of( () -> new URL( url ) );
   }

   public String toString() {
      return "GitHubStrategy(root=" + root + ')';
   }
}
