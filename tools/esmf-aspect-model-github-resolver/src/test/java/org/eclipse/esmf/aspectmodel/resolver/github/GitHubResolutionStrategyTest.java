package org.eclipse.esmf.aspectmodel.resolver.github;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;
import org.eclipse.esmf.test.TestResources;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import io.vavr.control.Try;

class GitHubResolutionStrategyTest {

   private static final String GITHUB_URL = "https://github.com/eclipse-esmf/esmf-semantic-aspect-meta-model/blob/main/esmf-semantic-aspect-meta-model/"
         + "src/test/resources/samm_2_0_0/aspect-shape/";
   private static final String GITHUB_CTX_URL = GitHubResolutionStrategy.transformGithubUrlToContentLink( URI.create( GITHUB_URL ) ).get().toString();
   private static final String GITHUB_FILE = "org.eclipse.esmf.test/1.0.0/TestAspect.ttl";
   private static final AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn(
         "urn:samm:org.eclipse.esmf.test:1.0.0#TestAspect" );

   private static String testModelContent;
   private URL testUrl;

   private GitHubResolutionStrategy strategy;

   @BeforeAll
   static void beforeAll() {
      TestModel testModel = TestAspect.ASPECT_WITH_ENTITY;
      testModelContent = TestModel.modelToString( TestResources.getModelWithoutResolution( testModel, KnownVersion.getLatest() ).getRawModel() );
   }

   @BeforeEach
   void setUp() throws IOException {
      testUrl = mock( URL.class );
      when( testUrl.getHost() ).thenReturn( "github.com" );
      //      when( testUrl.toURI() ).thenReturn( URI.create( GITHUB_URL + GITHUB_FILE )
      when( testUrl.openStream() ).then( __ -> IOUtils.toInputStream( testModelContent, Charset.defaultCharset() ) );

      strategy = new GitHubResolutionStrategy( URI.create( GITHUB_URL + GITHUB_FILE ) );
   }

   @Test
   void applyReturnsModelWhenRootIsNotNullAndUrnIsSupported() {
      try ( var __ = overrideGitHubUrlTransformation() ) {

         Try<Model> result = strategy.apply( aspectModelUrn );

         assertThat( result.isSuccess() ).as( result.toString() ).isTrue();
      }
   }

   @Test
   void applyUriReturnsModelWhenUriIsAspectModelUrn() {
      try ( var __ = overrideGitHubUrlTransformation() ) {

         Try<Model> result = strategy.applyUri( aspectModelUrn.getUrn() );

         assertTrue( result.isSuccess() );
      }
   }

   @Test
   void applyUriReturnsFailureWhenUriIsNotAspectModelUrn() {
      Try<Model> result = strategy.applyUri( URI.create( "https://github.com" ) );
      assertTrue( result.isFailure() );
   }

   @Test
   void readReturnsInputStreamWhenUriIsGithubUrl() throws IOException {
      try ( var __ = overrideGitHubUrlTransformation() ) {
         try ( var is = strategy.read( URI.create( GITHUB_URL + GITHUB_FILE ) ) ) {
            assertThat( is ).hasContent( testModelContent );
         }
      }
   }

   @Test
   void readReturnsNullWhenUriIsNotGithubUrl() {
      assertNull( strategy.read( URI.create( "https://notgithub.com" ) ) );
   }

   @Test
   void toStringReturnsExpectedFormat() {
      assertThat( strategy.toString() )
            .contains( "GitHubStrategy(root=" )
            .contains( GitHubResolutionStrategy.transformGithubUrlToContentLink( URI.create( GITHUB_URL ) ).get().toString() )
            .doesNotContain( GITHUB_FILE );
   }

   private MockedStatic<GitHubResolutionStrategy> overrideGitHubUrlTransformation() {
      MockedStatic<GitHubResolutionStrategy> mocked = mockStatic( GitHubResolutionStrategy.class );

      when( GitHubResolutionStrategy.transformGithubUrlToContentLink(
            eq( URI.create( GITHUB_URL + GITHUB_FILE ) ) ) )
            .thenReturn( Try.success( testUrl ) );

      when( GitHubResolutionStrategy.transformGithubUrlToContentLink(
            eq( URI.create( GITHUB_CTX_URL + GITHUB_FILE ) ) ) )
            .thenReturn( Try.success( testUrl ) );

      //tradeoff
      when( GitHubResolutionStrategy.toURL( any() ) ).thenCallRealMethod();
      when( GitHubResolutionStrategy.isUrl( any() ) ).thenCallRealMethod();
      when( GitHubResolutionStrategy.isGithubUrl( any() ) ).thenCallRealMethod();
      when( GitHubResolutionStrategy.loadModel( any( URL.class ) ) ).thenCallRealMethod();
      when( GitHubResolutionStrategy.read( any( URL.class ) ) ).thenCallRealMethod();

      return mocked;
   }
}