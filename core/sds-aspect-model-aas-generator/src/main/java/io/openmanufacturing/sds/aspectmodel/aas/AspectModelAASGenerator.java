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
package io.openmanufacturing.sds.aspectmodel.aas;

import io.adminshell.aas.v3.dataformat.SerializationException;
import io.adminshell.aas.v3.dataformat.aasx.AASXSerializer;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.impl.DefaultAssetAdministrationShellEnvironment;
import io.openmanufacturing.sds.metamodel.Aspect;

import java.io.*;
import java.util.function.Function;

/**
 * Generator that generates a AASX file containing a AAS submodel for a given Aspect model
 */
public class AspectModelAASGenerator {


   public void generateAASXFile(final Aspect aspect, final Function<String, OutputStream> nameMapper ) throws IOException {
      try ( final OutputStream output = nameMapper.apply( aspect.getName() ) ) {
         output.write( generateOutput(aspect).toByteArray() );
      }
   }

   public ByteArrayOutputStream generateOutput (Aspect aspect) throws IOException {
      final AspectModelAASVisitor visitor = new AspectModelAASVisitor();

      AssetAdministrationShellEnvironment environment = new DefaultAssetAdministrationShellEnvironment.Builder().
              submodels(visitor.visitAspect( aspect, null ))
              .build();

      try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
         new AASXSerializer().write(environment, null, out);
         return out;
      }catch (SerializationException e){
         throw new IOException(e);
      }
   }
}