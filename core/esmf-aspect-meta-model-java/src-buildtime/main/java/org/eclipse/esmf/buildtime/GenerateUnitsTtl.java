/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.aspectmodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;

/**
 * Generates Java classes for the Units and QuantityKinds defined in SAMM.
 */
public class GenerateUnitsTtl {
   private final Model unitsModel;

   private static final String UNITS_CLASS_TEMPLATE = """
         /*
          * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

         package org.eclipse.esmf.metamodel;

         import java.util.Arrays;
         import java.util.HashMap;
         import java.util.List;
         import java.util.Locale;
         import java.util.Map;
         import java.util.Optional;
         import java.util.Set;
         import java.util.HashSet;
         import java.util.Collections;
         import java.util.stream.Collectors;

         import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
         import org.eclipse.esmf.aspectmodel.vocabulary.SammNs;
         import org.eclipse.esmf.metamodel.impl.DefaultUnit;
         import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
         import org.eclipse.esmf.metamodel.datatypes.LangString;

         /**
          * Enumeration of Units as defined in <a href="https://tfig.unece.org/contents/recommendation-20.htm">Recommendation 20</a> by
          * the <a href="https://www.unece.org/info/ece-homepage.html">UNECE</a> (United Nations Economic Commission for Europe).
          */
         public class Units {
            private static final Map<String, Unit> UNITS_BY_NAME = new HashMap<>();

            private Units() {
            }

            // ${initMethods}

            /**
             * Returns the unit with a given name
             */
            public static synchronized Optional<Unit> fromName( final String name ) {
               if ( UNITS_BY_NAME.isEmpty() ) {
                  // ${initMethodCalls}
               }
               return Optional.ofNullable( UNITS_BY_NAME.get( name ) );
            }

            /**
             * Finds the unit with a given code
             */
            public static Optional<Unit> fromCode( final String code ) {
               if ( UNITS_BY_NAME.isEmpty() ) {
                  fromName( "" );
               }
               return UNITS_BY_NAME.values()
                     .stream()
                     .filter( unit -> unit.getCode().map( code2 -> code2.equals( code ) ).orElse( false ) )
                     .findAny();
            }

            /**
             * Finds the units with a given symbol. In most, but not all cases, this set will contain exactly one unit.
             */
            public static Set<Unit> fromSymbol( final String symbol ) {
               if ( UNITS_BY_NAME.isEmpty() ) {
                  fromName( "" );
               }
               return UNITS_BY_NAME.values()
                     .stream()
                     .flatMap( unit -> unit.getSymbol()
                           .filter( symbol2 -> symbol2.equals( symbol ) )
                           .stream()
                           .map( symbol2 -> unit ) )
                     .collect( Collectors.toSet() );
            }

            /**
             * Returns all units of a given quantity kind
             */
            public static Set<Unit> unitsWithQuantityKind( final QuantityKind quantityKind ) {
               if ( UNITS_BY_NAME.isEmpty() ) {
                  fromName( "" );
               }
               return UNITS_BY_NAME.values()
                     .stream()
                     .filter( unit -> unit.getQuantityKinds().contains( quantityKind ) )
                     .collect( Collectors.toSet() );
            }
         }
         """;

   private static final String QUANTITY_KINDS_CLASS_TEMPLATE = """
         /*
          * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

         package org.eclipse.esmf.metamodel;

         import java.util.Arrays;
         import java.util.Optional;

         import org.eclipse.esmf.aspectmodel.resolver.services.ModelFile;
         import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
         import org.eclipse.esmf.aspectmodel.vocabulary.SammNs;
         import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

         /**
          * Enumeration of Quantity Kinds as defined in <a href="http://tfig.unece.org/contents/recommendation-20.htm">Recommendation 20</a>
          * by the <a href="http://www.unece.org/info/ece-homepage.html">UNECE</a> (United Nations Economic Commission for Europe).
          */
         public enum QuantityKinds implements QuantityKind {
            // ${joinedQuantityKinds};

            private final String name;
            private final String label;

            QuantityKinds( final String name, final String label ) {
               this.name = name;
               this.label = label;
            }
             
            @Override
            public AspectModelUrn urn() {
               return AspectModelUrn.fromUrn( SammNs.UNIT.urn( name ) );
            }
            
            @Override
            public Optional<ModelFile> getSourceFile() {
               return Optional.of( MetaModelFiles.UNITS );
            }

            /**
             * Returns the quantity kind's unique name
             */
            @Override
            public String getName() {
               return name;
            }

            /**
             * Returns the quantity kind's human-readable name
             */
            @Override
            public String getLabel() {
               return label;
            }

            /**
             * Returns the quantity kind's human-readable name
             */
            @Override
            public String toString() {
               return getLabel();
            }

            @Override
            public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
               return visitor.visitQuantityKind( this, context );
            }

            /**
             * Finds the quantity kind with a given name
             */
            public static Optional<QuantityKind> fromName( final String name ) {
               return Arrays.stream( QuantityKinds.values() )
                     .filter( quantityKind -> quantityKind.getName().equals( name ) )
                     .map( QuantityKind.class::cast )
                     .findAny();
            }
         }
         """;

