package org.eclipse.esmf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestModel;

import org.junit.jupiter.api.Test;

class GitHubStrategyTest {

   @Test
   void testGithubStrategy() {
      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#Aspect" );

      final ResolutionStrategy gitHubStrategy = new GitHubStrategy( "eclipse-esmf/esmf-sdk",
            "core/esmf-test-aspect-models/src/main/resources/valid/org.eclipse.esmf.test/1.0.0" );

      final AspectModel result = new AspectModelLoader( gitHubStrategy ).load( testUrn );
      assertThat( result.files() ).hasSize( 1 );
      assertThat( result.elements() ).hasSize( 1 );
      assertThat( result.aspect().getName() ).isEqualTo( "Aspect" );
      assertThat( result.aspect().urn() ).isEqualTo( testUrn );
   }
}
