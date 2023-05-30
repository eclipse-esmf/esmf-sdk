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
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedLanguagesConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedValuesConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AndConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.ClosedConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.Constraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.EqualsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.HasValueConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.LessThanOrEqualsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxInclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxLengthConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinInclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinLengthConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeKindConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NotConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.SparqlConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.XoneConstraint;
import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClassTypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClosedViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.DisjointViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.EqualsViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidSyntaxViolation;
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
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.UniqueLanguageViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ValueFromListViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.eclipse.esmf.aspectmodel.shacl.constraint.ClassConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DatatypeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DisjointConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.LessThanConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxExclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinExclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.OrConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.PatternConstraint;

/**
 * Formats one or multiple {@link Violation}s in a human-readable way and provides detailed information.
 * Note that this is intended only for places with raw textual output, such as a text console.
 * For a more sensible representation of violations in other contexts, implement {@link Violation.Visitor}.
 */
public class DetailedViolationFormatter extends ViolationFormatter {
   @Override
   protected String processNonSemanticViolation( final List<Violation> violations ) {
      if ( violations.isEmpty() ) {
         return "# Input model is valid";
      }

      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "# Processing violations were found:%n" ) );
      for ( final Violation violation : violations ) {
         builder.append( String.format( "- violation-type: %s%n", violation.getClass().getSimpleName() ) );
         builder.append( String.format( "  error-code: %s%n", violation.errorCode() ) );
         builder.append( String.format( "  description: %s%n", violation.message() ) );
         for ( final String line : violation.accept( this ).split( "\n" ) ) {
            builder.append( String.format( "  %s%n", line ) );
         }
         builder.append( String.format( "%n" ) );
      }
      return builder.toString();
   }

   @Override
   protected String processSemanticViolations( final List<Violation> violations ) {
      if ( violations.isEmpty() ) {
         return "# Input model is valid";
      }

      final Map<? extends Class<? extends Violation>, List<Violation>> violationsByType =
            violations.stream().collect( Collectors.groupingBy( Violation::getClass ) );
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "# Semantic violations were found:%n" ) );
      for ( final Map.Entry<? extends Class<? extends Violation>, List<Violation>> entry : violationsByType.entrySet() ) {
         for ( final Violation violation : entry.getValue() ) {
            builder.append( String.format( "- violation-type: %s%n", entry.getKey().getSimpleName() ) );
            builder.append( String.format( "  error-code: %s%n", violation.errorCode() ) );
            builder.append( String.format( "  description: %s%n", violation.message() ) );
            builder.append( String.format( "  context-element: %s%n", violation.elementName() ) );
            builder.append( String.format( "  context-element-full: %s%n",
                  Optional.ofNullable( violation.context().element().getURI() ).orElse( "anonymous element" ) ) );
            violation.context().property().ifPresent( property -> {
               builder.append( String.format( "  context-property: %s%n", violation.shortUri( property.getURI() ) ) );
               builder.append( String.format( "  context-property-full: %s%n", property.getURI() ) );
            } );
            if ( violation.fixes().size() > 0 ) {
               builder.append( String.format( "  possible-fixes:%n" ) );
               for ( final Fix fix : violation.fixes() ) {
                  builder.append( String.format( "  - %s%n", fix.description() ) );
               }
            }

            for ( final String line : violation.accept( this ).split( "\n" ) ) {
               builder.append( String.format( "  %s%n", line ) );
            }
            builder.append( String.format( "  caused-by-shape:%n" ) );
            for ( final String line : formatShapeDetails( violation, violation.context().shape() ).split( "\n" ) ) {
               builder.append( String.format( "    %s%n", line ) );
            }
            builder.append( String.format( "%n" ) );
         }
      }
      return builder.toString();
   }

   private String formatResource( final Violation violation, final Resource resource ) {
      return Optional.ofNullable( resource.getURI() ).map( violation::shortUri ).orElse( "anonymous element" );
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
         for ( final String line : targetSparql.toString().split( "\n" ) ) {
            builder.append( String.format( "  %s%n", line ) );
         }
      } );
      builder.append( String.format( "severity: %s%n", shape.attributes().severity() ) );
      if ( !shape.attributes().constraints().isEmpty() ) {
         builder.append( String.format( "node-constraints: %n" ) );
         for ( final Constraint constraint : shape.attributes().constraints() ) {
            for ( final String line : formatConstraint( constraint, violation ).split( "\n" ) ) {
               builder.append( String.format( "  %s%n", line ) );
            }
         }
      }
      if ( shape instanceof Shape.Node nodeShape ) {
         if ( !nodeShape.properties().isEmpty() ) {
            builder.append( String.format( "property-constraints: %n" ) );
         }
         for ( final Shape.Property propertyShape : nodeShape.properties() ) {
            if ( !propertyShape.attributes().constraints().isEmpty() ) {
               builder.append( String.format( "  - property-path: %s%n", propertyShape.path() ) );
               for ( final Constraint constraint : propertyShape.attributes().constraints() ) {
                  for ( final String line : formatConstraint( constraint, violation ).split( "\n" ) ) {
                     builder.append( String.format( "    %s%n", line ) );
                  }
               }
            }
         }
      }
      return builder.toString();
   }

   private String formatConstraint( final Constraint constraint, final Violation violation ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "- %s%n", constraint.name() ) );
      final ConstraintFormatter constraintFormatter = new ConstraintFormatter( violation );
      for ( final String line : constraint.accept( constraintFormatter ).split( "\n" ) ) {
         builder.append( String.format( "  %s%n", line ) );
      }
      return builder.toString();
   }

   /**
    * Processing violation, e.g. a model element that could not be resolved
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visitProcessingViolation( final ProcessingViolation violation ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "cause: |%n" ) );
      final StringWriter stringWriter = new StringWriter();
      final PrintWriter printWriter = new PrintWriter( stringWriter );
      violation.cause().printStackTrace( printWriter );
      for ( final String line : stringWriter.toString().split( "\n" ) ) {
         builder.append( String.format( "  %s%n", line ) );
      }
      return builder.toString();
   }

   /**
    * Syntax error in the source file
    * @param violation the violation
    * @return formatted representation
    */
   @Override
   public String visitInvalidSyntaxViolation( final InvalidSyntaxViolation violation ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "line: %d%n", violation.line() ) );
      builder.append( String.format( "column: %d%n", violation.column() ) );
      builder.append( String.format( "source-context: |%n" ) );
      printSyntaxViolationSource( violation, builder, "  " );
      return builder.toString();
   }

   @Override
   public String visitClassTypeViolation( final ClassTypeViolation violation ) {
      return String.format( "allowed-class: %s%n", violation.shortUri( violation.allowedClass().getURI() ) )
            + String.format( "actual-class: %s%n", violation.shortUri( violation.actualClass().getURI() ) );
   }

   @Override
   public String visitDatatypeViolation( final DatatypeViolation violation ) {
      return String.format( "allowed-type: %s%n", violation.shortUri( violation.allowedTypeUri() ) )
            + String.format( "actual-type: %s%n", violation.shortUri( violation.actualTypeUri() ) );
   }

   @Override
   public String visitInvalidValueViolation( final InvalidValueViolation violation ) {
      return String.format( "allowed-value: %s%n", formatRdfNode( violation.allowed(), violation ) )
            + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) );
   }

   @Override
   public String visitLanguageFromListViolation( final LanguageFromListViolation violation ) {
      return String.format( "allowed-values: %s%n", String.join( ", ", violation.allowed() ) )
            + String.format( "actual-value: %s%n", violation.actual() );
   }

   @Override
   public String visitMaxCountViolation( final MaxCountViolation violation ) {
      return String.format( "allowed-value: %d%n", violation.allowed() )
            + String.format( "actual-value: %d%n", violation.actual() );
   }

   @Override
   public String visitMaxExclusiveViolation( final MaxExclusiveViolation violation ) {
      return String.format( "max-value: %s%n", formatRdfNode( violation.max(), violation ) )
            + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) );
   }

   @Override
   public String visitMaxInclusiveViolation( final MaxInclusiveViolation violation ) {
      return String.format( "max-value: %s%n", formatRdfNode( violation.max(), violation ) )
            + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) );
   }

   @Override
   public String visitMaxLengthViolation( final MaxLengthViolation violation ) {
      return String.format( "max-value: %d%n", violation.max() )
            + String.format( "actual-value: %d%n", violation.actual() );
   }

   @Override
   public String visitMinCountViolation( final MinCountViolation violation ) {
      return String.format( "allowed-value: %d%n", violation.allowed() )
            + String.format( "actual-value: %d%n", violation.actual() );
   }

   @Override
   public String visitMinExclusiveViolation( final MinExclusiveViolation violation ) {
      return String.format( "min-value: %s%n", formatRdfNode( violation.min(), violation ) )
            + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) );
   }

   @Override
   public String visitMinInclusiveViolation( final MinInclusiveViolation violation ) {
      return String.format( "min-value: %s%n", formatRdfNode( violation.min(), violation ) )
            + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) );
   }

   @Override
   public String visitMinLengthViolation( final MinLengthViolation violation ) {
      return String.format( "min-value: %d%n", violation.min() )
            + String.format( "actual-value: %d%n", violation.actual() );
   }

   @Override
   public String visitNodeKindViolation( final NodeKindViolation violation ) {
      return String.format( "allowed-nodekind: %s%n", violation.allowedNodeKind() )
            + String.format( "actual-nodekind: %s%n", violation.actualNodeKind() );
   }

   @Override
   public String visitPatternViolation( final PatternViolation violation ) {
      return String.format( "pattern: %s%n", violation.pattern() )
            + String.format( "actual-value: %s%n", violation.actual() );
   }

   @Override
   public String visitSparqlConstraintViolation( final SparqlConstraintViolation violation ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( String.format( "bindings:%n" ) );
      for ( final Map.Entry<String, RDFNode> entry : violation.bindings().entrySet() ) {
         builder.append( String.format( "  - %s --> %s%n", entry.getKey(), formatRdfNode( entry.getValue(), violation ) ) );
      }
      return builder.toString();
   }

   @Override
   public String visitJsViolation( final JsConstraintViolation violation ) {
      return String.format( "js-library: %s%n", violation.library().uri().map( violation::shortUri ).orElse( "anonymous element" ) )
            + String.format( "js-function: %s%n", violation.functionName() );
   }

   @Override
   public String visitUniqueLanguageViolation( final UniqueLanguageViolation violation ) {
      return String.format( "duplicates: %s%n", String.join( ", ", violation.duplicates() ) );
   }

   @Override
   public String visitEqualsViolation( final EqualsViolation violation ) {
      return String.format( "allowed-value: %s%n", formatRdfNode( violation.allowedValue(), violation ) )
            + String.format( "actual-value: %s%n", formatRdfNode( violation.actualValue(), violation ) );
   }

   @Override
   public String visitDisjointViolation( final DisjointViolation violation ) {
      return String.format( "other-property: %s%n", violation.shortUri( violation.otherProperty().getURI() ) )
            + String.format( "other-value: %s%n", formatRdfNode( violation.otherValue(), violation ) );
   }

   @Override
   public String visitLessThanViolation( final LessThanViolation violation ) {
      return String.format( "other-property: %s%n", violation.shortUri( violation.otherProperty().getURI() ) )
            + String.format( "other-value: %s%n", formatRdfNode( violation.otherValue(), violation ) );
   }

   @Override
   public String visitLessThanOrEqualsViolation( final LessThanOrEqualsViolation violation ) {
      return String.format( "other-property: %s%n", violation.shortUri( violation.otherProperty().getURI() ) )
            + String.format( "other-value: %s%n", formatRdfNode( violation.otherValue(), violation ) );
   }

   @Override
   public String visitValueFromListViolation( final ValueFromListViolation violation ) {
      return String.format( "allowed-values: %s%n",
            violation.allowed().stream().map( rdfNode -> formatRdfNode( rdfNode, violation ) ).collect( Collectors.joining( ", " ) ) )
            + String.format( "actual-value: %s%n", formatRdfNode( violation.actual(), violation ) );
   }

   @Override
   public String visitClosedViolation( final ClosedViolation violation ) {
      return String.format( "allowed-properties: %s%n", violation.allowedProperties().stream()
            .map( Property::getURI ).map( violation::shortUri ).collect( Collectors.joining( ", " ) ) )
            + String.format( "ignored-properties: %s%n", violation.ignoredProperties().stream()
            .map( Property::getURI ).map( violation::shortUri ).collect( Collectors.joining( ", " ) ) )
            + String.format( "actual: %s%n", violation.shortUri( violation.actual().getURI() ) );
   }

   @Override
   public String visitNotViolation( final NotViolation violation ) {
      final StringBuilder builder = new StringBuilder();
      final ConstraintFormatter constraintFormatter = new ConstraintFormatter( violation );
      builder.append( String.format( "not:%n" ) );
      for ( final String line : violation.negatedConstraint().accept( constraintFormatter ).split( "\n" ) ) {
         builder.append( String.format( "  %s%n", line ) );
      }
      return builder.toString();
   }

   private String formatRdfNode( final RDFNode node, final Violation violation ) {
      if ( node.isURIResource() ) {
         return violation.shortUri( node.asResource().getURI() );
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
         return String.format( "\"%s\"^^%s", literal.getLexicalForm(), violation.shortUri( literal.getDatatypeURI() ) );
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
         printNestedConstraints( builder, constraint.constraints() );
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
         return String.format( "allowed-type: %s%n", violation.shortUri( constraint.allowedTypeUri() ) );
      }

      @Override
      public String visitDisjointConstraint( final DisjointConstraint constraint ) {
         return String.format( "disjoint-with: %s%n", violation.shortUri( constraint.otherProperty().getURI() ) );
      }

      @Override
      public String visitEqualsConstraint( final EqualsConstraint constraint ) {
         return String.format( "equal-with: %s%n", violation.shortUri( constraint.otherProperty().getURI() ) );
      }

      @Override
      public String visitHasValueConstraint( final HasValueConstraint constraint ) {
         return String.format( "allowed-value: %s%n", formatRdfNode( constraint.allowedValue(), violation ) );
      }

      @Override
      public String visitLessThanConstraint( final LessThanConstraint constraint ) {
         return String.format( "less-than: %s%n", violation.shortUri( constraint.otherProperty().getURI() ) );
      }

      @Override
      public String visitLessThanOrEqualsConstraint( final LessThanOrEqualsConstraint constraint ) {
         return String.format( "less-than-or-equal: %s%n", violation.shortUri( constraint.otherProperty().getURI() ) );
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
         return String.format( "shape-node: %s%n", constraint.targetShape().attributes().uri().map( violation::shortUri ).orElse( "(anonymous shape)" ) );
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
         for ( final String line : negatedConstraint.accept( this ).split( "\n" ) ) {
            builder.append( String.format( "    %s%n", line ) );
         }
         return builder.toString();
      }

      private void printNestedConstraints( final StringBuilder builder, final List<Constraint> constraints ) {
         builder.append( String.format( "constraints:%n" ) );
         for ( final Constraint c : constraints ) {
            builder.append( String.format( "  - %s%n", c.getClass().getSimpleName() ) );
            for ( final String line : c.accept( this ).split( "\n" ) ) {
               builder.append( String.format( "    %s%n", line ) );
            }
         }
      }

      @Override
      public String visitOrConstraint( final OrConstraint constraint ) {
         final StringBuilder builder = new StringBuilder();
         printNestedConstraints( builder, constraint.constraints() );
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
         for ( final String line : constraint.query().toString().split( "\n" ) ) {
            builder.append( String.format( "  %s%n", line ) );
         }
         return builder.toString();
      }

      @Override
      public String visitXoneConstraint( final XoneConstraint constraint ) {
         final StringBuilder builder = new StringBuilder();
         printNestedConstraints( builder, constraint.constraints() );
         return builder.toString();
      }
   }
}
