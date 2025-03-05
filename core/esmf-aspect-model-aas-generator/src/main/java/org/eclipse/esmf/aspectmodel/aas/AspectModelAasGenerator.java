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
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.metamodel.Aspect;

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
   public AspectModelAasGenerator( final Aspect aspect, final AasGenerationConfig config ) {
      super( aspect, config );
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
}
