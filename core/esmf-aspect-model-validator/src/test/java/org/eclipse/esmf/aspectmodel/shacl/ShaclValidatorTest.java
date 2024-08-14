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

package org.eclipse.esmf.aspectmodel.shacl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.aspectmodel.RdfUtil.createModel;

import java.math.BigInteger;
import java.util.List;

import org.eclipse.esmf.aspectmodel.shacl.constraint.DatatypeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeKindConstraint;
import org.eclipse.esmf.aspectmodel.shacl.path.PredicatePath;
import org.eclipse.esmf.aspectmodel.shacl.path.SequencePath;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClassTypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClosedViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.DisjointViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.EqualsViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
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
import org.eclipse.esmf.aspectmodel.shacl.violation.OrViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.PatternViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.UniqueLanguageViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ValueFromListViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.shacl.violation.XoneViolation;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationRustLikeFormatter;

import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;

/**
 * This class tests the internals of the {@link ShaclValidator}
 */
public class ShaclValidatorTest {
   private final String namespace = "http://example.com#";

   final ViolationRustLikeFormatter rustLikeFormatter = new ViolationRustLikeFormatter();

   @Test
   public void testLoadingCustomShape() {
      final Model shapesModel = createModel( """
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:integer ;
                  sh:minCount 1 ;
                  sh:name "Test property" ;
                  sh:description "Test description" ;
               ] ;
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      assertThat( validator.getShapes().size() ).isEqualTo( 1 );

      final Shape.Node shape = validator.getShapes().get( 0 );
      assertThat( shape.attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( shape.attributes().name() ).hasValue( "Test shape" );
      assertThat( shape.attributes().description() ).hasValue( "Test shape description" );
      assertThat( shape.attributes().targetClass() ).hasValue( ResourceFactory.createResource( namespace + "TestClass" ) );

      assertThat( shape.properties().size() ).isEqualTo( 1 );
      final Shape.Property property = shape.properties().get( 0 );
      assertThat( property.attributes().name() ).hasValue( "Test property" );
      assertThat( property.attributes().description() ).hasValue( "Test description" );
      assertThat( property.attributes().constraints() ).hasOnlyElementsOfTypes( DatatypeConstraint.class, MinCountConstraint.class );
      assertThat( property.attributes().constraints() ).hasAtLeastOneElementOfType( DatatypeConstraint.class );
      assertThat( property.attributes().constraints() ).hasAtLeastOneElementOfType( MinCountConstraint.class );

      assertThat( property.path() ).isEqualTo( new PredicatePath( ResourceFactory.createProperty( namespace + "testProperty" ) ) );
   }

   @Test
   public void testClassConstraintEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:class :TestClass2 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

            :MySuperType a :TestClass2 .
            :MyType rdfs:subClassOf :MySuperType .

            :Bar a :TestClass ;
              :testProperty [ a :MyType ] .

            :Foo a :TestClass ;
              :testProperty [ a :SomethingElse ] .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( ClassTypeViolation.class );
      final ClassTypeViolation violation = (ClassTypeViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actualClass().getURI() ).isEqualTo( "http://example.com#SomethingElse" );
      assertThat( violation.allowedClass().getURI() ).isEqualTo( "http://example.com#TestClass2" );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo has type :SomethingElse, but only :TestClass2 is allowed." );
      assertThat( violation.errorCode() ).isEqualTo( ClassTypeViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 12" );
      assertThat( formattedMessage ).contains( ":testProperty [ a :SomethingElse ] ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":SomethingElse".length() ) );
   }

   @Test
   public void testDatatypeConstraintEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( DatatypeViolation.class );
      final DatatypeViolation violation = (DatatypeViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actualTypeUri() ).isEqualTo( XSD.integer.getURI() );
      assertThat( violation.allowedTypeUri() ).isEqualTo( XSD.xstring.getURI() );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo uses data type xsd:integer, but only xsd:string is allowed." );
      assertThat( violation.errorCode() ).isEqualTo( DatatypeViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty 42 ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testNodeKindConstraintEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:nodeKind sh:IRI ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( NodeKindViolation.class );
      final NodeKindViolation violation = (NodeKindViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.IRI );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty on :Foo is a value, but it must be a named element." );
      assertThat( violation.errorCode() ).isEqualTo( NodeKindViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty 42 ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testMinCountConstraintEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:minCount 1 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.listSubjects().nextResource().asResource();
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MinCountViolation.class );
      final MinCountViolation violation = (MinCountViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.message() ).isEqualTo( "Mandatory property :testProperty is missing on :Foo." );
      assertThat( violation.errorCode() ).isEqualTo( MinCountViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 2" );
      assertThat( formattedMessage ).contains( ":Foo a :TestClass ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":Foo".length() ) );
   }

   @Test
   public void testMaxCountEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:maxCount 1 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "foo" ;
              :testProperty "bar" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 2 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MaxCountViolation.class );
      final MaxCountViolation violation = (MaxCountViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty is used 2 times on :Foo, but may only be used 1 time." );
      assertThat( violation.errorCode() ).isEqualTo( MaxCountViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty \"bar\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testMinExclusiveConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:minExclusive 42 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MinExclusiveViolation.class );
      final MinExclusiveViolation violation = (MinExclusiveViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual().getInt() ).isEqualTo( 42 );
      assertThat( violation.min().getInt() ).isEqualTo( 42 );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty on :Foo has value 42, but it must be greater than 42." );
      assertThat( violation.errorCode() ).isEqualTo( MinExclusiveViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty 42 ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "42".length() ) );
   }

   @Test
   public void testMinInclusiveConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:minInclusive 42 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 41 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MinInclusiveViolation.class );
      final MinInclusiveViolation violation = (MinInclusiveViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual().getInt() ).isEqualTo( 41 );
      assertThat( violation.min().getInt() ).isEqualTo( 42 );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo has value 41, but it must be greater than or equal to 42." );
      assertThat( violation.errorCode() ).isEqualTo( MinInclusiveViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty 41 ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "41".length() ) );
   }

   @Test
   public void testMaxExclusiveConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:maxExclusive 42 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MaxExclusiveViolation.class );
      final MaxExclusiveViolation violation = (MaxExclusiveViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual().getInt() ).isEqualTo( 42 );
      assertThat( violation.max().getInt() ).isEqualTo( 42 );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty on :Foo has value 42, but it must be less than 42." );
      assertThat( violation.errorCode() ).isEqualTo( MaxExclusiveViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty 42 ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "42".length() ) );
   }

   @Test
   public void testMaxInclusiveConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:maxInclusive 42 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 43 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MaxInclusiveViolation.class );
      final MaxInclusiveViolation violation = (MaxInclusiveViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual().getInt() ).isEqualTo( 43 );
      assertThat( violation.max().getInt() ).isEqualTo( 42 );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo has value 43, but it must be less than or equal to 42." );
      assertThat( violation.errorCode() ).isEqualTo( MaxInclusiveViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty 43 ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "43".length() ) );
   }

   @Test
   public void testMinLengthConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:minLength 5 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "abc" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MinLengthViolation.class );
      final MinLengthViolation violation = (MinLengthViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual() ).isEqualTo( 3 );
      assertThat( violation.min() ).isEqualTo( 5 );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo has length 3, but its length must be greater than or equal to 5." );
      assertThat( violation.errorCode() ).isEqualTo( MinLengthViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty \"abc\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testMaxLengthConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:maxLength 5 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "abcabc" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MaxLengthViolation.class );
      final MaxLengthViolation violation = (MaxLengthViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual() ).isEqualTo( 6 );
      assertThat( violation.max() ).isEqualTo( 5 );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo has length 6, but its length must be less than or equal to 5." );
      assertThat( violation.errorCode() ).isEqualTo( MaxLengthViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty \"abcabc\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testPatternConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:pattern "^x" ;
                  sh:flags "i" ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "y" .

            :Bar a :TestClass ;
              :testProperty "X" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( PatternViolation.class );
      final PatternViolation violation = (PatternViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual() ).isEqualTo( "y" );
      assertThat( violation.pattern() ).isEqualTo( "^x" );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo has value y, which does not match the required pattern ^x." );
      assertThat( violation.errorCode() ).isEqualTo( PatternViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty \"y\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testAllowedLanguageConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:languageIn ( "en" "de" ) ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "non valide"@fr .

            :Bar a :TestClass ;
              :testProperty "valid"@en .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( LanguageFromListViolation.class );
      final LanguageFromListViolation violation = (LanguageFromListViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual() ).isEqualTo( "fr" );
      assertThat( violation.allowed() ).containsExactlyInAnyOrder( "en", "de" );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo has language tag fr, which is not in the list of allowed languages: [en, de]." );
      assertThat( violation.errorCode() ).isEqualTo( LanguageFromListViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty \"non valide\"@fr ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testEqualsConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:equals :anotherTestProperty ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "some value" ;
              :anotherTestProperty "a different value" .

            :Bar a :TestClass ;
              :testProperty "some value" ;
              :anotherTestProperty "some value" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( EqualsViolation.class );
      final EqualsViolation violation = (EqualsViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedValue().asLiteral().getString() ).isEqualTo( "a different value" );
      assertThat( violation.actualValue().asLiteral().getString() ).isEqualTo( "some value" );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo must have the same value as property :anotherTestProperty (a different value), but has value "
                  + "some value." );
      assertThat( violation.errorCode() ).isEqualTo( EqualsViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty \"some value\" ;" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "\"some value\"".length() ) );
   }

   @Test
   public void testDisjointConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:disjoint :anotherTestProperty ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "some value" ;
              :anotherTestProperty "some value" .

            :Bar a :TestClass ;
              :testProperty "some value" ;
              :anotherTestProperty "a different value" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( DisjointViolation.class );
      final DisjointViolation violation = (DisjointViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.otherValue().asLiteral().getString() ).isEqualTo( "some value" );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo may not have the same value as property :anotherTestProperty (some value)." );
      assertThat( violation.errorCode() ).isEqualTo( DisjointViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty \"some value\" ;" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testLessThanConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:lessThan :anotherTestProperty ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 10 ;
              :anotherTestProperty 5 .

            :Bar a :TestClass ;
              :testProperty 10 ;
              :anotherTestProperty 20 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( LessThanViolation.class );
      final LessThanViolation violation = (LessThanViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actualValue().getInt() ).isEqualTo( 10 );
      assertThat( violation.otherValue().getInt() ).isEqualTo( 5 );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo must have a value that is less than that of :anotherTestProperty: 10 must be less than 5." );
      assertThat( violation.errorCode() ).isEqualTo( LessThanViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty 10 ;" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "10".length() ) );
   }

   @Test
   public void testLessThanOrEqualsConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:lessThanOrEquals :anotherTestProperty ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 10 ;
              :anotherTestProperty 5 .

            :Bar a :TestClass ;
              :testProperty 10 ;
              :anotherTestProperty 10 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( LessThanOrEqualsViolation.class );
      final LessThanOrEqualsViolation violation = (LessThanOrEqualsViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actualValue().getInt() ).isEqualTo( 10 );
      assertThat( violation.otherValue().getInt() ).isEqualTo( 5 );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo must have a value that is less than or equal to that of :anotherTestProperty: 10 must be less"
                  + " than 5." );
      assertThat( violation.errorCode() ).isEqualTo( LessThanOrEqualsViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty 10 ;" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "10".length() ) );
   }

   @Test
   public void testUniqueLangConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:uniqueLang true ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "hello"@en ;
              :testProperty "hello again"@en .

            :Bar a :TestClass ;
              :testProperty "hello"@en ;
              :testProperty "hallo"@de .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 2 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( UniqueLanguageViolation.class );
      final UniqueLanguageViolation violation = (UniqueLanguageViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.duplicates() ).containsExactly( "en" );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty on :Foo uses language tag that has been used already: [en]." );
      assertThat( violation.errorCode() ).isEqualTo( UniqueLanguageViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty \"hello again\"@en ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testHasValueConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:hasValue 42 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "hello" .

            :Bar a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( InvalidValueViolation.class );
      final InvalidValueViolation violation = (InvalidValueViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getInt() ).isEqualTo( 42 );
      assertThat( violation.actual().asLiteral().getString() ).isEqualTo( "hello" );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty on :Foo has value hello, but only 42 is allowed." );
      assertThat( violation.errorCode() ).isEqualTo( InvalidValueViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty \"hello\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "\"hello\"".length() ) );
   }

   @Test
   public void testAllowedValuesEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:in ( "foo" "bar" ) ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "baz" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( ValueFromListViolation.class );
      final ValueFromListViolation violation = (ValueFromListViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed() ).containsExactly( ResourceFactory.createStringLiteral( "foo" ),
            ResourceFactory.createStringLiteral( "bar" ) );
      assertThat( violation.actual() ).isEqualTo( ResourceFactory.createStringLiteral( "baz" ) );
      assertThat( violation.message() ).isEqualTo(
            "Property :testProperty on :Foo has value baz which is not in the list of allowed values: [foo, bar]." );
      assertThat( violation.errorCode() ).isEqualTo( ValueFromListViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty \"baz\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "\"baz\"".length() ) );
   }

   @Test
   public void testNodeConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :testProperty ;
                 sh:node :AnotherNodeShape ;
               ] .

            :AnotherNodeShape
              a sh:NodeShape ;
              sh:property [
                sh:path :nestedProperty ;
                sh:hasValue "foo" ;
              ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty :Bar .

            :Bar :nestedProperty "bar" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( InvalidValueViolation.class );
      final InvalidValueViolation violation = (InvalidValueViolation) finding;
      final Resource nestedElement = dataModel.createResource( namespace + "Bar" );
      assertThat( violation.context().element() ).isEqualTo( nestedElement );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "AnotherNodeShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":nestedProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Bar" );
      assertThat( violation.allowed().asLiteral().getString() ).isEqualTo( "foo" );
      assertThat( violation.actual() ).isEqualTo( ResourceFactory.createStringLiteral( "bar" ) );
      assertThat( violation.message() ).isEqualTo( "Property :nestedProperty on :Bar has value bar, but only foo is allowed." );
      assertThat( violation.errorCode() ).isEqualTo( InvalidValueViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 5" );
      assertThat( formattedMessage ).contains( ":nestedProperty \"bar\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "\"bar\"".length() ) );
   }

   @Test
   public void testMultipleNodeConstraints() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :SomeNodeShape
              a sh:NodeShape ;
              sh:property [
                sh:path :testProperty ;
                sh:minInclusive 5 ;
              ] .

            :AnotherNodeShape
              a sh:NodeShape ;
              sh:property [
                sh:path :testProperty ;
                sh:minInclusive 10 ;
              ] .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :myProperty ;
                 sh:node :SomeNodeShape ;
                 sh:node :AnotherNodeShape ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :myProperty :element .

            :element :testProperty 1 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource foo = dataModel.createResource( namespace + "Foo" );
      final Resource element = dataModel.createResource( namespace + "element" );
      final List<Violation> violations = validator.validateElement( foo );

      assertThat( violations.size() ).isEqualTo( 2 );
      violations.forEach( finding -> {
         assertThat( finding ).isInstanceOf( MinInclusiveViolation.class );
         final MinInclusiveViolation violation = (MinInclusiveViolation) finding;
         assertThat( violation.context().element() ).isEqualTo( element );
         assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
         assertThat( violation.context().elementName() ).isEqualTo( ":element" );
         assertThat( violation.actual().getLexicalForm() ).isEqualTo(
               ResourceFactory.createTypedLiteral( new BigInteger( "1" ) ).getLexicalForm() );
      } );
   }

   @Test
   public void testClosedConstraint() {
      final Model shapesModel = createModel( """
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :testProperty ;
               ] ;
               sh:closed true ;
               sh:ignoredProperties ( rdf:type ) .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "bar" ;
              :aDifferentProperty "foo".
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( ClosedViolation.class );
      final ClosedViolation violation = (ClosedViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedProperties() ).hasSize( 1 );
      assertThat( violation.allowedProperties().iterator().next().getURI() ).isEqualTo( namespace + "testProperty" );
      assertThat( violation.actual().getURI() ).isEqualTo( namespace + "aDifferentProperty" );
      assertThat( violation.message() ).isEqualTo(
            ":aDifferentProperty is used on :Foo. It is not allowed there; only :testProperty is allowed." );
      assertThat( violation.errorCode() ).isEqualTo( ClosedViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":aDifferentProperty \"foo\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":aDifferentProperty".length() ) );
   }

   @Test
   public void testSparqlConstraintEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :prefixDeclarations
               sh:declare [
                  sh:prefix "" ;
                  sh:namespace "http://example.com#"^^xsd:anyURI ;
               ] .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
               ] ;
               sh:sparql [
                  a sh:SPARQLConstraint ;
                  sh:message "Constraint was violated on {$this}, value was {?value}." ;
                  sh:prefixes :prefixDeclarations ;
                  sh:select ""\"
                     select $this ?value ?code ?highlight
                     where {
                       $this a :TestClass .
                       $this :testProperty ?value .
                       filter( ?value != "secret valid value" )
                       bind( "ERR_CUSTOM" as ?code )
                       bind( ?value as ?highlight )
                     }
                  ""\"
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty "foo" .

            :Bar a :TestClass ;
              :testProperty "secret valid value" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( SparqlConstraintViolation.class );
      final SparqlConstraintViolation violation = (SparqlConstraintViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.message() ).isEqualTo( "Constraint was violated on :Foo, value was foo." );
      assertThat( violation.errorCode() ).isEqualTo( "ERR_CUSTOM" );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty \"foo\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "\"foo\"".length() ) );
   }

   @Test
   public void testBooleanJsConstraintEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyJavaScriptLibrary
               a sh:JSLibrary ;
               sh:jsLibraryURL "$RESOURCE_URL"^^xsd:anyURI .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
                  sh:js [
                     a sh:JSConstraint ;
                     sh:message "JavaScript constraint validation failed." ;
                     sh:jsLibrary :MyJavaScriptLibrary ;
                     sh:jsFunctionName "isRegularExpression" ;
                  ] ;
               ] .
            """.replace( "$RESOURCE_URL", getClass().getClassLoader()
            .getResource( "JsConstraintTest.js" ).toString() ) );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty "(((" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( JsConstraintViolation.class );
      final JsConstraintViolation violation = (JsConstraintViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.message() ).isEqualTo( "JavaScript constraint validation failed." );
      assertThat( violation.errorCode() ).isEqualTo( "ERR_JAVASCRIPT" );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty \"(((\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testMessageObjectJsConstraintEvaluation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyJavaScriptLibrary
               a sh:JSLibrary ;
               sh:jsLibraryURL "$RESOURCE_URL"^^xsd:anyURI .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
                  sh:js [
                     a sh:JSConstraint ;
                     sh:message "JavaScript constraint validation failed" ;
                     sh:jsLibrary :MyJavaScriptLibrary ;
                     sh:jsFunctionName "testTermFactoryAndMessageResult" ;
                  ] ;
               ] .
            """.replace( "$RESOURCE_URL", getClass().getClassLoader()
            .getResource( "JsConstraintTest.js" ).toString() ) );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty "some value" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( JsConstraintViolation.class );
      final JsConstraintViolation violation = (JsConstraintViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      // Note that the message given in the shape is overridden in the JavaScript function
      assertThat( violation.message() ).isEqualTo( "Invalid value: some value on :testProperty." );
      assertThat( violation.errorCode() ).isEqualTo( "ERR_JAVASCRIPT" );
      assertThat( violation.bindings().get( "value" ) ).isEqualTo( "some value" );
      final Property testProperty = dataModel.createProperty( "http://example.com#testProperty" );
      assertThat( (Node_URI) violation.bindings().get( "property" ) ).isEqualTo( testProperty.asNode() );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty \"some value\" ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testSequencePath() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path ( :prop1 :prop2 ) ;
                 sh:hasValue 42  ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :prop1 [
                :prop2 23 ;
              ] .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( InvalidValueViolation.class );
      final InvalidValueViolation violation = (InvalidValueViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":prop2" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getInt() ).isEqualTo( 42 );
      assertThat( violation.actual().asLiteral().getInt() ).isEqualTo( 23 );
      assertThat( violation.message() ).isEqualTo( "Property :prop2 on :Foo has value 23, but only 42 is allowed." );
      assertThat( violation.errorCode() ).isEqualTo( InvalidValueViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":prop2 23" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "23".length() ) );
   }

   @Test
   public void testAlternativePath() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path [ sh:alternativePath ( :prop1 :prop2 ) ] ;
                 sh:hasValue 42  ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :prop1 42 ;
              :prop2 23 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( InvalidValueViolation.class );
      final InvalidValueViolation violation = (InvalidValueViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":prop2" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getInt() ).isEqualTo( 42 );
      assertThat( violation.actual().asLiteral().getInt() ).isEqualTo( 23 );
      assertThat( violation.message() ).isEqualTo( "Property :prop2 on :Foo has value 23, but only 42 is allowed." );
      assertThat( violation.errorCode() ).isEqualTo( InvalidValueViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":prop2 23 ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "23".length() ) );
   }

   @Test
   public void testInversePath() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path ( :testProperty [ sh:inversePath :testProperty ] :testProperty2 ) ;
                 sh:hasValue 42  ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty :Bar ;
              :testProperty2 23 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( InvalidValueViolation.class );
      final InvalidValueViolation violation = (InvalidValueViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty2" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getInt() ).isEqualTo( 42 );
      assertThat( violation.actual().asLiteral().getInt() ).isEqualTo( 23 );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty2 on :Foo has value 23, but only 42 is allowed." );
      assertThat( violation.errorCode() ).isEqualTo( InvalidValueViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty2 23 ." );
      assertThat( formattedMessage ).contains( " " + "^".repeat( "23".length() ) );
   }

   @Test
   public void testZeroOrMorePath() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path ( :testProperty [ sh:zeroOrMorePath :testProperty2 ] ) ;
                 sh:nodeKind sh:BlankNode  ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty [
                 :testProperty2 [
                   :testProperty2 23 ;
                 ]
               ] .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( NodeKindViolation.class );
      final NodeKindViolation violation = (NodeKindViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty2" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty2 on :Foo is a value, but it must be an anonymous node." );
      assertThat( violation.errorCode() ).isEqualTo( NodeKindViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 5" );
      assertThat( formattedMessage ).contains( ":testProperty2 23" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty2".length() ) );
   }

   @Test
   public void testOneOrMorePath() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path ( :testProperty [ sh:oneOrMorePath :testProperty2 ] ) ;
                 sh:nodeKind sh:BlankNode  ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty [
                 :testProperty2 [
                   :testProperty2 23 ;
                 ]
               ] .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( NodeKindViolation.class );
      final NodeKindViolation violation = (NodeKindViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty2" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty2 on :Foo is a value, but it must be an anonymous node." );
      assertThat( violation.errorCode() ).isEqualTo( NodeKindViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 5" );
      assertThat( formattedMessage ).contains( ":testProperty2 23" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty2".length() ) );
   }

   @Test
   public void testZeroOrOnePath() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path ( :testProperty [ sh:zeroOrOnePath :testProperty2 ] ) ;
                 sh:nodeKind sh:BlankNode  ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty [
                 :testProperty2 23 ;
               ] .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( NodeKindViolation.class );
      final NodeKindViolation violation = (NodeKindViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty2" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty2 on :Foo is a value, but it must be an anonymous node." );
      assertThat( violation.errorCode() ).isEqualTo( NodeKindViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 4" );
      assertThat( formattedMessage ).contains( ":testProperty2 23" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty2".length() ) );
   }

   @Test
   public void testNotConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :testProperty ;
                 sh:not [
                   sh:nodeKind sh:BlankNode ;
                 ] ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty [
                 :testProperty2 23 ;
               ] .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( NotViolation.class );
      final NotViolation violation = (NotViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      final NodeKindConstraint nestedConstraint = (NodeKindConstraint) violation.negatedConstraint();
      assertThat( nestedConstraint.allowedNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.message() ).isEqualTo(
            "Expected violation of constraint sh:nodeKind on :testProperty on :Foo, but it did not occur." );
      assertThat( violation.errorCode() ).isEqualTo( NotViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty [" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testAndConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :testProperty ;
                 sh:and (
                   [ sh:nodeKind sh:BlankNode ]
                   [ sh:nodeKind sh:IRI ]
                 ) ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty [
                 :testProperty2 42 ;
              ] .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( NodeKindViolation.class );
      final NodeKindViolation violation = (NodeKindViolation) finding;
      assertThat( violation.context().parentContext().map( EvaluationContext::element ) ).contains( element );
      assertThat( violation.context().parentContext().get().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( ( (PredicatePath) violation.context().parentContext().get().propertyShape().get().path() ).predicate().getURI() )
            .endsWith( "testProperty" );
      assertThat( violation.context().parentElementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.IRI );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.message() ).isEqualTo( "Property :testProperty on :Foo is an anonymous node, but it must be a named element." );
      assertThat( violation.errorCode() ).isEqualTo( NodeKindViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty [" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   public void testOrConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :testProperty ;
                 sh:or (
                   [ sh:hasValue 17 ]
                   [ sh:hasValue 23 ]
                 ) ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations )
            .singleElement()
            .satisfies( violation -> {
               assertThat( violation.context().element() ).isEqualTo( element );
               assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
               assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
               assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
               assertThat( violation ).isInstanceOf( OrViolation.class );
               assertThat( ( (OrViolation) violation ).violations() )
                     .hasSize( 2 )
                     .anySatisfy( subviolation ->
                           assertThat( subviolation ).isInstanceOfSatisfying( InvalidValueViolation.class, invalidValueViolation -> {
                              assertThat( invalidValueViolation.allowed() ).isInstanceOfSatisfying( Literal.class, allowedLiteral ->
                                    assertThat( allowedLiteral.getInt() ).isEqualTo( 17 ) );
                              assertThat( invalidValueViolation.actual() ).isInstanceOfSatisfying( Literal.class, actualLiteral ->
                                    assertThat( actualLiteral.getInt() ).isEqualTo( 42 ) );
                           } ) )
                     .anySatisfy( subviolation ->
                           assertThat( subviolation ).isInstanceOfSatisfying( InvalidValueViolation.class, invalidValueViolation -> {
                              assertThat( invalidValueViolation.allowed() ).isInstanceOfSatisfying( Literal.class, allowedLiteral ->
                                    assertThat( allowedLiteral.getInt() ).isEqualTo( 23 ) );
                              assertThat( invalidValueViolation.actual() ).isInstanceOfSatisfying( Literal.class, actualLiteral ->
                                    assertThat( actualLiteral.getInt() ).isEqualTo( 42 ) );
                           } ) );
            } );
   }

   @Test
   public void testXoneConstraintInPropertyShape() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :testProperty ;
                 sh:xone (
                   [ sh:nodeKind sh:IRI ]
                 ) ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( XoneViolation.class );
      final XoneViolation violation = (XoneViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.violations() ).hasSize( 1 );
      assertThat( violation.violations().get( 0 ) ).isInstanceOf( NodeKindViolation.class );
      final NodeKindViolation nestedViolation = (NodeKindViolation) violation.violations().get( 0 );
      assertThat( nestedViolation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.IRI );
      assertThat( nestedViolation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( nestedViolation.message() ).isEqualTo( "Property :testProperty on :Foo is a value, but it must be a named element." );
      assertThat( nestedViolation.errorCode() ).isEqualTo( NodeKindViolation.ERROR_CODE );

      final String formattedMessage = rustLikeFormatter.visit( finding );
      assertThat( formattedMessage ).contains( "Error at line 3" );
      assertThat( formattedMessage ).contains( ":testProperty 42" );
      assertThat( formattedMessage ).contains( " " + "^".repeat( ":testProperty".length() ) );
   }

   @Test
   void testXoneConstraintInPropertyShapeWithNoSubViolations() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :testProperty ;
                 sh:xone (
                   :NodeKindAssertion
                   :ValueAssertion
                 ) ;
               ] .

            :NodeKindAssertion
              a sh:NodeShape ;
              sh:nodeKind sh:Literal .

            :ValueAssertion
              a sh:NodeShape ;
              sh:hasValue 42 .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( XoneViolation.class );
      final XoneViolation violation = (XoneViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.context().propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.context().elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.violations() ).isEmpty();
      assertThat( violation.message() ).contains(
            "Exactly one of the following conditions should lead to a violation, but all of them passed successfully" );
   }

   @Test
   void testXoneConstraintInNodeShapeExpectSuccess() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:xone (
                 :Property1Shape
                 :Property2Shape
               ) .

            :Property1Shape
              a sh:PropertyShape ;
              sh:path :foo ;
              sh:minCount 1 .

            :Property2Shape
              a sh:PropertyShape ;
              sh:path :bar ;
              sh:minCount 1 .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :foo 1 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations ).isEmpty();
   }

   @Test
   void testXoneConstraintInNodeShapeExpectFailure() {
      final Model shapesModel = createModel(
            """
                  @prefix sh: <http://www.w3.org/ns/shacl#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <http://example.com#> .

                  :MyShape
                     a sh:NodeShape ;
                     sh:targetClass :TestClass ;
                     sh:name "Test shape" ;
                     sh:description "Test shape description" ;
                     sh:xone (
                       :Property1Shape
                       :Property2Shape
                     ) .

                  :Property1Shape
                    a sh:PropertyShape ;
                    sh:path :foo ;
                    sh:minCount 1 .

                  :Property2Shape
                    a sh:PropertyShape ;
                    sh:path ( :bar :baz ) ;
                    sh:minCount 1 .
                  """
      );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 42 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( XoneViolation.class );
      assertThat( finding.message() ).startsWith( "Exactly one of the following violations must be fixed:" );
      assertThat( finding.message() ).contains( "Mandatory property :foo is missing on :Foo" );
      assertThat( finding.message() ).contains( "Mandatory property :bar/:baz is missing on :Foo" );
      assertThat( finding ).isInstanceOfSatisfying( XoneViolation.class, xoneViolation ->
            assertThat( xoneViolation.violations() ).satisfiesExactly(
                  violation ->
                        assertThat( violation ).isInstanceOfSatisfying( MinCountViolation.class, minCountViolation -> {
                           assertThat( minCountViolation.allowed() ).isEqualTo( 1 );
                           assertThat( minCountViolation.actual() ).isEqualTo( 0 );
                           assertThat( minCountViolation.context().propertyShape() ).hasValueSatisfying( property ->
                                 assertThat( property.path() ).isInstanceOfSatisfying( PredicatePath.class, predicatePath ->
                                       assertThat( predicatePath.toString() ).isEqualTo( ":foo" ) ) );
                        } ),
                  violation ->
                        assertThat( violation ).isInstanceOfSatisfying( MinCountViolation.class, minCountViolation -> {
                           assertThat( minCountViolation.allowed() ).isEqualTo( 1 );
                           assertThat( minCountViolation.actual() ).isEqualTo( 0 );
                           assertThat( minCountViolation.context().propertyShape() ).hasValueSatisfying( property ->
                                 assertThat( property.path() ).isInstanceOfSatisfying( SequencePath.class, sequencePath ->
                                       assertThat( sequencePath.toString() ).isEqualTo( ":bar/:baz" ) ) );
                        } )
            ) );
   }

   @Test
   void testSparqlTargetWithGenericConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :prefixDeclarations
               sh:declare [
                  sh:prefix "" ;
                  sh:namespace "http://example.com#"^^xsd:anyURI ;
               ] .

            :MyShape
               a sh:NodeShape ;
               sh:target [
                  a sh:SPARQLTarget ;
                  sh:prefixes :prefixDeclarations ;
                  sh:select ""\"
                     select $this
                     where {
                        $this a :TestClass .
                     }
                  ""\"
               ] ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                 sh:path :testProperty ;
                 sh:maxLength 2 ;
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "abc" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MaxLengthViolation.class );
   }

   @Test
   void testSparqlTargetWithShapeSparqlConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :prefixDeclarations
               sh:declare [
                  sh:prefix "" ;
                  sh:namespace "http://example.com#"^^xsd:anyURI ;
               ] .

            :MyShape
               a sh:NodeShape ;
               sh:target [
                  a sh:SPARQLTarget ;
                  sh:prefixes :prefixDeclarations ;
                  sh:select ""\"
                     select $this
                     where {
                        $this a :TestClass .
                     }
                  ""\"
               ] ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
               ] ;
               sh:sparql [
                  a sh:SPARQLConstraint ;
                  sh:message "Constraint was violated on {$this}, value was {?value}." ;
                  sh:prefixes :prefixDeclarations ;
                  sh:select ""\"
                     select $this ?value ?code
                     where {
                       $this a :TestClass .
                       $this :testProperty ?value .
                       filter( ?value != "secret valid value" )
                       bind( "ERR_CUSTOM" as ?code )
                     }
                  ""\"
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty "foo" .

            :Bar a :TestClass ;
              :testProperty "secret valid value" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( SparqlConstraintViolation.class );
   }

   @Test
   void testSparqlTargetWithPropertySparqlConstraint() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :prefixDeclarations
               sh:declare [
                  sh:prefix "" ;
                  sh:namespace "http://example.com#"^^xsd:anyURI ;
               ] .

            :MyShape
               a sh:NodeShape ;
               sh:target [
                  a sh:SPARQLTarget ;
                  sh:prefixes :prefixDeclarations ;
                  sh:select ""\"
                     select $this
                     where {
                        $this a :TestClass .
                     }
                  ""\"
               ] ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
                  sh:sparql [
                     a sh:SPARQLConstraint ;
                     sh:message "Required property 'testProperty' does not exist on {$this}." ;
                     sh:prefixes :prefixDeclarations ;
                     sh:select ""\"
                        select $this ?code
                        where {
                          $this a :TestClass .
                          filter ( not exists { $this :testProperty [] } ) .
                          bind( "ERR_CUSTOM" as ?code )
                        }
                     ""\"
                  ] ;
               ] .
            """ );

      // important detail: ':testProperty' is missing on ':Foo', the SPARQLConstraint must run anyway
      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass.

            :Bar a :TestClass ;
              :testProperty "secret valid value" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( SparqlConstraintViolation.class );
   }

   @Test
   void testMultiElementValidation() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix : <http://example.com#> .

            :prefixDeclarations
               sh:declare [
                  sh:prefix "" ;
                  sh:namespace "http://example.com#"^^xsd:anyURI ;
               ] .

            :MyShape
               a sh:NodeShape ;
               sh:target [
                  a sh:SPARQLTarget ;
                  sh:prefixes :prefixDeclarations ;
                  sh:select ""\"
                     select $this
                     where {
                        $this a :TestClass .
                     }
                  ""\"
               ] ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
               ] ;
               sh:sparql [
                  a sh:SPARQLConstraint ;
                  sh:message "Constraint was violated on {$this}, value was {?value}." ;
                  sh:prefixes :prefixDeclarations ;
                  sh:select ""\"
                     select $this ?value ?code
                     where {
                       $this a :TestClass .
                       $this :testProperty ?value .
                       filter( ?value != "secret valid value" )
                       bind( "ERR_CUSTOM" as ?code )
                     }
                  ""\"
               ] .
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty "foo" .

            :Bar a :TestClass ;
              :testProperty "bar" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource foo = dataModel.createResource( namespace + "Foo" );
      final Resource bar = dataModel.createResource( namespace + "Bar" );
      final List<Violation> violations = validator.validateElements( List.of( foo, bar ) );

      assertThat( violations.size() ).isEqualTo( 2 );
      assertThat( violations.get( 0 ) ).isInstanceOf( SparqlConstraintViolation.class );
      assertThat( violations.get( 1 ) ).isInstanceOf( SparqlConstraintViolation.class );
   }

   @Test
   void testTargetObjectsOf() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetObjectsOf rdf:type ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
               ] ;
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty 2 .

            :Bar a :TestClass ;
              :testProperty 3 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource foo = dataModel.createResource( namespace + "Foo" );
      final Resource bar = dataModel.createResource( namespace + "Bar" );
      final List<Violation> violations = validator.validateElements( List.of( foo, bar ) );

      assertThat( violations.size() ).isEqualTo( 2 );
      assertThat( violations.get( 0 ) ).isInstanceOf( DatatypeViolation.class );
      assertThat( violations.get( 1 ) ).isInstanceOf( DatatypeViolation.class );
   }

   @Test
   void testNodeTargets() {
      final Model shapesModel = createModel( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix : <http://example.com#> .

            :MyShape
               a sh:NodeShape ;
               sh:targetNode :Foo ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:property [
                  sh:path :testProperty ;
                  sh:datatype xsd:string ;
               ] ;
            """ );

      final Model dataModel = createModel( """
            @prefix : <http://example.com#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            :Foo a :TestClass ;
              :testProperty 2 .

            :Bar a :TestClass ;
              :testProperty 3 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource foo = dataModel.createResource( namespace + "Foo" );
      final Resource bar = dataModel.createResource( namespace + "Bar" );
      final List<Violation> violations = validator.validateElements( List.of( foo, bar ) );

      assertThat( violations.size() ).isEqualTo( 1 );
      assertThat( violations.get( 0 ) ).isInstanceOf( DatatypeViolation.class );
   }
}
