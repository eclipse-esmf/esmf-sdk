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

package org.eclipse.esmf.aspectmodel.validation.services;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedLanguagesConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedValuesConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AndConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.ClassConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.ClosedConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.Constraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DatatypeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DisjointConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.EqualsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.HasValueConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.LessThanConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.LessThanOrEqualsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxExclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxInclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxLengthConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinExclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinInclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinLengthConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeKindConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NotConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.OrConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.PatternConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.SparqlConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.XoneConstraint;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClassTypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClosedViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.DisjointViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.EqualsViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidValueViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.JsConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.LanguageFromListViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.LessThanOrEqualsViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.LessThanViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxCountViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxExclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxInclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxLengthViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinCountViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinExclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinInclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinLengthViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.NodeKindViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.NotViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.PatternViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.UniqueLanguageViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ValueFromListViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.CycleViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;

/**
 * Formats one or multiple {@link Violation}s in a human-readable way and provides detailed information.
 * Note that this is intended only for places with raw textual output, such as a text console.
 * For a more sensible representation of violations in other contexts, implement {@link Violation.Visitor}.
 */
public class DetailedViolationFormatter extends ViolationFormatter {
   @Override
   public String apply( final List<Violation> violations ) {
      if ( violations.isEmpty() ) {
         return String.format( "# Input model is valid%n" );
      }

      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "# Validation errors were found:%n%n" ) );
      for ( final Violation violation : violations ) {
         builder.append( violation.accept( this ) );
         builder.append( String.format( "%n" ) );
      }

      return builder.toString();
   }

   @Override
   public String visit( final Violation violation ) {
      return formatViolation( violation, () -> "" );
   }

   private String formatViolation( final Violation violation, final Supplier<String> additionalAttributesSupplier ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "- violation-type: %s%n", violation.getClass().getSimpleName() ) );
      builder.append( String.format( "  error-code: %s%n", violation.errorCode() ) );
      if ( violation.message() != null ) {
         builder.append( String.format( "  description: %s%n", violation.message() ) );
      }
      if ( violation.violationSpecificMessage() != null
            && ( violation.message() == null || !violation.message().equals( violation.violationSpecificMessage() ) ) ) {
         builder.append( String.format( "  description-details: %s%n", violation.violationSpecificMessage() ) );
      }
      if ( violation.context() != null ) {
         builder.append( String.format( "  context-element: %s%n", violation.context().elementName() ) );
         builder.append( String.format( "  context-element-full: %s%n",
               Optional.ofNullable( violation.context().element().getURI() ).orElse( "anonymous element" ) ) );
         violation.context().property().ifPresent( property -> {
            builder.append( String.format( "  context-property: %s%n", violation.context().shortUri( property.getURI() ) ) );
            builder.append( String.format( "  context-property-full: %s%n", property.getURI() ) );
         } );
      }
      if ( !violation.fixes().isEmpty() ) {
         builder.append( String.format( "  possible-fixes:%n" ) );
         for ( final Fix fix : violation.fixes() ) {
            builder.append( String.format( "  - %s%n", fix.description() ) );
         }
      }
      // Add documentation link
      builder.append( " documentation: https://eclipse-esmf.github.io/esmf-developer-guide/tooling-guide/error-codes.html"
            + violation.errorCode().toLowerCase().replace( "_", "-" ) );
      builder.append( indent( additionalAttributesSupplier.get(), 2 ) );
      if ( violation.context() != null ) {
         builder.append( String.format( "  caused-by-shape:%n" ) );
         builder.append( indent( formatShapeDetails( violation, violation.context().shape() ), 4 ) );
      }

      return builder.toString();
   }

   private String formatResource( final Violation violation, final Resource resource ) {
      return Optional.ofNullable( resource.getURI() ).map( uri -> violation.context().shortUri( uri ) ).orElse( "anonymous element" );
   }

   private String formatShapeDetails( final Violation violation, final Shape shape ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "uri: %s%n", shape.attributes().uri().orElse( "(unknown)" ) ) );
      shape.attributes().name().ifPresent( name -> builder.append( String.format( "name: %s%n", name ) ) );
      shape.attributes().message().ifPresent( message -> builder.append( String.format( "message: %s%n", message ) ) );
      shape.attributes().description().ifPresent( description -> builder.append( String.format( "description: %s%n", description ) ) );
      shape.attributes().targetClass().ifPresent( targetClass ->
            builder.append( String.format( "target-class: %s%n", formatResource( violation, targetClass ) ) ) );
      shape.attributes().targetNode().ifPresent( targetNode ->
            builder.append( String.format( "target-node: %s%n", formatResource( violation, targetNode ) ) ) );
      shape.attributes().targetObjectsOf().ifPresent( targetObjectsOf ->
            builder.append( String.format( "target-objects-of: %s%n", formatResource( violation, targetObjectsOf ) ) ) );
      shape.attributes().targetSubjectsOf().ifPresent( targetSubjectsOf ->
            builder.append( String.format( "target-subjects-of: %s%n", formatResource( violation, targetSubjectsOf ) ) ) );
      shape.attributes().targetSparql().ifPresent( targetSparql -> {
         builder.append( String.format( "sparql-target: |%n" ) );
         builder.append( indent( targetSparql.toString(), 2 ) );
      } );
      builder.append( String.format( "severity: %s%n", shape.attributes().severity() ) );
      if ( !shape.attributes().constraints().isEmpty() ) {
         builder.append( String.format( "node-constraints: %n" ) );
         for ( final Constraint constraint : shape.attributes().constraints() ) {
            builder.append( indent( formatConstraint( constraint, violation ), 2 ) );
         }
      }
      if ( shape instanceof final Shape.Node nodeShape ) {
         if ( !nodeShape.properties().isEmpty() ) {
            builder.append( String.format( "property-constraints: %n" ) );
         }
         for ( final Shape.Property propertyShape : nodeShape.properties() ) {
            if ( !propertyShape.attributes().constraints().isEmpty() ) {
               builder.append( String.format( "  - property-path: %s%n", propertyShape.path() ) );
               for ( final Constraint constraint : propertyShape.attributes().constraints() ) {
                  builder.append( indent( formatConstraint( constraint, violation ), 4 ) );
               }
            }
         }
      }
      return builder.toString();
   }

   private String formatConstraint( final Constraint constraint, final Violation violation ) {
      return String.format( "- %s%n", constraint.name() )
            + indent( constraint.accept( new ConstraintFormatter( violation ) ), 2 );
   }

   /**
    * Processing violation, e.g. a model element that could not be resolved
    *
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visitProcessingViolation( final ProcessingViolation violation ) {
      return formatViolation( violation, () -> {
         final StringBuilder builder = new StringBuilder();
         if ( violation.cause() != null
               && violation.cause() instanceof final ModelResolutionException modelResolutionException
               && !modelResolutionException.getCheckedLocations().isEmpty() ) {

            modelResolutionException.getCheckedLocations()
                  .stream()
                  .filter( failure -> failure.element().isEmpty() )
                  .forEach( failure -> {
                     builder.append( "could-not-load: " ).append( "\n" );
                     builder.append( "  - checked-location: " ).append( failure.location() ).append( "\n" );
                     builder.append( "  - reason: " ).append( failure.description() ).append( "\n" );
                  } );

            modelResolutionException.getCheckedLocations()
                  .stream()
                  .filter( failure -> failure.element().isPresent() )
                  .collect( Collectors.groupingBy( loadingFailure -> loadingFailure.element().get() ) )
                  .entrySet()
                  .stream()
                  .sorted( Map.Entry.comparingByKey() )
                  .forEach( entry -> {
                     entry.getValue().forEach( failure -> {
                        builder.append( "could-not-resolve: " );
                        builder.append( failure.element() ).append( "\n" );
                        builder.append( "  - checked-location: " ).append( failure.location() ).append( "\n" );
                        builder.append( "  - reason: " ).append( failure.description() ).append( "\n" );
                     } );
                  } );
         } else {
            if ( violation.cause() != null ) {
               builder.append( String.format( "cause: |%n" ) );
               final StringWriter stringWriter = new StringWriter();
               final PrintWriter printWriter = new PrintWriter( stringWriter );
               violation.cause().printStackTrace( printWriter );
               builder.append( indent( stringWriter.toString(), 2 ) );
            }
         }

         return builder.toString();
      } );
   }

   /**
    * Syntax error in the source file
    *
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visitInvalidSyntaxViolation( final InvalidSyntaxViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "line: %d%n", violation.line() )
                  + String.format( "column: %d%n", violation.column() )
                  + String.format( "source-context: |%n" )
                  + formatSourceLines( sourceContext( violation.source(), violation.line() ), violation.line() ) );
   }

   protected String formatSourceLines( final Map<Integer, String> lines, final long focusLine ) {
      final StringBuilder builder = new StringBuilder();
      final int prefixWidth = lines.keySet().stream().mapToInt( lineNo -> String.valueOf( lineNo ).length() + 1 ).max().orElse( 0 );
      lines.entrySet().stream().sorted( Map.Entry.comparingByKey() ).forEach( entry -> {
         final int currentLine = entry.getKey();
         final String arrow = currentLine == focusLine ? "-->" : "   ";
         builder.append( String.format( "  %s %" + ( prefixWidth - 1 ) + "d: %s%n", arrow, currentLine, entry.getValue() ) );
      } );
      return builder.toString();
   }

   @Override
   public String visitInvalidLexicalValueViolation( final InvalidLexicalValueViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "type: %s%n", RdfUtil.curie( violation.type().getURI() ) )
                  + String.format( "value: %s%n", violation.value() )
                  + String.format( "line: %d%n", violation.line() )
                  + String.format( "column: %d%n", violation.column() )
                  + String.format( "source-context: |%n" )
                  + formatSourceLines( Map.of( violation.line(), violation.sourceLine() ), violation.line() ) );
   }

   @Override
   public String visitCycleViolation( final CycleViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "properties-in-path: %s%n",
                  violation.path().stream().map( Resource::getURI ).collect( Collectors.joining( ", " ) ) ) );
   }

   @Override
   public String visitClassTypeViolation( final ClassTypeViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-class: %s%n", violation.context().shortUri( violation.allowedClass().getURI() ) )
                  + String.format( "actual-class: %s%n", violation.context().shortUri( violation.actualClass().getURI() ) ) );
   }

   @Override
   public String visitDatatypeViolation( final DatatypeViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-type: %s%n", violation.context().shortUri( violation.allowedTypeUri() ) )
                  + String.format( "actual-type: %s%n", violation.context().shortUri( violation.actualTypeUri() ) ) );
   }

   @Override
   public String visitInvalidValueViolation( final InvalidValueViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-value: %s%n", formatRdfNode( violation.allowed(), violation ) )
                  + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) ) );
   }

   @Override
   public String visitLanguageFromListViolation( final LanguageFromListViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-values: %s%n", String.join( ", ", violation.allowed() ) )
                  + String.format( "actual-value: %s%n", violation.actual() ) );
   }

   @Override
   public String visitMaxCountViolation( final MaxCountViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-value: %d%n", violation.allowed() )
                  + String.format( "actual-value: %d%n", violation.actual() ) );
   }

   @Override
   public String visitMaxExclusiveViolation( final MaxExclusiveViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "max-value: %s%n", formatRdfNode( violation.max(), violation ) )
                  + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) ) );
   }

   @Override
   public String visitMaxInclusiveViolation( final MaxInclusiveViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "max-value: %s%n", formatRdfNode( violation.max(), violation ) )
                  + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) ) );
   }

   @Override
   public String visitMaxLengthViolation( final MaxLengthViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "max-value: %d%n", violation.max() )
                  + String.format( "actual-value: %d%n", violation.actual() ) );
   }

   @Override
   public String visitMinCountViolation( final MinCountViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-value: %d%n", violation.allowed() )
                  + String.format( "actual-value: %d%n", violation.actual() ) );
   }

   @Override
   public String visitMinExclusiveViolation( final MinExclusiveViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "min-value: %s%n", formatRdfNode( violation.min(), violation ) )
                  + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) ) );
   }

   @Override
   public String visitMinInclusiveViolation( final MinInclusiveViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "min-value: %s%n", formatRdfNode( violation.min(), violation ) )
                  + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) ) );
   }

   @Override
   public String visitMinLengthViolation( final MinLengthViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "min-value: %d%n", violation.min() )
                  + String.format( "actual-value: %d%n", violation.actual() ) );
   }

   @Override
   public String visitNodeKindViolation( final NodeKindViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-nodekind: %s%n", violation.allowedNodeKind() )
                  + String.format( "actual-nodekind: %s%n", violation.actualNodeKind() ) );
   }

   @Override
   public String visitPatternViolation( final PatternViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "pattern: %s%n", violation.pattern() )
                  + String.format( "actual-value: %s%n", violation.actual() ) );
   }

   @Override
   public String visitSparqlConstraintViolation( final SparqlConstraintViolation violation ) {
      return formatViolation( violation, () -> {
         final StringBuilder builder = new StringBuilder();
         builder.append( String.format( "bindings:%n" ) );
         for ( final Map.Entry<String, RDFNode> entry : violation.bindings().entrySet() ) {
            builder.append( String.format( "  - %s --> %s%n", entry.getKey(), formatRdfNode( entry.getValue(), violation ) ) );
         }
         return builder.toString();
      } );
   }

   @Override
   public String visitJsViolation( final JsConstraintViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "js-library: %s%n",
                  violation.library().uri().map( uri -> violation.context().shortUri( uri ) ).orElse( "anonymous element" ) )
                  + String.format( "js-function: %s%n", violation.functionName() ) );
   }

   @Override
   public String visitUniqueLanguageViolation( final UniqueLanguageViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "duplicates: %s%n", String.join( ", ", violation.duplicates() ) ) );
   }

   @Override
   public String visitEqualsViolation( final EqualsViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-value: %s%n", formatRdfNode( violation.allowedValue(), violation ) )
                  + String.format( "actual-value: %s%n", formatRdfNode( violation.actualValue(), violation ) ) );
   }

   @Override
   public String visitDisjointViolation( final DisjointViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "other-property: %s%n", violation.context().shortUri( violation.otherProperty().getURI() ) )
                  + String.format( "other-value: %s%n", formatRdfNode( violation.otherValue(), violation ) ) );
   }

   @Override
   public String visitLessThanViolation( final LessThanViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "other-property: %s%n", violation.context().shortUri( violation.otherProperty().getURI() ) )
                  + String.format( "other-value: %s%n", formatRdfNode( violation.otherValue(), violation ) ) );
   }

   @Override
   public String visitLessThanOrEqualsViolation( final LessThanOrEqualsViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "other-property: %s%n", violation.context().shortUri( violation.otherProperty().getURI() ) )
                  + String.format( "other-value: %s%n", formatRdfNode( violation.otherValue(), violation ) ) );
   }

   @Override
   public String visitValueFromListViolation( final ValueFromListViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-values: %s%n",
                  violation.allowed().stream().map( rdfNode -> formatRdfNode( rdfNode, violation ) ).collect( Collectors.joining( ", " ) ) )
                  + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) ) );
   }

   @Override
   public String visitClosedViolation( final ClosedViolation violation ) {
      return formatViolation( violation, () ->
            String.format( "allowed-properties: %s%n", violation.allowedProperties().stream()
                  .map( Property::getURI ).map( uri -> violation.context().shortUri( uri ) ).collect( Collectors.joining( ", " ) ) )
                  + String.format( "ignored-properties: %s%n", violation.ignoredProperties().stream()
                  .map( Property::getURI ).map( uri -> violation.context().shortUri( uri ) ).collect( Collectors.joining( ", " ) ) )
                  + String.format( "actual: %s%n", violation.context().shortUri( violation.actual().getURI() ) ) );
   }

   @Override
   public String visitNotViolation( final NotViolation violation ) {
      return formatViolation( violation, () -> {
         final StringBuilder builder = new StringBuilder();
         final ConstraintFormatter constraintFormatter = new ConstraintFormatter( violation );
         builder.append( String.format( "not:%n" ) );
         builder.append( indent( violation.negatedConstraint().accept( constraintFormatter ), 2 ) );
         return builder.toString();
      } );
   }

   private String formatRdfNode( final RDFNode node, final Violation violation ) {
      if ( node.isURIResource() ) {
         return violation.context().shortUri( node.asResource().getURI() );
      }
      if ( node.isResource() ) {
         return "anonymous element";
      }
      if ( node.isLiteral() ) {
         final Literal literal = node.asLiteral();
         if ( literal.getDatatypeURI().equals( XSD.xstring.getURI() ) ) {
            return "\"" + literal.getLexicalForm() + "\"";
         }
         if ( literal.getDatatypeURI().equals( XSD.xboolean.getURI() ) || literal.getDatatypeURI().equals( XSD.integer.getURI() ) ) {
            return literal.getLexicalForm();
         }
         return String.format( "\"%s\"^^%s", literal.getLexicalForm(), violation.context().shortUri( literal.getDatatypeURI() ) );
      }
      return "?";
   }

   private class ConstraintFormatter implements Constraint.Visitor<String> {
      private final Violation violation;

      private ConstraintFormatter( final Violation violation ) {
         this.violation = violation;
      }

      @Override
      public String visit( final Constraint constraint ) {
         return "";
      }

      @Override
      public String visitAllowedLanguagesConstraint( final AllowedLanguagesConstraint constraint ) {
         return String.format( "allowed-languages: %s%n", String.join( ", ", constraint.allowedLanguages() ) );
      }

      @Override
      public String visitAllowedValuesConstraint( final AllowedValuesConstraint constraint ) {
         return String.format( "allowed-values: %s%n",
               String.join( ", ", constraint.allowedValues().stream().map( node -> formatRdfNode( node, violation ) ).toList() ) );
      }

      @Override
      public String visitAndConstraint( final AndConstraint constraint ) {
         final StringBuilder builder = new StringBuilder();
         printNestedShapes( builder, constraint.shapes() );
         return builder.toString();
      }

      @Override
      public String visitClassConstraint( final ClassConstraint constraint ) {
         return String.format( "allowed-class: %s%n", formatRdfNode( constraint.allowedClass(), violation ) );
      }

      @Override
      public String visitClosedConstraint( final ClosedConstraint constraint ) {
         if ( constraint.ignoredProperties().isEmpty() ) {
            return "";
         }
         final StringBuilder builder = new StringBuilder();
         builder.append( String.format( "ignored-properties:%n" ) );
         for ( final Resource ignored : constraint.ignoredProperties() ) {
            builder.append( String.format( "  - %s%n", formatRdfNode( ignored, violation ) ) );
         }
         return builder.toString();
      }

      @Override
      public String visitDatatypeConstraint( final DatatypeConstraint constraint ) {
         return String.format( "allowed-type: %s%n", violation.context().shortUri( constraint.allowedTypeUri() ) );
      }

      @Override
      public String visitDisjointConstraint( final DisjointConstraint constraint ) {
         return String.format( "disjoint-with: %s%n", violation.context().shortUri( constraint.otherProperty().getURI() ) );
      }

      @Override
      public String visitEqualsConstraint( final EqualsConstraint constraint ) {
         return String.format( "equal-with: %s%n", violation.context().shortUri( constraint.otherProperty().getURI() ) );
      }

      @Override
      public String visitHasValueConstraint( final HasValueConstraint constraint ) {
         return String.format( "allowed-value: %s%n", formatRdfNode( constraint.allowedValue(), violation ) );
      }

      @Override
      public String visitLessThanConstraint( final LessThanConstraint constraint ) {
         return String.format( "less-than: %s%n", violation.context().shortUri( constraint.otherProperty().getURI() ) );
      }

      @Override
      public String visitLessThanOrEqualsConstraint( final LessThanOrEqualsConstraint constraint ) {
         return String.format( "less-than-or-equal: %s%n", violation.context().shortUri( constraint.otherProperty().getURI() ) );
      }

      @Override
      public String visitMaxCountConstraint( final MaxCountConstraint constraint ) {
         return String.format( "max-count: %d%n", constraint.maxCount() );
      }

      @Override
      public String visitMaxExclusiveConstraint( final MaxExclusiveConstraint constraint ) {
         return String.format( "max-value: %s%n", constraint.maxValue().getLexicalForm() );
      }

      @Override
      public String visitMaxInclusiveConstraint( final MaxInclusiveConstraint constraint ) {
         return String.format( "max-value: %s%n", constraint.maxValue().getLexicalForm() );
      }

      @Override
      public String visitMaxLengthConstraint( final MaxLengthConstraint constraint ) {
         return String.format( "max-length: %d%n", constraint.maxLength() );
      }

      @Override
      public String visitMinCountConstraint( final MinCountConstraint constraint ) {
         return String.format( "min-count: %d%n", constraint.minCount() );
      }

      @Override
      public String visitMinExclusiveConstraint( final MinExclusiveConstraint constraint ) {
         return String.format( "min-value: %s%n", constraint.minValue().getLexicalForm() );
      }

      @Override
      public String visitMinInclusiveConstraint( final MinInclusiveConstraint constraint ) {
         return String.format( "min-value: %s%n", constraint.minValue().getLexicalForm() );
      }

      @Override
      public String visitMinLengthConstraint( final MinLengthConstraint constraint ) {
         return String.format( "min-length: %d%n", constraint.minLength() );
      }

      @Override
      public String visitNodeConstraint( final NodeConstraint constraint ) {
         return String.format( "shape-node: %s%n",
               constraint.targetShape().get().attributes().uri().map( uri -> violation.context().shortUri( uri ) )
                     .orElse( "(anonymous shape)" ) );
      }

      @Override
      public String visitNodeKindConstraint( final NodeKindConstraint constraint ) {
         return String.format( "allowed-node-kind: %s%n", constraint.allowedNodeKind() );
      }

      @Override
      public String visitNotConstraint( final NotConstraint constraint ) {
         final StringBuilder builder = new StringBuilder();
         builder.append( String.format( "negated:%n" ) );
         final Constraint negatedConstraint = constraint.constraint();
         builder.append( String.format( "  - %s%n", negatedConstraint.getClass().getSimpleName() ) );
         builder.append( indent( negatedConstraint.accept( this ), 4 ) );
         return builder.toString();
      }

      private void printNestedShapes( final StringBuilder builder, final List<Shape> shapes ) {
         builder.append( String.format( "shapes:%n" ) );
         builder.append( indent( formatShapeDetails( violation, violation.context().shape() ), 4 ) );
      }

      @Override
      public String visitOrConstraint( final OrConstraint constraint ) {
         final StringBuilder builder = new StringBuilder();
         printNestedShapes( builder, constraint.shapes() );
         return builder.toString();
      }

      @Override
      public String visitPatternConstraint( final PatternConstraint constraint ) {
         return String.format( "pattern: %s%n", constraint.pattern() );
      }

      @Override
      public String visitSparqlConstraint( final SparqlConstraint constraint ) {
         final StringBuilder builder = new StringBuilder();
         builder.append( String.format( "message: %s%n", constraint.message() ) );
         builder.append( String.format( "query: |%n" ) );
         builder.append( indent( constraint.query().toString(), 2 ) ).append( "\n" );
         return builder.toString();
      }

      @Override
      public String visitXoneConstraint( final XoneConstraint constraint ) {
         final StringBuilder builder = new StringBuilder();
         printNestedShapes( builder, constraint.shapes() );
         return builder.toString();
      }
   }

   private static class FixFormatter implements Fix.Visitor<String> {
      @Override
      public String visit( final Fix fix ) {
         return fix.description();
      }
   }
}
