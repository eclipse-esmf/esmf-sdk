package org.eclipse.esmf.metamodel.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.junit.jupiter.api.Test;

class LanguageCollectorModelVisitorTest {

   @Test
   void testLanguageCollector() {
      final VersionedModel versionedModel = model( """
            @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.0.0#> .
            @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.0.0#> .
            @prefix : <urn:samm:test:0.0.1#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                        
            :Reference a samm:Aspect ;
               samm:preferredName "Reference"@en ;
               samm:description "Beschreibung."@de ;
               samm:properties ( :items ) ;
               samm:operations ( ) .
                        
            :items a samm:Property ;
               samm:preferredName "Items"@es ;
               samm:description "Items."@it ;
               samm:characteristic [
                  a samm-c:Set ;
                  samm:dataType :AEntity
               ] .
                        
            :AEntity a samm:Entity ;
               samm:preferredName "A name"@pt ;
               samm:preferredName "Ein Name"@de ;
               samm:properties ( :AProperty ) .
                        
            :AProperty a samm:Property ;
               samm:preferredName "Common Property"@en ;
               samm:description "Descriptif de l'objet"@fr ;
               samm:characteristic :ACharacteristic .
                        
            :ACharacteristic a samm-c:Code ;
               samm:dataType xsd:string .
            """ );
      final Aspect aspect = AspectModelLoader.getSingleAspectUnchecked( versionedModel );
      final Set<Locale> reachableLocales = new LanguageCollectorModelVisitor().visitAspect( aspect, new HashSet<>() );
      assertThat( reachableLocales ).containsExactlyInAnyOrder( Locale.ENGLISH, Locale.GERMAN, Locale.ITALIAN, Locale.FRENCH, Locale.forLanguageTag( "es" ),
            Locale.forLanguageTag( "pt" ) );
   }

   private VersionedModel model( final String ttlRepresentation ) {
      final Model model = ModelFactory.createDefaultModel();
      final InputStream in = new ByteArrayInputStream( ttlRepresentation.getBytes( StandardCharsets.UTF_8 ) );
      model.read( in, "", RDFLanguages.strLangTurtle );
      return new SammAspectMetaModelResourceResolver().mergeMetaModelIntoRawModel( model, KnownVersion.SAMM_2_0_0 )
            .get();
   }
}
