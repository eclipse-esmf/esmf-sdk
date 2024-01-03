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
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.eclipse.esmf.functions.ThrowingBiConsumer;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;

/**
 * Generator that generates an AAS file containing an AAS submodel for a given Aspect model
 */
public class AspectModelAASGenerator {
   public enum Format {
      AASX( ( environment, output ) -> new AASXSerializer().write( environment, null, output ) ),
      XML( ( environment, output ) -> output.write( new XmlSerializer().write( environment ).getBytes() ) ),
      JSON( ( environment, output ) -> output.write( new JsonSerializer().write( environment ).getBytes() ) );

      private final ThrowingBiConsumer<Environment, OutputStream, Throwable> serializer;

      @Override
      public String toString() {
         return switch ( this ) {
            case AASX -> "aasx";
            case XML -> "xml";
            case JSON -> "json";
         };
      }

      Format( final ThrowingBiConsumer<Environment, OutputStream, Throwable> serializer ) {
         this.serializer = serializer;
      }

      public ThrowingBiConsumer<Environment, OutputStream, Throwable> getSerializer() {
         return serializer;
      }
   }

   /**
    * Generates an AAS file for a given Aspect
    * @param format the output format
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @return the generated AAS file as byte array
    */
   public byte[] generateAsByteArray( final Format format, final Aspect aspect ) {
      return generateAsByteArray( format, aspect, null );
   }

   /**
    * Generates an AAS file for a given Aspect and its corresponding payload
    * @param format the output format
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @return the generated AAS file as byte array
    * @throws AasGenerationException in case the generation can not properly be executed
    */
   public byte[] generateAsByteArray( final Format format, final Aspect aspect, final JsonNode aspectData ) {
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      generate( format, aspect, aspectData, name -> baos );
      return baos.toByteArray();
   }

   /**
    * Generates an AAS file for a given Aspect and writes it to a given OutputStream provided by {@code nameMapper}
    *
    * @param format the output format
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    */
   public void generate( final Format format, final Aspect aspect, final Function<String, OutputStream> nameMapper ) {
      generate( format, aspect, null, nameMapper );
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
    */
   public void generate( final Format format, final Aspect aspect, @Nullable final JsonNode aspectData,
         final Function<String, OutputStream> nameMapper ) {
      try ( final OutputStream output = nameMapper.apply( aspect.getName() ) ) {
         final AspectModelAASVisitor visitor = new AspectModelAASVisitor().withPropertyMapper( new LangStringPropertyMapper() );
         final Context context;
         if ( aspectData != null ) {
            final Submodel submodel = new DefaultSubmodel.Builder().build();
            final Environment inputEnvironment = new DefaultEnvironment.Builder().submodels( List.of( submodel ) ).build();
            context = new Context( inputEnvironment, submodel );
            context.setEnvironment( inputEnvironment );
            context.setAspectData( aspectData );
         } else {
            context = null;
         }

         final Environment environment = visitor.visitAspect( aspect, context );
         format.getSerializer().accept( environment, output );
      } catch ( final Throwable exception ) {
         throw new AasGenerationException( exception );
      }
   }

   /**
    * Generates an AASX archive file for a given Aspect and writes it to a given OutputStream provided by {@code nameMapper}
    *
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated use {@link #generate(Format, Aspect, Function)} instead
    */
   @Deprecated( forRemoval = true )
   public void generateAASXFile( final Aspect aspect, final Function<String, OutputStream> nameMapper ) {
      generate( Format.AASX, aspect, nameMapper );
   }

   /**
    * Generates an AASX archive file for a given Aspect and its corresponding payload and writes it to a given OutputStream
    *
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @param aspectData the JSON data corresponding to the Aspect
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated use {@link #generate(Format, Aspect, JsonNode, Function)} instead
    */
   @Deprecated( forRemoval = true )
   public void generateAASXFile( final Aspect aspect, final JsonNode aspectData, final Function<String, OutputStream> nameMapper ) {
      generate( Format.AASX, aspect, aspectData, nameMapper );
   }

   /**
    * Generates an AAS XML file for a given Aspect and writes it to a given OutputStream provided by {@code nameMapper}
    *
    * @param aspect the Aspect for which an xml file shall be generated
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated use {@link #generate(Format, Aspect, Function)} instead
    */
   @Deprecated( forRemoval = true )
   public void generateAasXmlFile( final Aspect aspect, final Function<String, OutputStream> nameMapper ) {
      generate( Format.XML, aspect, nameMapper );
   }

   /**
    * Generates an AAS XML file for a given Aspect and its corresponding payload and writes it to a given OutputStream provided
    * by {@code nameMapper}
    *
    * @param aspect the Aspect for which an xml file shall be generated
    * @param aspectData the JSON data corresponding to the Aspect
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated use {@link #generate(Format, Aspect, JsonNode, Function)} instead
    */
   @Deprecated( forRemoval = true )
   public void generateAasXmlFile( final Aspect aspect, final JsonNode aspectData, final Function<String, OutputStream> nameMapper ) {
      generate( Format.XML, aspect, aspectData, nameMapper );
   }

   /**
    * Generates an AAS JSON file for a given Aspect and writes it to a given OutputStream provided by {@code nameMapper}
    *
    * @param aspect the Aspect for which an JSON shall be generated
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated use {@link #generate(Format, Aspect, Function)} instead
    */
   @Deprecated( forRemoval = true )
   public void generateAasJsonFile( final Aspect aspect, final Function<String, OutputStream> nameMapper ) {
      generate( Format.JSON, aspect, nameMapper );
   }

   /**
    * Generates an AAS JSON file for a given Aspect and its corresponding payload and writes it to a given OutputStream provided
    * by {@code nameMapper}
    *
    * @param aspect the Aspect for which an JSON shall be generated
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws AasGenerationException in case the generation can not properly be executed
    * @deprecated use {@link #generate(Format, Aspect, JsonNode, Function)} instead
    */
   @Deprecated( forRemoval = true )
   public void generateAasJsonFile( final Aspect aspect, final JsonNode aspectData, final Function<String, OutputStream> nameMapper ) {
      generate( Format.JSON, aspect, aspectData, nameMapper );
   }
}
