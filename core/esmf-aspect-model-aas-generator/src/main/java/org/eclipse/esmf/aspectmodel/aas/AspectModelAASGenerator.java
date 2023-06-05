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
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.aasx.AASXSerializer;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.xml.XmlSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;

import com.fasterxml.jackson.databind.JsonNode;

import org.eclipse.esmf.metamodel.Aspect;

/**
 * Generator that generates an AASX file containing an AAS submodel for a given Aspect model
 */
public class AspectModelAASGenerator {

   /**
    * Generates an AASX archive file for a given Aspect and writes it to a given OutputStream provided by <code>nameMapper<code/>
    *
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws IOException in case the generation can not properly be executed
    */
   public void generateAASXFile( final Aspect aspect, final Function<String, OutputStream> nameMapper )
         throws IOException {
      try ( final OutputStream output = nameMapper.apply( aspect.getName() ) ) {
         output.write( generateAasxOutput( aspect ).toByteArray() );
      }
   }

   /**
    * Generates an AAS XML archive file for a given Aspect and writes it to a given OutputStream provided by <code>nameMapper<code/>
    *
    * @param aspect the Aspect for which an AASX archive shall be generated
    * @param nameMapper a Name Mapper implementation, which provides an OutputStream for a given filename
    * @throws IOException in case the generation can not properly be executed
    */
   public void generateAasXmlFile(
         final Aspect aspect, final Function<String, OutputStream> nameMapper ) throws IOException {
      try ( final OutputStream output = nameMapper.apply( aspect.getName() ) ) {
         output.write( generateXmlOutput( aspect ).toByteArray() );
      }
   }

   public void generateAasXmlFile(
         final Aspect aspect, final JsonNode aspectData, final Function<String, OutputStream> nameMapper ) throws IOException {
      try ( final OutputStream output = nameMapper.apply( aspect.getName() ) ) {
         output.write( generateXmlOutput( Map.of( aspect, aspectData ) ).toByteArray() );
      }
   }

   protected ByteArrayOutputStream generateXmlOutput( final Map<Aspect, JsonNode> aspectsWithData ) throws IOException {
      final AspectModelAASVisitor visitor = new AspectModelAASVisitor().withPropertyMapper( new LangStringPropertyMapper() );

      final Map<Aspect, Environment> aspectEnvironments =
            aspectsWithData.entrySet().stream()
                  .map( aspectWithData -> {
                     final Submodel submodel = new DefaultSubmodel.Builder().build();
                     final Environment environment = new DefaultEnvironment.Builder().submodels( Collections.singletonList( submodel ) ).build();
                     final Context context = new Context( environment, submodel );
                     context.setEnvironment( environment );
                     context.setAspectData( aspectWithData.getValue() );

                     return Map.entry( aspectWithData.getKey(), visitor.visitAspect( aspectWithData.getKey(), context ) );
                  } )
                  .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );
      final Environment mergedEnvironment = mergeEnvironments( aspectEnvironments );
      try ( final ByteArrayOutputStream out = new ByteArrayOutputStream() ) {
         final XmlSerializer serializer = new XmlSerializer();
         serializer.write( out, mergedEnvironment );
         return out;
      } catch ( final SerializationException e ) {
         throw new IOException( e );
      }
   }

   private Environment mergeEnvironments( final Map<Aspect, Environment> aspectEnvironments ) {
      final Submodel submodel = new DefaultSubmodel.Builder().build();
      final Environment environment = new DefaultEnvironment.Builder()
            .assetAdministrationShells( aspectEnvironments.values().stream().flatMap( e -> e.getAssetAdministrationShells().stream() ).toList() )
            .submodels( aspectEnvironments.values().stream().flatMap( e -> e.getSubmodels().stream() ).toList() )
            .conceptDescriptions( aspectEnvironments.values().stream().flatMap( e -> e.getConceptDescriptions().stream() ).toList() )
            .build();

      return environment;
   }

   protected ByteArrayOutputStream generateAasxOutput( final Aspect aspect ) throws IOException {
      final AspectModelAASVisitor visitor = new AspectModelAASVisitor();
      final Environment environment = visitor.visitAspect( aspect, null );

      try ( final ByteArrayOutputStream out = new ByteArrayOutputStream() ) {
         final AASXSerializer serializer = new AASXSerializer();
         serializer.write( environment, null, out );
         return out;
      } catch ( final SerializationException e ) {
         throw new IOException( e );
      }
   }

   protected ByteArrayOutputStream generateXmlOutput( final Aspect aspect ) throws IOException {
      final AspectModelAASVisitor visitor = new AspectModelAASVisitor();
      final Environment environment = visitor.visitAspect( aspect, null );

      try ( final ByteArrayOutputStream out = new ByteArrayOutputStream() ) {
         final XmlSerializer serializer = new XmlSerializer();
         serializer.write( out, environment );
         return out;
      } catch ( final SerializationException e ) {
         throw new IOException( e );
      }
   }
}
