/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

package org.eclipse.esmf.aspect.to;

import java.io.IOException;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.generator.protobuf.AspectModelProtobufGenerator;
import org.eclipse.esmf.aspectmodel.generator.protobuf.ProtobufArtifact;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;

import picocli.CommandLine;

/**
 * CLI command that generates a proto3 schema for an Aspect Model.
 */
@CommandLine.Command(
   name = AspectToProtobufCommand.COMMAND_NAME,
   description = "Generate Protocol Buffers message schema for an Aspect Model",
   descriptionHeading = "%n@|bold Description|@:%n%n",
   parameterListHeading = "%n@|bold Parameters|@:%n",
   optionListHeading = "%n@|bold Options|@:%n" )
public class AspectToProtobufCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "protobuf";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--output", "-o" },
      description = "Output file path (default: stdout)" )
   private String outputFilePath = "-";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--details" },
      description = "Print detailed reports on errors" )
   private boolean details = false;

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final Aspect aspect = getInputHandler( parentCommand.parentCommand.getInput() ).loadAspect();
      final ProtobufArtifact schema = new AspectModelProtobufGenerator( aspect ).singleResult();
      withOutputStream( outputFilePath, outputStream -> {
         try {
            outputStream.write( schema.serialize() );
         } catch ( final IOException exception ) {
            throw new CommandException( "Could not write Protobuf schema", exception );
         }
      } );
   }
}
