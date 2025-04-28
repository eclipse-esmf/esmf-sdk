package org.eclipse.esmf.aspectmodel.java.performance;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.DefaultAspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class EsmfPerformanceTest {
   private static final int ITERATIONS = 10;
   private static final List<AspectModelFile> inputFiles210 = InlineModel.getModel( KnownVersion.SAMM_2_2_0.toVersionString() );
   private static final Collection<AspectModelUrn> urns210 = getUrns( inputFiles210 );
   private static final List<AspectModelFile> inputFilesLatest = InlineModel.getModel( KnownVersion.getLatest().toVersionString() );
   private static final Collection<AspectModelUrn> urnsLatest = getUrns( inputFilesLatest );
   private static final long MAX_DURATION_MS = 800;
   private static final long MAX_MEMORY_BYTES = 130L * 1024 * 1024;

   @Test
   @Timeout( 90 )
   void testResolvingWithOldSammVersion() {
      measurePerformance( "OldSammVersion", inputFiles210, urns210 );
   }

   @Test
   @Timeout( 2 )
   void testResolvingWithLatestSammVersion() {
      measurePerformance( "LatestSammVersion", inputFilesLatest, urnsLatest );
   }

   private static Set<AspectModelUrn> getUrns( final List<AspectModelFile> listOfFiles ) {
      return listOfFiles.stream().map( AspectModelFile::sourceModel )
            .map( Model::listSubjects )
            .map( ResIterator::toSet )
            .flatMap( Collection::stream )
            .filter( resource -> !resource.isAnon() )
            .map( urn -> AspectModelUrn.fromUrn( urn.getURI() ) )
            .collect( Collectors.toSet() );
   }

   static class InlineModel {
      //just copied from performance tests - it is not mandatory to be in sync
      private static final int ASPECTS = 10;
      private static final int ENTITIES = 10;
      private static final int PROPERTIES = 30;
      private static final int CHARACTERISTICS = 30;
      private static final int CONSTRAINTS = 10;
      private static final int OPERATIONS = 30;
      private static final int EVENTS = 10;

      static List<AspectModelFile> getModel( final String samm ) {

         final List<AspectModelFile> models = new ArrayList<>();

         StringBuilder result = new StringBuilder();

         String header = """
               @prefix :  <urn:samm:com.bosch.performance.catalog.test.0:1.1.0#> .
               @prefix samm:   <urn:samm:org.eclipse.esmf.samm:meta-model:%samm#> .
               @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:%samm#> .
               @prefix xsd:    <http://www.w3.org/2001/XMLSchema#> .
               """.replace( "%samm", samm );
         result.append( header );

         result.append(
               """
                     :TestAspect0 a samm:Aspect ;
                        samm:description "A test Aspect en"@en ;
                        samm:description "A test Aspect de"@de ;
                        samm:preferredName  "TestAspect en"@en ;
                        samm:preferredName  "TestAspect de"@de ;
                        samm:properties  ( :testInteger0 ) ;
                        samm:operations  ( :move0 ) ;
                        samm:events      ( :event0 ) .
                     """ );

         for ( int i = 0; i < PROPERTIES; i++ ) {
            result.append(
                  """
                        :testInteger%index
                           a samm:Property ;
                           samm:preferredName  "testInteger en"@en ;
                           samm:preferredName  "testInteger de"@de ;
                           samm:characteristic :IntegerTestCharacteristic0 .
                        """
                        .replace( "%index", String.valueOf( i ) ) );
         }

         for ( int i = 0; i < ENTITIES; i++ ) {
            result.append(
                  """
                        :Entity%index a samm:Entity ;
                           samm:preferredName "Entity de"@en ;
                           samm:preferredName "Entity en"@de ;
                           samm:description "Entity that describes a result state from production of a processed part. en"@en ;
                           samm:description "Entity that describes a result state from production of a processed part. de"@de ;
                           samm:properties ( :testInteger0 ).
                        """
                        .replace( "%index", String.valueOf( i ) ) );
         }

         for ( int i = 0; i < CHARACTERISTICS; i++ ) {
            result.append( """
                  :IntegerTestCharacteristic%index
                     a samm:Characteristic ;
                     samm:preferredName "IntegerTestCharacteristic en"@en ;
                     samm:preferredName "IntegerTestCharacteristic de"@de ;
                     samm:dataType xsd:nonNegativeInteger ;
                     samm-c:constraint :RangeConstraint0.
                  """
                  .replace( "%index", String.valueOf( i ) ) );
         }

         for ( int i = 0; i < CONSTRAINTS; i++ ) {
            result.append(
                  """
                        :RangeConstraint%index a samm-c:RangeConstraint ;
                           samm:description "Limits the range of the individual numbers in the list from 5 to 10. en"@en ;
                           samm:description "Limits the range of the individual numbers in the list from 5 to 10. de"@de ;
                           samm-c:minValue  "5"^^xsd:nonNegativeInteger ;
                           samm-c:maxValue  "10"^^xsd:nonNegativeInteger .
                        """
                        .replace( "%index", String.valueOf( i ) ) );
         }

         for ( int i = 0; i < OPERATIONS; i++ ) {
            result.append(
                  """
                         :move%index
                           a samm:Operation ;
                           samm:description "moves around en"@en ;
                           samm:description "moves around de"@de ;
                           samm:input () ;
                           samm:output :testInteger0 .
                        """
                        .replace( "%index", String.valueOf( i ) ) );
         }

         for ( int i = 0; i < EVENTS; i++ ) {
            result.append(
                  """
                        :event%index
                            a samm:Event ;
                            samm:preferredName "event en"@en ;
                            samm:preferredName "event de"@de ;
                            samm:parameters    ( :testInteger0 ) .
                        """
                        .replace( "%index", String.valueOf( i ) ) );
         }

         models.add(
               new DefaultAspectModelFile(
                     ModelFactory.createDefaultModel().read( new StringReader( result.toString() ), "", "TTL" ),
                     List.of(),
                     Optional.of( URI.create( "namespaceFile" + 0 ) ) )
         );

         for ( int aspect = 1; aspect < ASPECTS; aspect++ ) {

            result = new StringBuilder();

            header = """
                  @prefix :       <urn:samm:com.bosch.performance.catalog.test.0:1.1.0#> .
                  @prefix samm:   <urn:samm:org.eclipse.esmf.samm:meta-model:%samm#> .
                  @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:%samm#> .
                  @prefix xsd:    <http://www.w3.org/2001/XMLSchema#> .
                  """.replace( "%samm", samm );
            result.append( header );

            result.append(
                  """
                        :TestAspect%index a samm:Aspect ;
                           samm:description "A test Aspect en"@en ;
                           samm:description "A test Aspect de"@de ;
                           samm:preferredName  "TestAspect en"@en ;
                           samm:preferredName  "TestAspect de"@de ;
                           samm:properties  ( :testInteger0 ) ;
                           samm:operations  ( :move0 ) ;
                           samm:events      ( :event0 ) .
                        """
                        .replace( "%index", String.valueOf( aspect ) ) );

            models.add(
                  new DefaultAspectModelFile(
                        ModelFactory.createDefaultModel().read( new StringReader( result.toString() ), "", "TTL" ),
                        List.of(),
                        Optional.of( URI.create( "namespaceFile" + aspect ) ) )
            );
         }
         return models;
      }
   }

   public static class ResolutionStrategyBasedOnFiles implements ResolutionStrategyWithDefaultModelSource {

      private final List<AspectModelFile> files;

      public ResolutionStrategyBasedOnFiles( final List<AspectModelFile> files ) {
         this.files = files;
      }

      @Override
      public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport support ) {
         final Resource resource = ResourceFactory.createResource( aspectModelUrn.getUrn().toString() );
         final var probablyFile = files.stream().filter( file -> support.containsDefinition( file, aspectModelUrn ) ).findAny();
         if ( probablyFile.isEmpty() ) {
            throw new ResourceDefinitionNotFoundException( getClass().getSimpleName(), resource.toString() );
         }
         return probablyFile.get();
      }
   }

   private void measurePerformance( final String testName, final List<AspectModelFile> inputFiles, final Collection<AspectModelUrn> urns ) {
      Runtime runtime = Runtime.getRuntime();

      // Clean up before starting measurement
      System.gc();
      long beforeUsedMem = runtime.totalMemory() - runtime.freeMemory();
      long startTime = System.nanoTime();

      for ( int i = 0; i < ITERATIONS; i++ ) {
         assertThatCode( () -> new AspectModelLoader( new ResolutionStrategyBasedOnFiles( inputFiles ) ).loadUrns( urns ) )
               .doesNotThrowAnyException();
      }

      long endTime = System.nanoTime();
      long afterUsedMem = runtime.totalMemory() - runtime.freeMemory();

      long durationInMillis = (endTime - startTime) / 1_000_000;
      long memoryUsedInBytes = afterUsedMem - beforeUsedMem;

      System.out.println( "\nPerformance Results for: " + testName );
      System.out.println( "Total time: " + durationInMillis + " ms for " + ITERATIONS + " iterations" );
      System.out.println( "Average time per iteration: " + (durationInMillis / ITERATIONS) + " ms" );
      System.out.println( "Memory allocated: " + memoryUsedInBytes / 1024 + " KB\n" );

      assertTrue( durationInMillis <= MAX_DURATION_MS,
            "Execution time exceeded the limit: " + durationInMillis + "ms > " + MAX_DURATION_MS + "ms" );
      assertTrue( memoryUsedInBytes <= MAX_MEMORY_BYTES,
            "Memory usage exceeded the limit: " + (memoryUsedInBytes / 1024 / 1024) + "MB > " + (MAX_MEMORY_BYTES / 1024 / 1024) + "MB" );
   }
}
