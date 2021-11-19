package io.openmanufacturing.sds.aspectmodel.versionupdate.migrator;

import java.util.Map;
import java.util.Optional;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class SdsMetaModelVersionUriRewriter extends AbstractUriRewriter {

   public SdsMetaModelVersionUriRewriter( final KnownVersion sourceVersion, final KnownVersion targetVersion ) {
      super( sourceVersion, targetVersion );
   }

   @Override
   public int order() {
      return 50;
   }

   @Override
   protected Optional<String> rewriteUri( final String oldUri, final Map<String, String> oldToNewNamespaces ) {
      final String[] uriParts = oldUri.split( "#" );
      if ( uriParts.length == 1 ) {
         return Optional.empty();
      }
      return oldToNewNamespaces.keySet()
            .stream()
            .filter( key -> key.equals( uriParts[0] + "#" ) )
            .findAny()
            .map( key -> oldToNewNamespaces.get( key ) + uriParts[1] );
   }

   @Override
   public Optional<String> getDescription() {
      return Optional.of( String
            .format( "Change meta model version from %s to %s in URIs", getSourceKnownVersion().toVersionString(),
                  getTargetKnownVersion().toVersionString() ) );
   }
}
