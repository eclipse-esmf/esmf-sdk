package org.eclipse.esmf.aspectmodel.generator.jsonld;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.github.jsonldjava.core.JsonLdError;
import org.junit.jupiter.api.Test;

class AspectModelJsonLdGeneratorTest {

   @Test
   void generateTest() throws JsonLdError {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_ENTITY_LIST ).aspect();
      final AspectModelToJsonLdGenerator jsonGenerator = new AspectModelToJsonLdGenerator( aspect );
      final String generatedJsonLd = jsonGenerator.generateJsonLd();

      assertJsonLdMeta( generatedJsonLd, aspect.urn() );
   }

   private void assertJsonLdMeta( final String generatedJsonLd, final AspectModelUrn aspectModelUrn ) {
      final String context = """
            "@context": {
                    "xsd": "http://www.w3.org/2001/XMLSchema#",
                    "samm-c": "urn:samm:org.eclipse.esmf.samm:characteristic:2.1.0#",
                    "samm": "urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#",
                    "@vocab": "urn:samm:org.eclipse.esmf.test:1.0.0#"
                }
            """;

      assertThat( generatedJsonLd ).contains( "\"@graph\": [" );
      assertThat( generatedJsonLd ).contains( context );
      assertThat( generatedJsonLd ).contains( "\"xsd\": \"http://www.w3.org/2001/XMLSchema#\"," );
      assertThat( generatedJsonLd ).contains( String.format( "\"@id\": \"%s\"", aspectModelUrn.toString() ) );
   }
}
