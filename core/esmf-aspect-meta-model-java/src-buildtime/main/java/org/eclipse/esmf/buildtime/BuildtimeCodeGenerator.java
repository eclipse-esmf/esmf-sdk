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

package org.eclipse.esmf.buildtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFLanguages;

/**
 * Abstract base class for build time code generators
 */
public abstract class BuildtimeCodeGenerator {
   protected final Path srcBuildTimePath;
   protected final Path srcGenPath;
   protected final Path templatesPath;
   protected final String templateClassName;
   protected final List<String> templateClassSources;
   protected final String targetPackage;

   protected BuildtimeCodeGenerator( final Path srcBuildTimePath, final Path srcGenPath, final String templateClassName,
         final String targetPackage ) {
      this.srcBuildTimePath = srcBuildTimePath;
      this.srcGenPath = srcGenPath;
      templatesPath = srcBuildTimePath.resolve( "main" ).resolve( "java" )
            .resolve( "org" ).resolve( "eclipse" ).resolve( "esmf" ).resolve( "buildtime" ).resolve( "template" );
      this.templateClassName = templateClassName;
      this.targetPackage = targetPackage;
      templateClassSources = determineTemplateClassSourceCode();
   }

   protected abstract String interpolateVariable( String variableName );

   protected Model loadMetaModelFile( final String section, final String fileName ) {
      final Model model = ModelFactory.createDefaultModel();
      final String pathToTurtleFile = "samm/%s/%s/%s".formatted( section, KnownVersion.getLatest().toVersionString(), fileName );
      model.read( BuildtimeCodeGenerator.class.getClassLoader().getResourceAsStream( pathToTurtleFile ), "",
            RDFLanguages.TURTLE.getName() );
      return model;
   }

   private String generateClassContent() {
      final StringBuilder result = new StringBuilder();
      final Pattern replaceWholeLineVariablePattern = Pattern.compile( " * // \\$\\{([a-zA-Z]+)}.*" );
      final Pattern replaceSingleVariablePattern = Pattern.compile( ".*\\$\\{([a-zA-Z]+)}.*" );
      for ( final String line : templateClassSources ) {
         final String lineToAdd;
         final Matcher wholeLineMatcher = replaceWholeLineVariablePattern.matcher( line );
         if ( wholeLineMatcher.matches() ) {
            final String variableName = wholeLineMatcher.group( 1 );
            lineToAdd = interpolateVariable( variableName );
         } else {
            final Matcher singleVariableMatcher = replaceSingleVariablePattern.matcher( line );
            if ( singleVariableMatcher.matches() ) {
               final String variableName = singleVariableMatcher.group( 1 );
               final String targetValue = interpolateVariable( variableName );
               if ( targetValue == null ) {
                  throw new RuntimeException( "Build-time code generation failed: Value for " + variableName + " could not be determined" );
               }
               lineToAdd = line.replace( "${" + variableName + "}", targetValue );
            } else {
               lineToAdd = line;
            }
         }
         result.append( lineToAdd );
         result.append( "\n" );
      }

      return result.toString();
   }

   private Path resolvePackageInPath( final Path base ) {
      Path result = base;
      for ( final String pathPart : targetPackage.split( "\\." ) ) {
         result = result.resolve( pathPart );
      }
      return result;
   }

   private List<String> determineTemplateClassSourceCode() {
      final Path filePath = templatesPath.resolve( templateClassName + ".java" );
      try ( final Stream<String> stream = Files.lines( filePath ) ) {
         return stream.map( line -> line.startsWith( "package " ) ? "package " + targetPackage + ";" : line ).toList();
      } catch ( final IOException exception ) {
         throw new RuntimeException( "Could not read template file " + filePath, exception );
      }
   }

   private File determineOutputFile() {
      return resolvePackageInPath( srcGenPath.resolve( "main" ).resolve( "java" ) )
            .resolve( templateClassName + ".java" ).toFile();
   }

   protected Optional<Statement> optionalAttributeValue( final Resource resource, final Property property ) {
      return Optional.ofNullable( resource.getProperty( property ) );
   }

   protected List<Statement> attributeValues( final Resource resource, final Property property ) {
      return Streams.stream( resource.listProperties( property ) ).toList();
   }

   protected String toUpperSnakeCase( final String s ) {
      return s.replaceAll( "([A-Z])", "_$1" ).toUpperCase().replaceAll( "^_", "" );
   }

   public void writeGeneratedFile() {
      final File outputFile = determineOutputFile();
      outputFile.getParentFile().mkdirs();
      try ( final OutputStream outputStream = new FileOutputStream( outputFile ) ) {
         outputStream.write( generateClassContent().getBytes( StandardCharsets.UTF_8 ) );
      } catch ( final IOException exception ) {
         throw new RuntimeException( "Could not write source code file " + outputFile, exception );
      }
      System.out.println( "Written " + outputFile );
   }
}
