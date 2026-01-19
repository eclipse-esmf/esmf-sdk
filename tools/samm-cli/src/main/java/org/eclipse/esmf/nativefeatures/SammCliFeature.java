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

package org.eclipse.esmf.nativefeatures;

import static org.eclipse.esmf.nativefeatures.AssetAdministrationShellFeature.ADMINSHELL_PROPERTIES;

import java.util.List;

import org.eclipse.esmf.SammCli;
import org.eclipse.esmf.aas.AasCommand;
import org.eclipse.esmf.aas.AasListSubmodelsCommand;
import org.eclipse.esmf.aas.AasToCommand;
import org.eclipse.esmf.aas.to.AasToAspectCommand;
import org.eclipse.esmf.aspect.AspectCommand;
import org.eclipse.esmf.aspect.AspectEditCommand;
import org.eclipse.esmf.aspect.AspectPrettyPrintCommand;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspect.AspectUsageCommand;
import org.eclipse.esmf.aspect.AspectValidateCommand;
import org.eclipse.esmf.aspect.edit.AspectEditNewVersionCommand;
import org.eclipse.esmf.aspect.to.AspectToAasCommand;
import org.eclipse.esmf.aspect.to.AspectToAsyncapiCommand;
import org.eclipse.esmf.aspect.to.AspectToHtmlCommand;
import org.eclipse.esmf.aspect.to.AspectToJavaCommand;
import org.eclipse.esmf.aspect.to.AspectToJsonCommand;
import org.eclipse.esmf.aspect.to.AspectToJsonLdCommand;
import org.eclipse.esmf.aspect.to.AspectToJsonSchemaCommand;
import org.eclipse.esmf.aspect.to.AspectToOpenapiCommand;
import org.eclipse.esmf.aspect.to.AspectToPngCommand;
import org.eclipse.esmf.aspect.to.AspectToSqlCommand;
import org.eclipse.esmf.aspect.to.AspectToSvgCommand;
import org.eclipse.esmf.aspect.to.AspectToTsCommand;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.exception.SubCommandException;
import org.eclipse.esmf.substitution.AdminShellConfig;

import org.graalvm.nativeimage.hosted.Feature;

public class SammCliFeature implements Feature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      Native.forClass( AdminShellConfig.class ).initializeAtBuildTime();

      Native.forClass( SammCli.class ).registerEverythingForReflection();

      Native.forClass( AasCommand.class ).registerEverythingForReflection();
      Native.forClass( AasListSubmodelsCommand.class ).registerEverythingForReflection();
      Native.forClass( AasToCommand.class ).registerEverythingForReflection();

      Native.forClass( AasToAspectCommand.class ).registerEverythingForReflection();

      Native.forClass( AspectCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectEditCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectPrettyPrintCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectUsageCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectValidateCommand.class ).registerEverythingForReflection();

      Native.forClass( AspectEditCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectEditNewVersionCommand.class ).registerEverythingForReflection();

      Native.forClass( AspectToAasCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToAsyncapiCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToHtmlCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToJavaCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToTsCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToJsonCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToJsonLdCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToJsonSchemaCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToOpenapiCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToPngCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToSqlCommand.class ).registerEverythingForReflection();
      Native.forClass( AspectToSvgCommand.class ).registerEverythingForReflection();

      Native.forClass( CommandException.class ).registerEverythingForReflection();
      Native.forClass( SubCommandException.class ).registerEverythingForReflection();

      Native.addResource( "application.properties" );
      Native.addResource( "git.properties" );
      Native.addResource( "pom.properties" );
      Native.addResource( "logback.xml" );
      Native.addResource( ADMINSHELL_PROPERTIES );
   }

   @Override
   public List<Class<? extends Feature>> getRequiredFeatures() {
      return List.of( EsmfFeature.class );
   }
}
