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
package io.openmanufacturing.sds;

import java.io.File;

import com.beust.jcommander.Parameter;

public class Args {

   @Parameter( names = { "--help", "-h" }, help = true, description = "Display help" )
   public boolean help;

   @Parameter( names = { "--generate-documentation", "-html" },
         description = "Generate HTML documentation for an Aspect Model" )
   public boolean generateHtml = false;

   @Parameter( names = { "--html-custom-css-file", "-hccf" },
         description = "CSS file with custom styles to be included in the generated HTML documentation" )
   public String htmlCustomCSSFile;

   @Parameter( names = { "--generate-svg-diagram", "-svg" },
         description = "Generate SVG diagram for an Aspect Model" )
   public boolean generateSvgDiagram = false;

   @Parameter( names = { "--generate-png-diagram", "-png" },
         description = "Generate PNG diagram for an Aspect Model" )
   public boolean generatePngDiagram = false;

   @Parameter( names = { "--generate-dot-diagram", "-dot" },
         description = "Generate DOT (graphviz) diagram for an Aspect Model" )
   public boolean generateDotDiagram = false;

   @Parameter( names = { "--generate-json-payload", "-json" },
         description = "Generate sample JSON payload for an Aspect Model" )
   public boolean generateJsonPayload = false;

   @Parameter( names = { "--generate-json-schema", "-schema" },
         description = "Generate JSON schema for an Aspect Model" )
   public boolean generateJsonSchema = false;

   @Parameter( names = { "--generate-openapi-json", "-oapi-json" },
         description = "Generate OpenAPI JSON specification for an Aspect Model" )
   public boolean generateJsonOpenApiSpec = false;

   @Parameter( names = { "--generate-openapi-yaml", "-oapi-yaml" },
         description = "Generate OpenAPI YAML specification for an Aspect Model" )
   public boolean generateYamlOpenApiSpec = false;

   @Parameter( names = { "--generate-java-classes", "-java" },
         description = "Generate Java domain classes for an Aspect Model" )
   public boolean generateAspectModelJavaClasses = false;

   @Parameter( names = { "--generate-static-java-classes", "-static-java" },
         description = "Generate Java domain classes for an Static Meta Model" )
   public boolean generateStaticMetaModelJavaClasses = false;

   @Parameter( names = { "--input", "-i" },
         required = true,
         description = "Input file name of the Aspect Model .ttl file",
         converter = FileConverter.class )
   public File input;

   @Parameter( names = { "--package-name", "-pn" },
         validateWith = IsValueSet.class,
         description = "Package to use for generated Java classes" )
   public String packageName = "";

   @Parameter( names = { "--output-directory", "-d" },
         description = "Output directory to write files to",
         converter = FileConverter.class )
   public File outputPath = new File( "." );

   @Parameter( names = { "--validate", "-v" },
         description = "Validate input Aspect Model" )
   public boolean validateModel = false;

   @Parameter( names = { "--migrate", "-m" },
         description = "Migrate Aspect Model to latest Meta Model Version" )
   public boolean migrateModel = false;

   @Parameter( names = { "--pretty-print", "-p" },
         description = "Pretty print (format) Aspect Model" )
   public boolean prettyPrint = false;

   @Parameter( names = { "--disable-jackson-annotations", "-dja" },
         description = "Disable Jackson annotation generation in generated Java classes" )
   public boolean disableJacksonAnnotations = false;

   @Parameter( names = { "--use-semantic-api-version", "-sv" },
         description = "Use the full semantic version from the Aspect Model as the version for the Aspect API." )
   public boolean useSemanticApiVersion = false;

   @Parameter( names = { "--aspect-api-base-url", "-base-url" },
         description = "The base url for the Aspect API used in the OpenAPI specification." )
   public String aspectApiBaseUrl = "";

   @Parameter( names = { "--include-aspect-query-api", "-query-api" },
         description = "Include the path for the Query Aspect API Endpoint in the OpenAPI specification." )
   public boolean includeQueryApi = false;

   @Parameter( names = { "--aspect-resource-path", "-arp" },
         description = "The resource path for the Aspect API endpoints." )
   public String aspectResourcePath;

   @Parameter( names = { "--aspect-parameter-file", "-apf" },
         description = "The path to a file including the parameter for the Aspect API endpoints." )
   public String aspectParameterFile;

   @Parameter( names = { "--aspect-exclude-paging", "-no-paging" },
         description = "Exclude paging information for the Aspect API Endpoint in the OpenAPI specification." )
   public boolean excludePaging = false;

   @Parameter( names = { "--aspect-time-based-paging", "-atbp" },
         description = "In case there is more than one paging possibility, it has to be time based paging." )
   public boolean aspectTimeBasedPaging = false;

   @Parameter( names = { "--aspect-offset-based-paging", "-aobp" },
         description = "In case there is more than one paging possibility, it has to be offset based paging." )
   public boolean aspectOffsetBasedPaging = false;

   @Parameter( names = { "--aspect-cursor-based-paging", "-acbp" },
         description = "In case there is more than one paging possibility, it has to be cursor based paging." )
   public boolean aspectCursorBasedPaging = false;

   @Parameter( names = { "--execute-library-macros", "-elm" },
         description = "Execute the macros provided in the velocity macro library." )
   public boolean executeLibraryMacros = false;

   @Parameter( names = { "--template-library-path", "-tlp" },
         description = "The resource path for the velocity macro library." )
   public String templateLibPath = "";

   @Parameter( names = { "--template-library-file-name", "-tlfn" },
         description = "The name of the velocity template file containing the macro library." )
   public String templateLibFileName = "";
}
