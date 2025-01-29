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

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

/**
 * Generates the Units instances
 */
public class GenerateUnits extends BuildtimeCodeGenerator {
   private final List<List<String>> declarationSlices;
   private final Model inputModel;

   public GenerateUnits( final Path srcBuildTimePath, final Path srcGenPath ) {
      super( srcBuildTimePath, srcGenPath, "Units", "org.eclipse.esmf.metamodel" );
      inputModel = loadMetaModelFile( "unit", "units.ttl" );
      declarationSlices = Lists.partition( unitDeclarations(), 100 );
   }

   @Override
   protected String interpolateVariable( final String variableName ) {
      return switch ( variableName ) {
         case "initMethods" -> generateInitMethods();
         case "initMethodCalls" -> generateInitMethodCalls();
         case "generator" -> getClass().getCanonicalName();
         default -> throw new RuntimeException( "Unexpected variable in template: " + variableName );
      };
   }

   private String generateInitMethods() {
      final StringBuilder result = new StringBuilder();
      for ( int i = 0; i < declarationSlices.size(); i++ ) {
         final List<String> statements = declarationSlices.get( i );
         result.append( "private static void init" );
         result.append( i );
         result.append( "() {" );
         statements.forEach( result::append );
         result.append( "}\n" );
      }
      return result.toString();
   }

   private String generateInitMethodCalls() {
      final StringBuilder result = new StringBuilder();
      for ( int i = 0; i < declarationSlices.size(); i++ ) {
         result.append( "init" );
         result.append( i );
         result.append( "();\n" );
      }
      return result.toString();
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

      return Streams.stream( inputModel.listStatements( null, RDF.type, samm.Unit() ) ).map( Statement::getSubject ).map( unit -> {
         final String name = unit.getLocalName();
         final String symbolDeclaration = buildDeclaration.apply(
               optionalAttributeValue( unit, samm.symbol() ).map( Statement::getString ) );
         final String commonCodeDeclaration = buildDeclaration.apply(
               optionalAttributeValue( unit, samm.commonCode() ).map( Statement::getString ) );
         final String referenceUnitDeclaration = buildDeclaration.apply(
               optionalAttributeValue( unit, samm.referenceUnit() ).map( Statement::getResource ).map( Resource::getLocalName ) );
         final String conversionFactorDeclaration = buildDeclaration.apply(
               optionalAttributeValue( unit, samm.conversionFactor() ).map( Statement::getString ) );
         final String quantityKindDefs = attributeValues( unit, samm.quantityKind() ).stream()
               .map( quantityKind -> "QuantityKinds." + toUpperSnakeCase( quantityKind.getResource().getLocalName() ) )
               .collect( Collectors.joining( ", ", "new HashSet<>(Arrays.asList(", "))" ) );
         final String quantityKinds = quantityKindDefs.contains( "()" ) ? "Collections.emptySet()" : quantityKindDefs;
         final String metaModelBaseAttributes =
               "MetaModelBaseAttributes.builder().withUrn( SammNs.UNIT.urn( \"%s\" ) )%s%s%s%s.build()".formatted(
                     unit.getLocalName(),
                     attributeValues( unit, samm.preferredName() ).stream()
                           .map( statement -> ".withPreferredName( Locale.forLanguageTag( \"%s\" ), \"%s\" )".formatted(
                                 statement.getLanguage(), statement.getString() ) ).collect( Collectors.joining() ),
                     attributeValues( unit, samm.description() ).stream()
                           .map( statement -> ".withDescription( Locale.forLanguageTag( \"%s\" ), \"%s\" )".formatted(
                                 statement.getLanguage(), statement.getString() ) ).collect( Collectors.joining() ),
                     attributeValues( unit, samm.see() ).stream().map( seeValue -> "withSee( \"" + seeValue + "\" )" )
                           .collect( Collectors.joining() ),
                     ".withSourceFile( MetaModelFile.UNITS )"
               );

         final String unitDefinition = "new DefaultUnit( %s, %s, %s, %s, %s, %s )".formatted( metaModelBaseAttributes,
               symbolDeclaration, commonCodeDeclaration, referenceUnitDeclaration, conversionFactorDeclaration, quantityKinds );
         return "UNITS_BY_NAME.put( \"%s\", %s );".formatted( name, unitDefinition );
      } ).sorted().toList();
   }
}
