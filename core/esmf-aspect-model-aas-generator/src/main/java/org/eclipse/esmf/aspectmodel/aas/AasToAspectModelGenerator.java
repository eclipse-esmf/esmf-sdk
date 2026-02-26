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

package org.eclipse.esmf.aspectmodel.aas;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectArtifact;
import org.eclipse.esmf.aspectmodel.generator.Generator;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.datatype.LangString;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.ModellingKind;

public class AasToAspectModelGenerator extends Generator<Environment, AspectModelUrn, Aspect, AspectGenerationConfig, AspectArtifact> {
   public static final AspectGenerationConfig DEFAULT_CONFIG = AspectGenerationConfigBuilder.builder().build();

   private AasToAspectModelGenerator( final Environment aasEnvironment ) {
      super( aasEnvironment, DEFAULT_CONFIG );
   }

   public static AasToAspectModelGenerator fromAasXml( final InputStream inputStream ) {
      try {
         return fromEnvironment( new XmlDeserializer().read( inputStream ) );
      } catch ( final DeserializationException exception ) {
         throw new AspectModelGenerationException( exception );
      }
   }

   public static AasToAspectModelGenerator fromAasx( final InputStream inputStream ) {
      try {
         final AASXDeserializer deserializer = new AASXDeserializer( inputStream );
         return fromEnvironment( new XmlDeserializer().read( deserializer.getResourceString() ) );
      } catch ( final InvalidFormatException | IOException | DeserializationException exception ) {
         throw new AspectModelGenerationException( exception );
      }
   }

   public static AasToAspectModelGenerator fromAasJson( final InputStream inputStream ) {
      final JsonDeserializer deserializer = new JsonDeserializer();
      try {
         return fromEnvironment( deserializer.read( inputStream, Environment.class ) );
      } catch ( final DeserializationException exception ) {
         throw new AspectModelGenerationException( exception );
      }
   }

   public static AasToAspectModelGenerator fromEnvironment( final Environment environment ) {
      return new AasToAspectModelGenerator( environment );
   }

   public static AasToAspectModelGenerator fromFile( final java.io.File file ) {
      try ( final InputStream inputStream = new FileInputStream( file ) ) {
         return switch ( FilenameUtils.getExtension( file.getAbsolutePath() ) ) {
            case "xml" -> AasToAspectModelGenerator.fromAasXml( inputStream );
            case "aasx" -> AasToAspectModelGenerator.fromAasx( inputStream );
            case "json" -> AasToAspectModelGenerator.fromAasJson( inputStream );
            default -> throw new AspectModelGenerationException(
                  "Invalid file extension on file " + file + ", allowed extensions are " + AasFileFormat.allValues() );
         };
      } catch ( final IOException exception ) {
         throw new AspectModelGenerationException( exception );
      }
   }

   @Override
   public Stream<AspectArtifact> generate() {
      final SubmodelToAspectConverter converter = new SubmodelToAspectConverter( conceptDescriptionInfoById( getFocus() ) );
      return getFocus().getSubmodels()
            .stream()
            .filter( submodel -> submodel.getKind().equals( ModellingKind.TEMPLATE ) )
            .map( converter::convert )
            .map( aspect -> new AspectArtifact( aspect.urn(), aspect ) );
   }

   private static Map<String, ConceptDescriptionInfo> conceptDescriptionInfoById( final Environment environment ) {
      return Optional.ofNullable( environment.getConceptDescriptions() ).orElseGet( List::of ).stream()
            .filter( conceptDescription -> conceptDescription.getId() != null )
            .map( conceptDescription -> {
               final Set<LangString> descriptions = descriptions( conceptDescription );
               final Set<LangString> displayNames = displayNames( conceptDescription );
               return Map.entry( conceptDescription.getId(),
                     new ConceptDescriptionInfo(
                           conceptDescription.getId(),
                           conceptDescription.getIdShort(),
                           descriptions,
                           displayNames ) );
            } )
            .collect( Collectors.toMap(
                  Map.Entry::getKey,
                  Map.Entry::getValue,
                  ( existing, ignored ) -> existing ) );
   }

   private static Set<LangString> descriptions( final ConceptDescription conceptDescription ) {
      return SubmodelToAspectUtils.langStringSet( conceptDescription.getDescription() );
   }

   private static Set<LangString> displayNames( final ConceptDescription conceptDescription ) {
      return SubmodelToAspectUtils.langStringSet( conceptDescription.getDisplayName() );
   }
}
