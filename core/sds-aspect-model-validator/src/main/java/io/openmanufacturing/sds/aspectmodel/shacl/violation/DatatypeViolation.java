/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.shacl.violation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import io.openmanufacturing.sds.aspectmodel.shacl.fix.Fix;
import io.openmanufacturing.sds.aspectmodel.shacl.fix.ReplaceValue;

public record DatatypeViolation(EvaluationContext context, String allowedTypeUri, String actualTypeUri) implements Violation {
   public static final String ERROR_CODE = "ERR_TYPE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String message() {
      if ( context.property().isPresent() ) {
         return allowedTypeUri.equals( RDF.langString.getURI() ) && actualTypeUri.equals( XSD.xstring.getURI() ) ?
               String.format( "Property %s on %s is missing a language tag",
                     propertyName(), elementName() ) :
               String.format( "Property %s on %s uses data type %s, but only %s is allowed",
                     propertyName(), elementName(), shortUri( actualTypeUri ), shortUri( allowedTypeUri ) );
      }
      return String.format( "%s uses data type %s, but only %s is allowed",
            elementName(), shortUri( actualTypeUri ), shortUri( allowedTypeUri ) );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitDatatypeViolation( this );
   }

   @Override
   public List<Fix> fixes() {
      if ( context.property().isEmpty() || context.offendingStatements().isEmpty() ) {
         return List.of();
      }

      final List<Fix> fixes = new ArrayList<>();
      final Property property = context.property().get();

      // value is string but should be langString
      if ( allowedTypeUri.equals( RDF.langString.getURI() ) && actualTypeUri.equals( XSD.xstring.getURI() ) ) {
         fixes.addAll( replaceStringWithLangString() );
      }

      if ( allowedTypeUri.equals( XSD.xstring.getURI() ) && actualTypeUri.equals( RDF.langString.getURI() ) ) {
         fixes.addAll( replaceLangStringWithString() );
      }

      return fixes;
   }

   private List<Fix> replaceLangStringWithString() {
      return context.offendingStatements().stream()
            .flatMap( offendingStatement -> {
               final Literal oldValue = offendingStatement.getLiteral();
               if ( oldValue == null ) {
                  return Stream.of();
               }
               final Literal newValue = context.element().getModel().createLiteral( oldValue.getLexicalForm() );
               return Stream.of( new ReplaceValue( context, oldValue, newValue,
                     Optional.of( "Remove language tag from value" ) ) );
            } )
            .collect( Collectors.toList() );
   }

   private List<Fix> replaceStringWithLangString() {
      return context.offendingStatements().stream()
            .flatMap( offendingStatement -> {
               final Literal oldValue = offendingStatement.getLiteral();
               if ( oldValue == null ) {
                  return Stream.of();
               }
               final Literal newValue = context.element().getModel().createLiteral( oldValue.getString(), "en" );
               return Stream.of( new ReplaceValue( context, oldValue, newValue,
                     Optional.of( "Add default @en language tag to value" ) ) );
            } )
            .collect( Collectors.toList() );
   }

   @Override
   public AppliesTo appliesTo() {
      return AppliesTo.ONLY_PROPERTY;
   }
}
