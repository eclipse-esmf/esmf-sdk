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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;

/**
 * Generator that generates an AAS file containing an AAS submodel for a given Aspect model.
 */
public class AspectModelAasGenerator extends AspectGenerator<String, byte[], AasGenerationConfig, AasArtifact> {
   /*
    * This should be removed together with the deprecated constructors
    */
   @Deprecated
   private List<PropertyMapper<?>> propertyMappers = List.of();

   public AspectModelAasGenerator( final Aspect aspect, final AasGenerationConfig config ) {
      super( aspect, config );
   }

   @Deprecated( forRemoval = true )
   public AspectModelAasGenerator() {
      super( null, null );
   }

   @Deprecated( forRemoval = true )
   public AspectModelAasGenerator( final List<PropertyMapper<?>> propertyMappers ) {
      super( null, null );
      this.propertyMappers = propertyMappers;
   }

   @Override
   public Stream<AasArtifact> generate() {
      final AspectModelAasVisitor visitor = new AspectModelAasVisitor().withPropertyMapper( new LangStringPropertyMapper() );
      config.propertyMappers().forEach( visitor::withPropertyMapper );
      final Context context;
      if ( config.aspectData() != null ) {
         final Submodel submodel = new DefaultSubmodel.Builder().build();
         final Environment inputEnvironment = new DefaultEnvironment.Builder().submodels( List.of( submodel ) ).build();
         context = new Context( inputEnvironment, submodel );
         context.setEnvironment( inputEnvironment );
         context.setAspectData( config.aspectData() );
      } else {
         context = null;
      }

      try {
         final Environment environment = visitor.visitAspect( aspect(), context );
         final byte[] result = switch ( config.format() ) {
            case XML -> new XmlSerializer().write( environment ).getBytes();
            case JSON -> new JsonSerializer().write( environment ).getBytes();
            case AASX -> {
               final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
               new AASXSerializer().write( environment, null, outputStream );
               yield outputStream.toByteArray();
            }
         };
         return Stream.of( new AasArtifact( aspect().getName() + "." + config.format(), result ) );
      } catch ( final SerializationException | IOException exception ) {
         throw new AasGenerationException( exception );
      }
   }

   /**
    * Generates an AAS file for a given Aspect.
    *
    * @param format the output format
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @return the generated AAS file as byte array
    * @deprecated Use {@link #AspectModelAasGenerator(Aspect, AasGenerationConfig)} and {@link #getContent()} instead
    */
   @Deprecated( forRemoval = true )
   public byte[] generateAsByteArray( final AasFileFormat format, final Aspect aspect ) {
      final AasGenerationConfig generationConfig = AasGenerationConfigBuilder.builder()
            .format( format )
            .propertyMappers( propertyMappers )
            .build();
      return new AspectModelAasGenerator( aspect, generationConfig ).getContent();
   }

   /**
    * Generates an AAS file for a given Aspect and its corresponding payload.
    *
    * @param format the output format
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @return the generated AAS file as byte array
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated Use {@link #AspectModelAasGenerator(Aspect, AasGenerationConfig)} and {@link #getContent()} instead
    */
   @Deprecated( forRemoval = true )
   public byte[] generateAsByteArray( final AasFileFormat format, final Aspect aspect, final JsonNode aspectData ) {
      final AasGenerationConfig generationConfig = AasGenerationConfigBuilder.builder()
            .format( format )
            .aspectData( aspectData )
            .propertyMappers( propertyMappers )
            .build();
      return new AspectModelAasGenerator( aspect, generationConfig ).getContent();
   }

   /**
    * Generates an AAS file for a given Aspect and writes it to a given OutputStream provided by {@code nameMapper}
    *
    * @param format the output format
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated Use {@link #AspectModelAasGenerator(Aspect, AasGenerationConfig)} and {@link #generate(Function)} instead
    */
   @Deprecated( forRemoval = true )
   public void generate( final AasFileFormat format, final Aspect aspect, final Function<String, OutputStream> nameMapper ) {
      final AasGenerationConfig generationConfig = AasGenerationConfigBuilder.builder()
            .format( format )
            .propertyMappers( propertyMappers )
            .build();
      new AspectModelAasGenerator( aspect, generationConfig ).generate( nameMapper );
   }

   /**
    * Generates an AAS file for a given Aspect and its corresponding payload and writes it to a given OutputStream provided by
    * {@code nameMapper}
    *
    * @param format the output format
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @param aspectData the JSON data corresponding to the Aspect, may be null
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated Use {@link #AspectModelAasGenerator(Aspect, AasGenerationConfig)} and {@link #generate(Function)} instead
    */
   @Deprecated( forRemoval = true )
   public void generate( final AasFileFormat format, final Aspect aspect, @Nullable final JsonNode aspectData,
         final Function<String, OutputStream> nameMapper ) {
      final AasGenerationConfig generationConfig = AasGenerationConfigBuilder.builder()
            .format( format )
            .aspectData( aspectData )
            .propertyMappers( propertyMappers )
            .build();
      new AspectModelAasGenerator( aspect, generationConfig ).generate( nameMapper );
   }
}
