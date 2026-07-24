/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.aspect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.turtle.languageserver.aspect.TestUtil.parsedDocument;

import java.util.stream.Stream;

import org.eclipse.esmf.Diagnostic;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AspectModelNamingConventionValidationServiceTest {
   private static final String PREFIXES = """
      @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
      """;
   private static final String NAMING_CONVENTION_DOCS = "https://eclipse-esmf.github.io/samm-specification/snapshot/modeling-guidelines"
         + ".html#naming-rules";
   private static final String BEST_PRACTICES_DOCS = "https://eclipse-esmf.github.io/samm-specification/snapshot/appendix/best-practices"
         + ".html";

   private final AspectModelNamingConventionValidationService service = new AspectModelNamingConventionValidationService();

   static Stream<Arguments> namingConventionWarningScenarios() {
      return Stream.of(
            Arguments.of(
                  "characteristic name starting lowercase produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :property ) .

                     :property a samm:Property ;
                        samm:characteristic :myValue .

                     :myValue a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """,
                  "WARN_NAMING_CONVENTION",
                  "'myValue' should start with an uppercase letter, e.g. 'MyValue'%n%s".formatted( NAMING_CONVENTION_DOCS )
            ),
            Arguments.of(
                  "characteristic subtype starting lowercase produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :property ) .

                     :property a samm:Property ;
                        samm:characteristic :myState .

                     :myState a samm-c:Enumeration ;
                        samm:dataType xsd:string ;
                        samm-c:values ( "a" "b" ) .
                     """,
                  "WARN_NAMING_CONVENTION",
                  "'myState' should start with an uppercase letter, e.g. 'MyState'%n%s".formatted( NAMING_CONVENTION_DOCS )
            ),
            Arguments.of(
                  "property name starting uppercase produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :MyValue ) .

                     :MyValue a samm:Property ;
                        samm:characteristic :MyState .

                     :MyState a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """,
                  "WARN_NAMING_CONVENTION",
                  "'MyValue' should start with a lowercase letter, e.g. 'myValue'%n%s".formatted( NAMING_CONVENTION_DOCS )
            ),
            Arguments.of(
                  "abstract property name starting uppercase produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :myValue ) .

                     :myValue a samm:Property ;
                        samm:extends :MyAbstractValue ;
                        samm:characteristic :MyState .

                     :MyAbstractValue a samm:AbstractProperty .

                     :MyState a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """,
                  "WARN_NAMING_CONVENTION",
                  "'MyAbstractValue' should start with a lowercase letter, e.g. 'myAbstractValue'%n%s".formatted( NAMING_CONVENTION_DOCS )
            ),
            Arguments.of(
                  "property name with uppercase acronym produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :documentURL ) .

                     :documentURL a samm:Property ;
                        samm:characteristic :MyValue .

                     :MyValue a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  ( "Name 'documentURL' should use camel case for acronyms instead of all-uppercase, e.g. 'documentUrl'%n%s" )
                        .formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "name with acronym followed by word keeps last acronym letter uppercase",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :property ) .

                     :property a samm:Property ;
                        samm:characteristic :XMLParserValue .

                     :XMLParserValue a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  ( "Name 'XMLParserValue' should use camel case for acronyms instead of all-uppercase, e.g. 'XmlParserValue'%n%s" )
                        .formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "description starting lowercase produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:description "this is a test description."@en ;
                        samm:properties ( ) .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  "Description should start with an uppercase letter%n%s".formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "description ending without punctuation produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:description "This is a test description"@en ;
                        samm:properties ( ) .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  "Description should end with a period%n%s".formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "aspect name containing meta model type name produces warning",
                  """
                     :VehicleAspect a samm:Aspect ;
                        samm:properties ( ) .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  "Name 'VehicleAspect' should not contain the meta model element type 'Aspect', e.g. 'Vehicle'%n%s"
                        .formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "property name containing meta model type name produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :priceProperty ) .

                     :priceProperty a samm:Property ;
                        samm:characteristic :MyValue .

                     :MyValue a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  "Name 'priceProperty' should not contain the meta model element type 'Property', e.g. 'price'%n%s"
                        .formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "aspect name containing meta model type name in the middle produces warning",
                  """
                     :ExampleAspectTest a samm:Aspect ;
                        samm:properties ( ) .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  "Name 'ExampleAspectTest' should not contain the meta model element type 'Aspect', e.g. 'ExampleTest'%n%s"
                        .formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "characteristic name containing meta model type name produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :property ) .

                     :property a samm:Property ;
                        samm:characteristic :TemperatureCharacteristic .

                     :TemperatureCharacteristic a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  "Name 'TemperatureCharacteristic' should not contain the meta model element type 'Characteristic', e.g. 'Temperature'%n%s"
                        .formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "entity name containing meta model type name produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :property ) .

                     :property a samm:Property ;
                        samm:characteristic :MyValue .

                     :MyValue a samm-c:SingleEntity ;
                        samm:dataType :EngineEntity .

                     :EngineEntity a samm:Entity ;
                        samm:properties ( ) .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  "Name 'EngineEntity' should not contain the meta model element type 'Entity', e.g. 'Engine'%n%s"
                        .formatted( BEST_PRACTICES_DOCS )
            ),
            Arguments.of(
                  "name equal to meta model type name produces warning without suggestion",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :property ) .

                     :property a samm:Property ;
                        samm:characteristic :Characteristic .

                     :Characteristic a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """,
                  "WARN_NAMING_BEST_PRACTICE",
                  "Name 'Characteristic' should not simply repeat the meta model element type 'Characteristic', "
                        + "choose a more descriptive name%n%s".formatted( BEST_PRACTICES_DOCS )
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "namingConventionWarningScenarios" )
   void namingConventionViolationProducesWarning( final String scenarioName, final String content, final String expectedErrorCode,
         final String expectedMessage ) {
      final ParsedDocument document = parsedDocument( "Aspect.ttl", PREFIXES + content );

      final DiagnosticReport report = service.validate( document );

      assertThat( report.diagnostics() ).singleElement().isInstanceOfSatisfying( AspectDocumentDiagnostic.class, diagnostic -> {
         assertThat( diagnostic.code().code() ).isEqualTo( expectedErrorCode );
         assertThat( diagnostic.severity() ).isEqualTo( Diagnostic.Severity.WARNING );
         assertThat( diagnostic.message() ).isEqualTo( expectedMessage );
      } );
   }

   static Stream<Arguments> noDiagnosticScenarios() {
      return Stream.of(
            Arguments.of(
                  "characteristic name starting uppercase produces no diagnostic",
                  "Aspect.ttl",
                  PREFIXES + """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :property ) .

                     :property a samm:Property ;
                        samm:characteristic :MyValue .

                     :MyValue a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """
            ),
            Arguments.of(
                  "property names are not flagged by characteristic naming convention",
                  "Aspect.ttl",
                  PREFIXES + """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :myValue ) .

                     :myValue a samm:Property ;
                        samm:characteristic :MyValue .

                     :MyValue a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """
            ),
            Arguments.of(
                  "non aspect model document is ignored",
                  "plain.ttl",
                  """
                     @prefix ex: <http://example.com#> .

                     ex:something ex:hasValue "42" .
                     """
            ),
            Arguments.of(
                  "camel cased name without acronym produces no acronym diagnostic",
                  "Aspect.ttl",
                  PREFIXES + """
                     :Vehicle a samm:Aspect ;
                        samm:properties ( :documentUrl ) .

                     :documentUrl a samm:Property ;
                        samm:characteristic :MyValue .

                     :MyValue a samm:Characteristic ;
                        samm:dataType xsd:string .
                     """
            ),
            Arguments.of(
                  "description starting uppercase with punctuation produces no diagnostic",
                  "Aspect.ttl",
                  PREFIXES + """
                     :Vehicle a samm:Aspect ;
                        samm:description "This is a test description."@en ;
                        samm:properties ( ) .
                     """
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "noDiagnosticScenarios" )
   void producesNoDiagnostic( final String scenarioName, final String fileName, final String content ) {
      final ParsedDocument document = parsedDocument( fileName, content );

      final DiagnosticReport report = service.validate( document );

      assertThat( report.diagnostics() ).isEmpty();
   }
}