   /*
    * args[0]: Path to src-gen directory
    */
   public static void main( final String[] args ) {
      final String pathToUnitsTtl = "samm/unit/" + KnownVersion.getLatest().toVersionString() + "/units.ttl";
      final Model unitsModel = ModelFactory.createDefaultModel();
      unitsModel.read( GenerateUnitsTtl.class.getClassLoader().getResourceAsStream( pathToUnitsTtl ), "", RDFLanguages.TURTLE.getName() );

      final String input = args[0];
      final Path targetPackage = Path.of( input ).resolve( "main" ).resolve( "java" ).resolve( "org" ).resolve( "eclipse" )
            .resolve( "esmf" ).resolve( "metamodel" );
      targetPackage.toFile().mkdirs();
      final File unitsFile = targetPackage.resolve( "Units.java" ).toFile();
      final File quantityKindsFile = targetPackage.resolve( "QuantityKinds.java" ).toFile();

      final GenerateUnitsTtl generator = new GenerateUnitsTtl( unitsModel );
      try {
         try ( final OutputStream outputStream = new FileOutputStream( unitsFile );
               final OutputStreamWriter outputStreamWriter = new OutputStreamWriter( outputStream, StandardCharsets.UTF_8 );
               final PrintWriter units = new PrintWriter( outputStreamWriter ) ) {
            generator.writeUnitsJava( units );
         }
         System.out.println( "Written " + unitsFile );
         try ( final OutputStream outputStream = new FileOutputStream( quantityKindsFile );
               final OutputStreamWriter outputStreamWriter = new OutputStreamWriter( outputStream, StandardCharsets.UTF_8 );
               final PrintWriter quantityKinds = new PrintWriter( outputStreamWriter ) ) {
            generator.writeQuantityKindsJava( quantityKinds );
         }
         System.out.println( "Written " + quantityKindsFile );
      } catch ( final IOException exception ) {
         throw new RuntimeException( "Could not write units file", exception );
      }
   }

   public GenerateUnitsTtl( final Model unitsModel ) {
      this.unitsModel = unitsModel;
   }

   private String toUpperSnakeCase( final String s ) {
      return s.replaceAll( "([A-Z])", "_$1" ).toUpperCase().replaceAll( "^_", "" );
   }

   private void writeUnitsJava( final PrintWriter out ) {
      final List<List<String>> declarationSlices = Lists.partition( unitDeclarations(), 100 );
      UNITS_CLASS_TEMPLATE.lines().forEach( line -> {
         if ( line.contains( "${initMethods}" ) ) {
            printInitMethods( out, declarationSlices );
         } else if ( line.contains( "${initMethodCalls}" ) ) {
            for ( int i = 0; i < declarationSlices.size(); i++ ) {
               out.println( "init" + i + "();" );
            }
         } else {
            out.println( line );
         }
      } );
   }

   private void printInitMethods( final PrintWriter out, final List<List<String>> declarationSlices ) {
      for ( int i = 0; i < declarationSlices.size(); i++ ) {
         final List<String> statements = declarationSlices.get( i );
         out.println( "private static void init" + i + "() {" );
         statements.forEach( out::println );
         out.println( "}" );
         out.println();
      }
   }

   private Optional<Statement> optionalAttributeValue( final Resource resource, final Property property ) {
      return Optional.ofNullable( resource.getProperty( property ) );
   }

   private List<Statement> attributeValues( final Resource resource, final Property property ) {
      return Streams.stream( resource.listProperties( property ) ).toList();
   }

