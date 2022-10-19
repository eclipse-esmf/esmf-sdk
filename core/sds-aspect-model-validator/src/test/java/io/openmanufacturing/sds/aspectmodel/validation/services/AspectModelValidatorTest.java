/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.validation.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.topbraid.shacl.vocabulary.SH;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.shacl.ShaclValidator;
import io.openmanufacturing.sds.aspectmodel.shacl.Shape;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.DatatypeConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MinCountConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.NodeKindConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.path.PredicatePath;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ClassTypeViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ClosedViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.DatatypeViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.DisjointViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EqualsViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.InvalidValueViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.LanguageFromListViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.LessThanOrEqualsViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.LessThanViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxCountViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxExclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxInclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxLengthViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinCountViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinExclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinInclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinLengthViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.NodeKindViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.NotViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.PatternViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.SparqlConstraintViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.UniqueLanguageViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ValueFromListViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationError;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.test.InvalidTestAspect;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;
import io.openmanufacturing.sds.test.TestResources;
import io.vavr.control.Try;

public class AspectModelValidatorTest extends MetaModelVersions {
   private static final String MISSING_PROPERTY_MESSAGE = "Property needs to have at least 1 values, but found 0";
   private final String namespace = "http://example.com#";
   private final AspectModelValidator service = new AspectModelValidator();

   private String getMetaModelUrn( final KnownVersion version ) {
      return "urn:bamm:io.openmanufacturing:meta-model:" + version.toVersionString() + "#";
   }

   private String getNameUrn( final KnownVersion version ) {
      return getMetaModelUrn( version ) + "name";
   }

   private String getPropertiesUrn( final KnownVersion version ) {
      return getMetaModelUrn( version ) + "properties";
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidAspect( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> validAspectModel = TestResources.getModel( TestAspect.ASPECT, metaModelVersion );
      final ValidationReport result = service.validate( validAspectModel );
      assertThat( result.conforms() ).isTrue();
      assertThat( result.getValidationErrors() ).hasSize( 0 );
   }

   /**
    * Test for a model that passes validation via BAMM's SHACL shapes but is actually invalid and can not be loaded using
    * {@link io.openmanufacturing.sds.metamodel.loader.AspectModelLoader#fromVersionedModel(VersionedModel)}.
    * This method and the corresponding test model should be removed once
    * <a href="https://github.com/OpenManufacturingPlatform/sds-bamm-aspect-meta-model/issues/173">BAMM-173</a> has been addressed
    * @param metaModelVersion the meta model version
    */
   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testFalsePositiveValidation( final KnownVersion metaModelVersion ) {
      final TestModel testModel = InvalidTestAspect.ASPECT_WITH_FALSE_POSITIVE_VALIDATION;
      final Try<VersionedModel> aspectModel = TestResources.getModel( testModel, metaModelVersion );
      final ValidationReport result = service.validate( aspectModel );

      System.out.println( result.getValidationErrors().iterator().next() );
      assertThat( result.conforms() ).isFalse();
      assertThat( result.getValidationErrors().iterator().next() ).isOfAnyClassIn( ValidationError.Processing.class );
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         "ASPECT_WITH_CONSTRAINTS",
         // uses bamm-c:OPEN which is not specified, see https://github.com/OpenManufacturingPlatform/sds-bamm-aspect-meta-model/issues/174
         "ASPECT_WITH_FIXED_POINT",
         "ASPECT_WITH_FIXED_POINT_CONSTRAINT",
         "ASPECT_WITH_LANGUAGE_CONSTRAINT",
         "ASPECT_WITH_RANGE_CONSTRAINT_ON_CONSTRAINED_NUMERIC_TYPE", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_DOUBLE_VALUE", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITHOUT_MIN_MAX_INTEGER_VALUE", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_LOWER_BOUND_INCL_BOUND_DEFINITION", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_MIN_VALUE", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND", // uses bamm-c:OPEN
         "ASPECT_WITH_RANGE_CONSTRAINT_WITH_ONLY_UPPER_BOUND_INCL_BOUND_DEFINITION" // uses bamm-c:OPEN
   } )
   public void testValidateTestAspectModel( final TestAspect testAspect ) {
      final Try<VersionedModel> tryModel = TestResources.getModel( testAspect, KnownVersion.getLatest() );
      final VersionedModel versionedModel = tryModel.get();

      final ValidationReport report = service.validate( tryModel );
      if ( !report.conforms() ) {
         report.getValidationErrors().forEach( System.out::println );
      }
      assertThat( report.conforms() ).isTrue();

      final List<String> elements = versionedModel.getRawModel().listStatements( null, RDF.type, (RDFNode) null )
            .mapWith( Statement::getSubject )
            .filterKeep( Resource::isURIResource )
            .mapWith( Resource::getURI )
            .toList();

      for ( final String elementUri : elements ) {
         final Resource element = versionedModel.getModel().createResource( elementUri );
         final List<Violation> violations = service.validateElement( element );
         assertThat( violations ).isEmpty();
      }
   }

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding1_0_0" )
   public void testInvalidAspectMissingProperties( final KnownVersion metaModelVersion ) {
      final TestModel testModel = InvalidTestAspect.ASPECT_MISSING_PROPERTIES;
      final Try<VersionedModel> validAspectModel = TestResources.getModel( testModel, metaModelVersion );
      final ValidationReport report = service.validate( validAspectModel );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();

      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            assertThat( error.getFocusNode() ).isEqualTo( testModel.getUrn().toString() );
            assertThat( error.getResultPath() ).isEqualTo( getPropertiesUrn( metaModelVersion ) );
            assertThat( error.getResultMessage() ).isEqualTo( MISSING_PROPERTY_MESSAGE );
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            fail();
            return null;
         }
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsUpToIncluding1_0_0" )
   public void testInvalidAspectMissingNameAndProperties( final KnownVersion metaModelVersion ) {
      final TestModel testModel = InvalidTestAspect.ASPECT_MISSING_NAME_AND_PROPERTIES;
      final Try<VersionedModel> validAspectModel = TestResources.getModel( testModel, metaModelVersion );
      final ValidationReport report = service.validate( validAspectModel );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 2 );

