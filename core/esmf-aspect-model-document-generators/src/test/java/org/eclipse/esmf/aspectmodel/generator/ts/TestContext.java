/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.eclipse.esmf.aspectmodel.generator.ts;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.esmf.functions.ThrowingFunction;

import org.apache.commons.io.IOUtils;

public class TestContext {
   private static final String SYNTHETIC_URN_PATTERN = "x[0-9A-F]{8}";

   private static final List<String> REPLACED_TEST_ARTIFACTS =
         List.of( "AEntity", "ExtendingTestEntity", "UsedTestEnumeration",
               "AbstractTestEntity", "YesNo", "TrafficLight", "TheEnum", "TestTimeSeriesEntity",
               "TestState", "TestScalarEnumeration", "TestQuantity", "TestFirstEntity", "TestEnumTwoCharacteristic",
               "TestEnumOneCharacteristic", "TestEnumerationTwo", "TestEnumeration", "TestEntityWithEnumOne", "TestCharacteristic",
               "SecondTestEntity", "RightEntity", "ProductionPeriodEntity", "TimeSeriesEntity",
               "MyEnumerationTwo", "MyEnumerationThree", "SystemState", "TheEntity", "ParentOfParentEntity",
               "TestEntityTwo", "TestEntityOne",
               "MyEnumerationOne", "MyEnumerationFour", "MyEntityOne", "MyEntityTwo", "MyEntityThree", "MyEntityFour",
               "LeftEntity", "Id", "GenericEnum", "Foo", "FirstLevelEntity", "EvaluationResults", "EvaluationResult",
               "Enum2", "Enum1", "EEntity", "ParentTestEntity", "TestEntity", "Automation" );

   public static ThrowingFunction<Collection<TsGenerator>, GenerationResult, IOException> generateAspectCode() {
      return generators -> {
         final File tempDirectory = Files.createTempDirectory( "junit" ).toFile();
         final GeneratedCodeAndClasses generatedCodeAndClasses = generateTsCode( tempDirectory, generators );
         return new GenerationResult( generatedCodeAndClasses.sources() );
      };
   }

   private record GeneratedCodeAndClasses( Map<QualifiedName, String> sources ) {}

   private static GeneratedCodeAndClasses generateTsCode( final File tempDirectory, final Collection<TsGenerator> generators )
         throws IOException {
      final File subFolder = new File(
            tempDirectory.getAbsolutePath() + File.separator + generators.iterator().next().getConfig().packageName()
                  .replace( '.', File.separatorChar ) );
      if ( !subFolder.mkdirs() ) {
         throw new IOException( "Could not create directory: " + subFolder );
      }

      final Map<QualifiedName, ByteArrayOutputStream> outputs = new LinkedHashMap<>();
      generators.forEach( generator ->
            generator.generate( name -> outputs.computeIfAbsent( name, name2 -> new ByteArrayOutputStream() ) ) );

      final Map<QualifiedName, String> sources = new LinkedHashMap<>();

      for ( final Map.Entry<QualifiedName, ByteArrayOutputStream> entry : outputs.entrySet() ) {
         final QualifiedName className = entry.getKey();
         String content = entry.getValue().toString( StandardCharsets.UTF_8 );
         content = normalizeString( content );
         sources.put( className, content );
         writeFile( className.fileName(), content.getBytes(), subFolder );
      }
      return new GeneratedCodeAndClasses( sources );
   }

   private static String normalizeString( final String classContent ) {
      // Step 1. Replace dates and synthetic URNs
      String replaced = classContent.replaceAll( "date = \".*?\"", "date = \"replaced\"" )
            .replaceAll( SYNTHETIC_URN_PATTERN, "GenericEnum" );

      // Step 2. Replace test artifact names with generic placeholder
      for ( String replacedArtifact : REPLACED_TEST_ARTIFACTS ) {
         replaced = replaced.replaceAll( replacedArtifact, "ReplacedAspectArtifact" );
      }

      // Step 3. Remove duplicate import statements for replaced artifacts and keep only one
      List<String> artifacts = List.of( "ReplacedAspectArtifact", "MetaReplacedAspectArtifact" );

      for ( String artifact : artifacts ) {
         final String regex = "(import \\{ " + artifact + ",} from '\\./" + artifact + "';)";
         Matcher matcher = Pattern.compile( regex ).matcher( replaced );
         if ( matcher.find() ) {
            replaced = replaced.replaceAll( regex, "" );
            replaced = "import { " + artifact + ",} from './" + artifact + "';\n" + replaced;
         }
      }

      return replaced;
   }

   private static void writeFile( final String className, final byte[] content, final File targetDirectory ) {
      final String fileName = className + ".ts";
      try {
         IOUtils.write( content, Files.newOutputStream( new File( targetDirectory, fileName ).toPath() ) );
      } catch ( final IOException e ) {
         fail( "Could not create file " + fileName );
      }
   }
}