   private List<String> unitDeclarations() {
      final SAMM samm = SammNs.SAMM;

      final Function<Optional<String>, String> buildDeclaration = optionalValue ->
            optionalValue.map( StringEscapeUtils::escapeJava ).map( "Optional.of(\"%s\")"::formatted ).orElse( "Optional.empty()" );
      final Function<Statement, Stream<String>> buildLangString = statement ->
            !"und".equals( Locale.forLanguageTag( statement.getLanguage() ).toLanguageTag() )
                  ? Stream.of( "new LangString( \"%s\", Locale.forLanguageTag( \"%s\" ) )"
                  .formatted( StringEscapeUtils.escapeJava( statement.getString() ), statement.getLanguage() ) )
                  : Stream.empty();

      return Streams.stream( unitsModel.listStatements( null, RDF.type, samm.Unit() ) ).map( Statement::getSubject ).map( unit -> {
         final String name = unit.getLocalName();
         final String symbolDeclaration = buildDeclaration.apply(
               optionalAttributeValue( unit, samm.symbol() ).map( Statement::getString ) );
         final String commonCodeDeclaration = buildDeclaration.apply(
               optionalAttributeValue( unit, samm.commonCode() ).map( Statement::getString ) );
         final String referenceUnitDeclaration = buildDeclaration.apply(
               optionalAttributeValue( unit, samm.referenceUnit() ).map( Statement::getResource ).map( Resource::getLocalName ) );
         final String conversionFactorDeclaration = buildDeclaration.apply(
               optionalAttributeValue( unit, samm.conversionFactor() ).map( Statement::getString ) );
         final String preferredNames = attributeValues( unit, samm.preferredName() ).stream()
               .map( statement -> ".withPreferredName( Locale.forLanguageTag( \"%s\" ), \"%s\" )".formatted( statement.getLanguage(),
                     statement.getString() ) ).collect( Collectors.joining() );
         final String quantityKindDefs = attributeValues( unit, samm.quantityKind() ).stream()
               .map( quantityKind -> "QuantityKinds." + toUpperSnakeCase( quantityKind.getResource().getLocalName() ) )
               .collect( Collectors.joining( ", ", "new HashSet<>(Arrays.asList(", "))" ) );
         final String quantityKinds = quantityKindDefs.contains( "()" ) ? "Collections.emptySet()" : quantityKindDefs;
         final String metaModelBaseAttributes =
               "MetaModelBaseAttributes.builder().withUrn( SammNs.UNIT.urn( \"%s\" ) )%s%s%s.build()".formatted(
                     unit.getLocalName(),
                     attributeValues( unit, samm.preferredName() ).stream()
                           .map( statement -> ".withPreferredName( Locale.forLanguageTag( \"%s\" ), \"%s\" )".formatted(
                                 statement.getLanguage(), statement.getString() ) ).collect( Collectors.joining() ),
                     attributeValues( unit, samm.description() ).stream()
                           .map( statement -> ".withDescription( Locale.forLanguageTag( \"%s\" ), \"%s\" )".formatted(
                                 statement.getLanguage(), statement.getString() ) ).collect( Collectors.joining() ),
                     attributeValues( unit, samm.see() ).stream().map( seeValue -> "withSee( \"" + seeValue + "\" )" )
                           .collect( Collectors.joining() ) );

         final String unitDefinition = "new DefaultUnit( %s, %s, %s, %s, %s, %s )".formatted( metaModelBaseAttributes, symbolDeclaration,
               commonCodeDeclaration, referenceUnitDeclaration, conversionFactorDeclaration, quantityKinds );
         return "UNITS_BY_NAME.put( \"%s\", %s );".formatted( name, unitDefinition );
      } ).sorted().toList();
   }

   private List<String> quantityKindDeclarations() {
      final SAMM samm = SammNs.SAMM;

      return Streams.stream( unitsModel.listStatements( null, RDF.type, samm.QuantityKind() ) ).map( Statement::getSubject )
            .map( quantityKind -> {
               final String name = quantityKind.getLocalName();
               final String label = attributeValues( quantityKind, samm.preferredName() ).stream().map( Statement::getString ).findAny()
                     .orElse( "" );
               return "%s( \"%s\", \"%s\" )".formatted( toUpperSnakeCase( name ), name, label );
            } ).toList();
   }

   private void writeQuantityKindsJava( final PrintWriter out ) {
      QUANTITY_KINDS_CLASS_TEMPLATE.lines().forEach( line -> {
         if ( line.contains( "${joinedQuantityKinds}" ) ) {
            out.println( String.join( ",\n", quantityKindDeclarations() ) + ";" );
         } else {
            out.println( line );
         }
      } );
   }
}
