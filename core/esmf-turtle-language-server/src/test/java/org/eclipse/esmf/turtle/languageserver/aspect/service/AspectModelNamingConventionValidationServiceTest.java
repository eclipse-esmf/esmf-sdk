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

import org.eclipse.esmf.aspectmodel.VersionInfo;
import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.validation.DefaultDocumentLocationViolation;
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
   private static final String NAMING_CONVENTION_DOCS =
         "https://eclipse-esmf.github.io/samm-specification/%s/modeling-guidelines.html#naming-rules"
               .formatted( VersionInfo.ASPECT_META_MODEL_VERSION );
   private static final String BEST_PRACTICES_DOCS = "https://eclipse-esmf.github.io/samm-specification/%s/appendix/best-practices.html"
         .formatted( VersionInfo.ASPECT_META_MODEL_VERSION );

   private final AspectModelNamingConventionValidationService service = new AspectModelNamingConventionValidationService();

   static Stream<Arguments> namingConventionErrorScenarios() {
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
                  "ERR_NAMING_CONVENTION",
                  NAMING_CONVENTION_DOCS,
                  "'myValue' should start with an uppercase letter, e.g. 'MyValue'"
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
                  "ERR_NAMING_CONVENTION",
                  NAMING_CONVENTION_DOCS,
                  "'myState' should start with an uppercase letter, e.g. 'MyState'"
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
                  "ERR_NAMING_CONVENTION",
                  NAMING_CONVENTION_DOCS,
                  "'MyValue' should start with a lowercase letter, e.g. 'myValue'"
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
                  "ERR_NAMING_CONVENTION",
                  NAMING_CONVENTION_DOCS,
                  "'MyAbstractValue' should start with a lowercase letter, e.g. 'myAbstractValue'"
            ) );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "namingConventionErrorScenarios" )
   void namingConventionViolationProducesError( final String scenarioName, final String content, final String expectedErrorCode,
         final String expectedHref, final String expectedMessage ) {
      final ParsedDocument document = parsedDocument( "Aspect.ttl", PREFIXES + content );

      final ViolationReport report = service.validate( document );

      assertThat( report.violations() ).singleElement().isInstanceOfSatisfying( DefaultDocumentLocationViolation.class, diagnostic -> {
         assertThat( diagnostic.code().code() ).isEqualTo( expectedErrorCode );
         assertThat( diagnostic.code().href() ).contains( expectedHref );
         assertThat( diagnostic.severity() ).isEqualTo( Violation.Severity.ERROR );
         assertThat( diagnostic.message() ).isEqualTo( expectedMessage );
      } );
   }

   static Stream<Arguments> namingBestPracticeInfoScenarios() {
      return Stream.of(
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
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Name 'documentURL' should use camel case for acronyms instead of all-uppercase, e.g. 'documentUrl'"
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
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Name 'XMLParserValue' should use camel case for acronyms instead of all-uppercase, e.g. 'XmlParserValue'"
            ),
            Arguments.of(
                  "description starting lowercase produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:description "this is a test description."@en ;
                        samm:properties ( ) .
                     """,
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Description should start with an uppercase letter"
            ),
            Arguments.of(
                  "description ending without punctuation produces warning",
                  """
                     :Vehicle a samm:Aspect ;
                        samm:description "This is a test description"@en ;
                        samm:properties ( ) .
                     """,
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Description should end with a period"
            ),
            Arguments.of(
                  "aspect name containing meta model type name produces warning",
                  """
                     :VehicleAspect a samm:Aspect ;
                        samm:properties ( ) .
                     """,
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Name 'VehicleAspect' should not contain the meta model element type 'Aspect', e.g. 'Vehicle'"
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
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Name 'priceProperty' should not contain the meta model element type 'Property', e.g. 'price'"
            ),
            Arguments.of(
                  "aspect name containing meta model type name in the middle produces warning",
                  """
                     :ExampleAspectTest a samm:Aspect ;
                        samm:properties ( ) .
                     """,
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Name 'ExampleAspectTest' should not contain the meta model element type 'Aspect', e.g. 'ExampleTest'"
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
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Name 'TemperatureCharacteristic' should not contain the meta model element type 'Characteristic', e.g. 'Temperature'"
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
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Name 'EngineEntity' should not contain the meta model element type 'Entity', e.g. 'Engine'"
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
                  "INFO_NAMING_BEST_PRACTICES",
                  BEST_PRACTICES_DOCS,
                  "Name 'Characteristic' should not simply repeat the meta model element type 'Characteristic', "
                        + "choose a more descriptive name"
            )
      );
   }

   @ParameterizedTest( name = "{0}" )
   @MethodSource( "namingBestPracticeInfoScenarios" )
   void namingBestPracticeViolationProducesInfo( final String scenarioName, final String content, final String expectedErrorCode,
         final String expectedHref, final String expectedMessage ) {
      final ParsedDocument document = parsedDocument( "Aspect.ttl", PREFIXES + content );

      final ViolationReport report = service.validate( document );

      assertThat( report.violations() ).singleElement().isInstanceOfSatisfying( DefaultDocumentLocationViolation.class, diagnostic -> {
         assertThat( diagnostic.code().code() ).isEqualTo( expectedErrorCode );
         assertThat( diagnostic.code().href() ).contains( expectedHref );
         assertThat( diagnostic.severity() ).isEqualTo( Violation.Severity.INFO );
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
      final ViolationReport report = service.validate( document );
      assertThat( report.violations() ).isEmpty();
   }
}
