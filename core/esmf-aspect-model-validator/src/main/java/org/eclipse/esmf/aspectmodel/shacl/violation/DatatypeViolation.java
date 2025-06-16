/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.shacl.violation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DatatypeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.fix.ReplaceValue;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

/**
 * Violation of a {@link DatatypeConstraint}
 *
 * @param context the evaluation context
 * @param allowedTypeUri the URI of the XSD or RDF class type that was allowed
 * @param actualTypeUri the URI that was encountered instead
 */
public record DatatypeViolation( EvaluationContext context, String allowedTypeUri, String actualTypeUri ) implements Violation {
   public static final String ERROR_CODE = "ERR_TYPE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      if ( context.property().isPresent() ) {
         if ( allowedTypeUri.equals( RDF.langString.getURI() ) && actualTypeUri.equals( XSD.xstring.getURI() ) ) {
            return String.format( "Property %s on %s is missing a language tag.", context.propertyName(), context.elementName() );
         } else if ( allowedTypeUri.equals( XSD.xstring.getURI() ) && actualTypeUri.equals( RDF.langString.getURI() ) ) {
            return String.format( "Property %s on %s must not have a language tag.", context.propertyName(), context.elementName() );
         } else {
            return String.format( "Property %s on %s uses data type %s, but only %s is allowed.",
                  context.propertyName(), context.elementName(), context.shortUri( actualTypeUri ), context.shortUri( allowedTypeUri ) );
         }
      }
      return String.format( "%s uses data type %s, but only %s is allowed.",
            context.elementName(), context.shortUri( actualTypeUri ), context.shortUri( allowedTypeUri ) );
   }

   @Override
   public RDFNode highlight() {
      return context().offendingStatements().stream()
            .findFirst()
            .map( Statement::getObject )
            .or( () -> context().property().map( p -> p.as( RDFNode.class ) ) )
            .filter( rdfNode -> TokenRegistry.getToken( rdfNode.asNode() ).isPresent() )
            .orElseGet( () -> context().element() );
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
