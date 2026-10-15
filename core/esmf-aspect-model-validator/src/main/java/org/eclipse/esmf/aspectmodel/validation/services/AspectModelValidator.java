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

package org.eclipse.esmf.aspectmodel.validation.services;

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.jena.query.ARQ;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import org.eclipse.esmf.aspectmodel.AspectLoadingException;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.MetaModelVersionException;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.loader.AspectLoadingViolationBuilder;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.service.ExtensionService;
import org.eclipse.esmf.aspectmodel.shacl.ShaclValidator;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolationBuilder;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolationBuilder;
import org.eclipse.esmf.aspectmodel.validation.MetaModelVersionViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolationBuilder;
import org.eclipse.esmf.aspectmodel.validation.RdfBasedValidator;
import org.eclipse.esmf.aspectmodel.validation.Validator;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import io.vavr.control.Either;

/**
 * Uses SHACL to validate an Aspect Model against the defined semantics of the Aspect Meta Model.
 */
public class AspectModelValidator implements Validator {
   private final ShaclValidator shaclValidator;
   private static boolean arqInitialized = false;

   private static synchronized void initArq() {
      if ( !arqInitialized ) {
         ARQ.init();
         arqInitialized = true;
      }
   }

   /**
    * Default constructor that will use the latest meta model version
    */
   public AspectModelValidator() {
      initArq();
      shaclValidator = new ShaclValidator( MetaModelFile.metaModelShapes() );
   }

   /**
    * Validates an Aspect Model provided by a Supplier. This can be used to make the validator also
    * catch and handle loading and resolution errors, such as RDF/Turtle syntax errors or missing
    * references. In those cases, corresponding violations such as {@link InvalidSyntaxViolation} and
    * {@link ProcessingViolation} are created.
    *
    * @param aspectModelSupplier the Aspect Model supplier
    * @return a list of {@link Violation}s. An empty list indicates that the model is valid.
    */
   @Override
   public ViolationReport validateModel( final Supplier<AspectModel> aspectModelSupplier ) {
      final Either<ViolationReport, AspectModel> result = loadModel( aspectModelSupplier );
      if ( result.isLeft() ) {
         return result.getLeft();
      }
      return validateModel( result.get() );
   }

   /**
    * Loads an Aspect Model provided by a Supplier and returns either the sucessfully loaded model or
    * the list of violations representing resolution failures, syntax errors etc.. This method does
    * <i>not</i> perform semantic validation. This method is an alternative to
    * {@link AspectModelLoader#load(File)} and its siblings with the difference that on errors (syntax
    * errors, resolution errors), no exception is thrown and a {@link Violation} is created instead.
    *
    * @param aspectModelLoader the Aspect Model supplier
    * @return An {@link Either.Right} with the model if there are no violations, or an
    *         {@link Either.Left} with the violation report.
    */
   @Override
   public Either<ViolationReport, AspectModel> loadModel( final Supplier<AspectModel> aspectModelLoader ) {
      final AspectModel model;
      try {
         model = aspectModelLoader.get();
         return Either.right( model );
      } catch ( final ParserException exception ) {
         // Regular syntax errors
         return Either.left( reportForParserException( exception ) );
      } catch ( final ValueParsingException exception ) {
         // Failure to parse value literals
         return Either.left( reportForValueParsingException( exception ) );
      } catch ( final MetaModelVersionException exception ) {
         // The file uses meta model terms that do not exist in the version it declares, so it was not
         // migrated and could not be loaded. One violation is reported per offending term.
         return Either.left( new ViolationReport( exception.problems().stream()
               .<Violation>map( problem -> new MetaModelVersionViolation( problem.message(), problem.highlight() ) )
               .toList() ) );
      } catch ( final CancelValidation cancelValidation ) {
         // The validation was short-circuited by the aspectModelLoader function
         return Either.left( cancelValidation.violations );
      } catch ( final AspectLoadingException exception ) {
         return Either.left( reportForAspectLoadingException( exception ) );
      } catch ( final ModelResolutionException exception ) {
         return Either.left( reportForModelResolutionException( exception ) );
      } catch ( final Exception exception ) {
         // Any other exception, e.g., resolution exception
         return Either.left( reportForDefaultException( exception ) );
      }
   }

   private static ViolationReport reportForDefaultException( final Exception exception ) {
      return new ViolationReport( ProcessingViolationBuilder.builder()
            .message( exception.getMessage() )
            .cause( Optional.of( exception ) )
            .build() );
   }