      final ValidationError missingPropertiesFailure = new ValidationError.Semantic(
            MISSING_PROPERTY_MESSAGE, testModel.getUrn().toString(), getPropertiesUrn( metaModelVersion ),
            SH.Violation.toString(), "" );
      assertThat( errors.contains( missingPropertiesFailure ) ).isTrue();

      final ValidationError.Semantic missingNameFailure = new ValidationError.Semantic(
            MISSING_PROPERTY_MESSAGE, testModel.getUrn().toString(), getNameUrn( metaModelVersion ),
            SH.Violation.toString(), "" );
      assertThat( errors.contains( missingNameFailure ) ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testInvalidTurtleSyntax( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources
            .getModel( InvalidTestAspect.INVALID_SYNTAX, metaModelVersion );
      final ValidationReport report = service.validate( invalidTurtleSyntax );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();
      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            assertThat( error.getLineNumber() ).isEqualTo( 17 );
            assertThat( error.getColumnNumber() ).isEqualTo( 2 );
            assertThat( error.getOriginalExceptionMessage() )
                  .isEqualTo( "[line: 17, col: 2 ] Triples not terminated by DOT" );
            assertThat( error.toString() )
                  .isEqualTo( "The Aspect Model contains invalid syntax at line number 17 and column number 2." );
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            fail();
            return null;
         }
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testNonTurtleFile( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources
            .getModel( InvalidTestAspect.ACTUALLY_JSON, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isEqualTo( true );

      final ValidationReport report = service.validate( invalidTurtleSyntax );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();
      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            assertThat( error.getLineNumber() ).isEqualTo( 12 );
            assertThat( error.getColumnNumber() ).isEqualTo( 1 );
            assertThat( error.getOriginalExceptionMessage() )
                  .isEqualTo( "[line: 12, col: 1 ] Not implemented (formulae, graph literals)" );
            assertThat( error.toString() )
                  .isEqualTo( "The Aspect Model contains invalid syntax at line number 12 and column number 1." );
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            fail();
            return null;
         }
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithInvalidMetaModelVersion( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> invalidTurtleSyntax = TestResources
            .getModel( InvalidTestAspect.ASPECT_WITH_INVALID_VERSION, metaModelVersion );
      assertThat( invalidTurtleSyntax.isFailure() ).isEqualTo( true );

      final ValidationReport report = service.validate( invalidTurtleSyntax );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();
      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            assertThat( error.getMessage() ).contains( ValidationError.MESSAGE_MODEL_RESOLUTION_ERROR );
            assertThat( error.getMessage() ).startsWith( "Model could not be resolved entirely" );
            return null;
         }
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMissingAspectDeclaration( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> missingAspect = TestResources
            .getModel( InvalidTestAspect.MISSING_ASPECT_DECLARATION, metaModelVersion );
      assertThat( missingAspect.isFailure() ).isEqualTo( true );

      final ValidationReport report = service.validate( missingAspect );
      assertThat( report.conforms() ).isFalse();

      final Collection<? extends ValidationError> errors = report.getValidationErrors();
      assertThat( errors ).hasSize( 1 );

      final ValidationError validationError = errors.iterator().next();
      validationError.accept( new ValidationError.Visitor<Void>() {
         @Override
         public Void visit( final ValidationError.Semantic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Syntactic error ) {
            fail();
            return null;
         }

         @Override
         public Void visit( final ValidationError.Processing error ) {
            assertThat( error.getMessage() ).contains( ValidationError.MESSAGE_MODEL_RESOLUTION_ERROR );
            return null;
         }
      } );
   }

   @Test
   public void testLoadingCustomShape() {
      final Model shapesModel = model( """
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
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actualClass().getURI() ).isEqualTo( "http://example.com#SomethingElse" );
      assertThat( violation.allowedClass().getURI() ).isEqualTo( "http://example.com#TestClass2" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testDatatypeConstraintEvaluation() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actualTypeUri() ).isEqualTo( XSD.integer.getURI() );
      assertThat( violation.allowedTypeUri() ).isEqualTo( XSD.xstring.getURI() );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testNodeKindConstraintEvaluation() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.IRI );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testMinCountConstraintEvaluation() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( MinCountViolation.class );
      final MinCountViolation violation = (MinCountViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testMaxCountEvaluation() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testMinExclusiveConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual().getInt() ).isEqualTo( 42 );
      assertThat( violation.min().getInt() ).isEqualTo( 42 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testMinInclusiveConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual().getInt() ).isEqualTo( 41 );
      assertThat( violation.min().getInt() ).isEqualTo( 42 );
      assertThat( violation.message() ).isNotEmpty();

   }

   @Test
   public void testMaxExclusiveConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual().getInt() ).isEqualTo( 42 );
      assertThat( violation.max().getInt() ).isEqualTo( 42 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testMaxInclusiveConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual().getInt() ).isEqualTo( 43 );
      assertThat( violation.max().getInt() ).isEqualTo( 42 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testMinLengthConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual() ).isEqualTo( 3 );
      assertThat( violation.min() ).isEqualTo( 5 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testMaxLengthConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual() ).isEqualTo( 6 );
      assertThat( violation.max() ).isEqualTo( 5 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testPatternConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual() ).isEqualTo( "y" );
      assertThat( violation.pattern() ).isEqualTo( "^x" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testAllowedLanguageConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actual() ).isEqualTo( "fr" );
      assertThat( violation.allowed() ).containsExactlyInAnyOrder( "en", "de" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testEqualsConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedValue().asLiteral().getString() ).isEqualTo( "a different value" );
      assertThat( violation.actualValue().asLiteral().getString() ).isEqualTo( "some value" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testDisjointConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.otherValue().asLiteral().getString() ).isEqualTo( "some value" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testLessThanConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actualValue().getInt() ).isEqualTo( 10 );
      assertThat( violation.otherValue().getInt() ).isEqualTo( 5 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testLessThanOrEqualsConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.actualValue().getInt() ).isEqualTo( 10 );
      assertThat( violation.otherValue().getInt() ).isEqualTo( 5 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testUniqueLangConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.duplicates() ).containsExactly( "en" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testHasValueConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getInt() ).isEqualTo( 42 );
      assertThat( violation.actual().asLiteral().getString() ).isEqualTo( "hello" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testAllowedValuesEvaluation() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed() ).containsExactly( ResourceFactory.createStringLiteral( "foo" ), ResourceFactory.createStringLiteral( "bar" ) );
      assertThat( violation.actual() ).isEqualTo( ResourceFactory.createStringLiteral( "baz" ) );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testNodeConstraint() {
      final Model shapesModel = model( """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix : <http://example.com#> .

            :AnotherNodeShape
              a sh:NodeShape ;
              sh:property [
                sh:path :testProperty ;
                sh:hasValue "foo" ;
              ] .

            :MyShape
               a sh:NodeShape ;
               sh:targetClass :TestClass ;
               sh:name "Test shape" ;
               sh:description "Test shape description" ;
               sh:node :AnotherNodeShape .
            """ );

      final Model dataModel = model( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty "bar" .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 1 );
      final Violation finding = violations.get( 0 );
      assertThat( finding ).isInstanceOf( InvalidValueViolation.class );
      final InvalidValueViolation violation = (InvalidValueViolation) finding;
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "AnotherNodeShape" );
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getString() ).isEqualTo( "foo" );
      assertThat( violation.actual() ).isEqualTo( ResourceFactory.createStringLiteral( "bar" ) );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testMultipleNodeConstraints() {
      final Model shapesModel = model( """
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
               sh:node :SomeNodeShape ;
               sh:node :AnotherNodeShape .
            """ );

      final Model dataModel = model( """
            @prefix : <http://example.com#> .
            :Foo a :TestClass ;
              :testProperty 1 .
            """ );

      final ShaclValidator validator = new ShaclValidator( shapesModel );
      final Resource element = dataModel.createResource( namespace + "Foo" );
      final List<Violation> violations = validator.validateElement( element );

      assertThat( violations.size() ).isEqualTo( 2 );
      violations.forEach( finding -> {
         assertThat( finding ).isInstanceOf( MinInclusiveViolation.class );
         final MinInclusiveViolation violation = (MinInclusiveViolation) finding;
         assertThat( violation.context().element() ).isEqualTo( element );
         assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
         assertThat( violation.elementName() ).isEqualTo( ":Foo" );
         assertThat( violation.actual().getLexicalForm() ).isEqualTo( ResourceFactory.createTypedLiteral( new BigInteger( "1" ) ).getLexicalForm() );
      } );
   }

   @Test
   public void testClosedConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedProperties() ).hasSize( 1 );
      assertThat( violation.allowedProperties().iterator().next().getURI() ).isEqualTo( namespace + "testProperty" );
      assertThat( violation.actual().getURI() ).isEqualTo( namespace + "aDifferentProperty" );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testSparqlConstraintEvaluation() {
      final Model shapesModel = model( """
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
                  sh:message "Constraint was violated on {$this}, value was {?value}" ;
                  sh:prefixes :prefixDeclarations ;
                  sh:select ""\"
                     select $this ?value
                     where {
                       $this a :TestClass .
                       $this :testProperty ?value .
                       filter( ?value != "secret valid value" )
                     }
                  ""\"
               ] .
            """ );

      final Model dataModel = model( """
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
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.message() ).isNotEmpty();
      assertThat( violation.message() ).isEqualTo( "Constraint was violated on :Foo, value was foo" );
   }

   @Test
   public void testSequencePath() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":prop2" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getInt() ).isEqualTo( 42 );
      assertThat( violation.actual().asLiteral().getInt() ).isEqualTo( 23 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testAlternativePath() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":prop2" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getInt() ).isEqualTo( 42 );
      assertThat( violation.actual().asLiteral().getInt() ).isEqualTo( 23 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testInversePath() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty2" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowed().asLiteral().getInt() ).isEqualTo( 42 );
      assertThat( violation.actual().asLiteral().getInt() ).isEqualTo( 23 );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testZeroOrMorePath() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty2" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testOneOrMorePath() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty2" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testZeroOrOnePath() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty2" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.Literal );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testNotConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      final NodeKindConstraint nestedConstraint = (NodeKindConstraint) violation.negatedConstraint();
      assertThat( nestedConstraint.allowedNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.message() ).isNotEmpty();
   }

