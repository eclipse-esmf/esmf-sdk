package org.eclipse.esmf.aspectmodel.generator.jsonld;

import java.io.IOException;

import com.github.jsonldjava.core.JsonLdError;
import org.junit.jupiter.api.Test;

public class AspectModelJsonLdGeneratorTest {

   @Test
   void generateTest() throws JsonLdError, IOException {
      AspectModelToJsonLdGenerator aspectModelToJsonLdGenerator = new AspectModelToJsonLdGenerator();
      aspectModelToJsonLdGenerator.generate();
   }
}