   private static ViolationReport reportForAspectLoadingException( final AspectLoadingException exception ) {
      if ( exception.getMessage() == null ) {
         return new ViolationReport( ProcessingViolationBuilder.builder()
               .message( "Failed to load Aspect Model" )
               .cause( Optional.ofNullable( exception.getCause() ) )
               .build() );
      }
      if ( exception.getSourceDocument() != null ) {
         return new ViolationReport( AspectLoadingViolationBuilder.builder()
               .message( exception.getMessage() )
               .sourceDocument( exception.getSourceDocument() )
               .documentContent( exception.getDocumentContent() )
               .highlight( exception.highlightElement() )
               .build() );
      }

      return new ViolationReport( ProcessingViolationBuilder.builder()
            .message( exception.getMessage() )
            .cause( Optional.ofNullable( exception.getCause() ) )
            .build() );
   }

   private static ViolationReport reportForModelResolutionException( final ModelResolutionException exception ) {
      return exception.getCheckedLocations().isEmpty()
            ? new ViolationReport( new ProcessingViolation( exception.getMessage(), Optional.of( exception ) ) )
            : new ViolationReport( exception.getCheckedLocations().stream().<Violation>map( Function.identity() ).toList() );
   }

   private static ViolationReport reportForValueParsingException( final ValueParsingException exception ) {
      return new ViolationReport( InvalidLexicalValueViolationBuilder.builder()
            .type( exception.getType() )
            .value( exception.getValue() )
            .location( new Location( (int) exception.getLine(), (int) exception.getColumn() ) )
            .sourceLine( exception.getSourceDocument().lines().toList().get( (int) exception.getLine() - 1 ) )
            .sourceDocument( exception.getSourceLocation() )
            .documentContent( exception::getSourceDocument )
            .build() );
   }

   private static ViolationReport reportForParserException( final ParserException exception ) {
      return new ViolationReport( InvalidSyntaxViolationBuilder.builder()
            .message( exception.getMessage() )
            .sourceDocument( exception.getSourceLocation() )
            .documentContent( exception::getSourceDocument )
            .location( new Location( (int) exception.getLine() - 1, (int) exception.getColumn() - 1 ) )
            .build() );
   }

   private static class CancelValidation extends RuntimeException {
      private final ViolationReport violations;

      private CancelValidation( final ViolationReport violations ) {
         this.violations = violations;
      }
   }

   @SuppressWarnings( "unchecked" )
   @Override
   public <E extends RuntimeException> E cancelValidation( final ViolationReport violations ) {
      return (E) new CancelValidation( violations );
   }

   /**
    * Validates an Aspect Model.
    *
    * @param aspectModel the Aspect Model
    * @return a list of {@link Violation}s. An empty list indicates that the model is valid.
    */
   @Override
   public ViolationReport validateModel( final AspectModel aspectModel ) {
      final Model mergedModel = buildMergedModel( aspectModel.files() );
      return validateModel( mergedModel );
   }

   private Model buildMergedModel( final Collection<AspectModelFile> files ) {
      final Stream<Map.Entry<URI, Model>> filesStream =
            files.stream().map( file -> Map.entry( file.sourceUri(), file.sourceModel() ) );
      final Stream<Map.Entry<URI, Model>> metaModelFilesStream =
            Stream.of( Map.entry( URI.create( SammNs.SAMM.getUri() ), MetaModelFile.metaModelDefinitions() ) );
      final Map<URI, Model> graphContent = Stream.concat( filesStream, metaModelFilesStream ).collect( asMap() );
      return RdfUtil.mergedView( graphContent );
   }

   /**
    * Validates an Aspect Model. Note that the model needs to include the SAMM meta model definitions
    * to yield correct validation results.
    *
    * @param model the Aspect Model
    * @return a list of {@link Violation}s. An empty list indicates that the model is valid.
    */
   @Override
   public ViolationReport validateModel( final Model model ) {
      final Stream<Supplier<RdfBasedValidator>> builtInValidators = Stream.of(
            () -> shaclValidator,
            ModelCycleDetector::new,
            RegularExpressionExampleValueValidator::new
      );
      final Stream<Supplier<RdfBasedValidator>> extensionValidators =
            ExtensionService.getRdfBasedValidators().stream()
                  .map( validator -> () -> validator );
      return Stream.concat( builtInValidators, extensionValidators )
            .map( validator -> validator.get().validateModel( model ) )
            .filter( result -> !result.isEmpty() )
            .reduce( ViolationReport::merge )
            .orElse( ViolationReport.EMPTY );
   }

   /**
    * Validates a single model element.
    *
    * @param element the aspect model element to validate. The element MUST be part of the fully
    *        resolved model (i.e., element.getModel() should return the same value as
    *        versionedModel.getModel())
    * @return the list of violations
    */
   public List<Violation> validateElement( final Resource element ) {
      return shaclValidator.validateElement( element );
   }
}