   @Test
   public void testAndConstraint() {
      final Model shapesModel = model( """
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

      final Model dataModel = model( """
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
      assertThat( violation.context().element() ).isEqualTo( element );
      assertThat( violation.context().shape().attributes().uri() ).hasValue( namespace + "MyShape" );
      assertThat( violation.propertyName() ).isEqualTo( ":testProperty" );
      assertThat( violation.elementName() ).isEqualTo( ":Foo" );
      assertThat( violation.allowedNodeKind() ).isEqualTo( Shape.NodeKind.IRI );
      assertThat( violation.actualNodeKind() ).isEqualTo( Shape.NodeKind.BlankNode );
      assertThat( violation.message() ).isNotEmpty();
   }

   private Model model( final String ttlRepresentation ) {
      final Model model = ModelFactory.createDefaultModel();
      final InputStream in = new ByteArrayInputStream( ttlRepresentation.getBytes( StandardCharsets.UTF_8 ) );
      model.read( in, "", RDFLanguages.strLangTurtle );
      return model;
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidationWithMultipleAspects( final KnownVersion metaModelVersion ) {
      final Try<VersionedModel> model = TestResources.getModel( TestAspect.ASPECT, metaModelVersion );
      model.forEach( versionedModel -> {
         final VersionedModel model2 = TestResources.getModel( TestAspect.ASPECT_WITH_SIMPLE_TYPES, metaModelVersion ).get();
         versionedModel.getModel().add( model2.getRawModel() );
         versionedModel.getRawModel().add( model2.getRawModel() );
      } );

      final ValidationReport report = service.validate( model );
      if ( !report.conforms() ) {
         report.getValidationErrors().forEach( System.out::println );
      }
      assertThat( report.conforms() ).isTrue();
   }
}
