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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.aas.AspectModelAASGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.openapi.PagingOption;
import io.openmanufacturing.sds.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.java.pojo.AspectModelJavaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.FileSystemStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.serializer.PrettyPrinter;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.util.FailureLog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("squid:S1147") // System.exit is really required here, this is a CLI tool
public class BammCli {
    private static final Logger LOG = LoggerFactory.getLogger(BammCli.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static void fileNotFound(final Args args, final Throwable exception) {
        LOG.error("Can not open file for reading: {}", args.input, exception);
        System.exit(1);
    }

    private static FileOutputStream getStreamForFile(final String path, final String artifactName, final Args args) {
        try {
            final File directory = new File(args.outputPath.getPath() + File.separator + path);
            directory.mkdirs();
            final File file = new File(directory.getPath() + File.separator + artifactName);
            return new FileOutputStream(file);
        } catch (final FileNotFoundException exception) {
            fileNotFound(args, exception);
        }
        return null;
    }

    private static FileOutputStream getStreamForFile(final String artifactName, final Args args) {
        return getStreamForFile("", artifactName, args);
    }

    private static void printHelp(final JCommander jCommander) {
        final Properties properties = new Properties();
        Stream.of("application", "git")
                .map(fileName -> BammCli.class.getResourceAsStream("/" + fileName + ".properties"))
                .forEach(inputStream -> {
                    try {
                        properties.load(inputStream);
                        inputStream.close();
                    } catch (final IOException exception) {
                        LOG.error("Error while closing input stream", exception);
                    }
                });

        final String name = properties.getProperty("application.name");
        final String version = properties.getProperty("version");
        final String buildDate = properties.getProperty("build.date");
        LOG.warn("{}\n  Version: {}\n  Build date: {}", name, version, buildDate);

        if (version.contains("SNAPSHOT") && LOG.isWarnEnabled()) {
            LOG.warn("  Commit Hash: {}", properties.getProperty("git.commit.id").substring(0, 7));
        }

        if (LOG.isWarnEnabled()) {
            LOG.warn("  Supported BAMM Versions: {}\n",
                    KnownVersion.getVersions().stream().map(KnownVersion::toVersionString)
                            .collect(Collectors.joining(", ")));
        }

        jCommander.usage();
    }

    public void run(final String... argv) throws IOException {
        final Args args = new Args();
        final JCommander jCommander = initializeJCommander(argv, args);

        if (args.help) {
            printHelp(jCommander);
            System.exit(0);
        }

        validateInputAndOutputPath(args);
        validateCustomPackageName(args);
        performModelValidation(args);
        performModelMigration(args);
        performPrettyPrinting(args);
        performHtmlGeneration(args);
        performJsonPayloadGeneration(args);
        performJsonSchemaGeneration(args);
        performOpenApiSpecGeneration(args);
        performAasAasxSpecGeneration(args);
        performDiagramGeneration(args);
        performAspectModelJavaGeneration(args);
        performStaticMetaModelJavaGeneration(args);

        System.exit(0);
    }

    public static void main(final String[] argv) throws Exception {
        new BammCli().run(argv);
    }

    private static void performAspectModelJavaGeneration(final Args args) {
        if (args.generateAspectModelJavaClasses) {
            generateAspectModelJavaClasses(args);
        }
    }

    private static void performStaticMetaModelJavaGeneration(final Args args) {
        if (args.generateStaticMetaModelJavaClasses) {
            generateStaticMetaModelJavaClasses(args);
        }
    }

    private static void performDiagramGeneration(final Args args) throws IOException {
        if (args.generatePngDiagram || args.generateSvgDiagram || args.generateDotDiagram) {
            generateDiagram(args);
        }
    }

    private static void performJsonPayloadGeneration(final Args args) throws IOException {
        if (args.generateJsonPayload) {
            generateJsonPayload(args);
        }
    }

    private static void performJsonSchemaGeneration(final Args args) {
        if (args.generateJsonSchema) {
            generateJsonSchema(args);
        }
    }

    private static void performOpenApiSpecGeneration(final Args args) {
        if ((args.generateJsonOpenApiSpec || args.generateYamlOpenApiSpec) && args.aspectApiBaseUrl.isEmpty()) {
            LOG.error("Please provide the base URL for the Aspect API with the option -base-url.");
            System.exit(1);
        }

        if (args.generateJsonOpenApiSpec) {
            generateOpenApiSpecJson(args);
        }
        if (args.generateYamlOpenApiSpec) {
            generateOpenApiSpecYaml(args);
        }
    }

    private static void performAasAasxSpecGeneration(final Args args) throws IOException {
        if (args.generateAasAasxSpec) {
            generateAasAasxSpec(args);
        }
    }

    private static void performHtmlGeneration(final Args args) throws IOException {
        if (args.generateHtml) {
            generateHtml(args);
        }
    }

    private static VersionedModel loadModelOrFail(final Args args) {
        final Try<VersionedModel> versionedModel = loadAndResolveModel(args);
        return versionedModel.recover(throwable -> {
            // Model can not be loaded, root cause e.g. File not found
            if (throwable instanceof IllegalArgumentException) {
                fileNotFound(args, throwable.getCause());
            }

            if (throwable instanceof ModelResolutionException) {
                LOG.error("Could not resolve all model elements", throwable);
                System.exit(1);
            }

            // Another exception, e.g. syntax error. Let the validator handle this
            final AspectModelValidator validator = new AspectModelValidator();
            final ValidationReport report = validator.validate(versionedModel);

            if (LOG.isWarnEnabled()) {
                LOG.warn(report.toString());
            }
            System.exit(1);
            return null;
        }).get();
    }

    private static AspectModelUrn fileToUrn(final File inputFile) {
        final File versionDirectory = inputFile.getParentFile();
        if (versionDirectory == null) {
            LOG.error("Could not determine parent directory of {}", inputFile);
            LOG.error("Please verify that the model directory structure is correct");
            System.exit(1);
        }

        final String version = versionDirectory.getName();
        final File namespaceDirectory = versionDirectory.getParentFile();
        if (namespaceDirectory == null) {
            LOG.error("Could not determine parent directory of {}", versionDirectory);
            LOG.error("Please verify that the model directory structure is correct");
            System.exit(1);
        }

        final String namespace = namespaceDirectory.getName();
        final String aspectName = FilenameUtils.removeExtension(inputFile.getName());
        final String urn = String.format("urn:bamm:%s:%s#%s", namespace, version, aspectName);
        return new SdsAspectMetaModelResourceResolver().getAspectModelUrn(urn).getOrElse(() -> {
            LOG.error("The URN constructed from the input file path is invalid: {}", urn);
            LOG.error("Please verify that the model directory structure is correct");
            System.exit(1);
            return null;
        });
    }

    private static Try<Path> getModelRoot(final File inputFile) {
        return Option.of(Paths.get(inputFile.getParent(), "..", ".."))
                .map(Path::toFile)
                .flatMap(file -> CheckedFunction1.lift(File::getCanonicalFile).apply(file))
                .map(File::toPath)
                .filter(path -> path.toFile().exists() && path.toFile().isDirectory())
                .toTry(() -> new ModelResolutionException("Could not locate models root directory"));
    }

    private static Try<VersionedModel> loadAndResolveModel(final Args args) {
        final File inputFile = args.input.getAbsoluteFile();
        final AspectModelUrn urn = fileToUrn(inputFile);
        return getModelRoot(inputFile).flatMap(modelsRoot ->
                new AspectModelResolver().resolveAspectModel(new FileSystemStrategy(modelsRoot), urn));
    }

    private static Try<VersionedModel> loadButNotResolveModel(final Args args) {
        final File inputFile = args.input.getAbsoluteFile();
        try (final InputStream inputStream = new FileInputStream(inputFile)) {
            final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
            return TurtleLoader.loadTurtle(inputStream).flatMap(model ->
                    metaModelResourceResolver.getBammVersion(model).flatMap(metaModelVersion ->
                            metaModelResourceResolver.mergeMetaModelIntoRawModel(model, metaModelVersion)));
        } catch (final IOException exception) {
            return Try.failure(exception);
        }
    }

    private static void performModelValidation(final Args args) {
        if (args.validateModel) {
            FailureLog.set(new FailureLog() {
                @Override
                public void logFailure(final String message) {
                    // Do not log SHACL-internal errors
                }
            });

            final Try<VersionedModel> versionedModel = loadAndResolveModel(args);
            final AspectModelValidator validator = new AspectModelValidator();
            final ValidationReport report = validator.validate(versionedModel);

            if (LOG.isWarnEnabled()) {
                LOG.warn(report.toString());
            }

            if (!report.conforms()) {
                System.exit(1);
            }
        }
    }

    @SuppressWarnings("squid:S106") // We DO want to write the serialized model to stdout, not log it
    private void performModelMigration(final Args args) {
        if (args.migrateModel) {
            final PrintWriter printWriter = new PrintWriter(System.out);
            final File inputFile = args.input.getAbsoluteFile();
            final AspectModelUrn aspectModelUrn = fileToUrn(inputFile);

            final MigratorService migratorService = new MigratorService();
            loadButNotResolveModel(args).flatMap(migratorService::updateMetaModelVersion)
                    .forEach(migratedModel -> {
                        new PrettyPrinter(migratedModel, aspectModelUrn, printWriter).print();
                        printWriter.flush();
                        System.exit(0);
                    });
        }
    }

    @SuppressWarnings("squid:S106") // We DO want to write the serialized model to stdout, not log it
    private void performPrettyPrinting(final Args args) {
        if (args.prettyPrint) {
            final PrintWriter printWriter = new PrintWriter(System.out);
            final File inputFile = args.input.getAbsoluteFile();
            final AspectModelUrn aspectModelUrn = fileToUrn(inputFile);
            loadButNotResolveModel(args).forEach(versionedModel -> {
                new PrettyPrinter(versionedModel, aspectModelUrn, printWriter).print();
                printWriter.flush();
                System.exit(0);
            });
        }
    }

    private static void validateInputAndOutputPath(final Args args) {
        if (!args.input.exists() || !args.input.canRead()) {
            fileNotFound(args, null);
        }

        if (!args.outputPath.exists()) {
            args.outputPath.mkdirs();
        } else if (!args.outputPath.isDirectory()) {
            LOG.warn("Output path {} is no directory", args.outputPath.getPath());
            System.exit(1);
        }
    }

    private static void validateCustomPackageName(final Args args) {
        final boolean packageNameGiven = !args.packageName.isEmpty();
        final boolean generatePojos = args.generateAspectModelJavaClasses;
        final boolean generateStaticClasses = args.generateStaticMetaModelJavaClasses;

        if (packageNameGiven && !(generatePojos || generateStaticClasses)) {
            LOG.error("Custom package name can only be set in combination with the option -java or -static-java");
            System.exit(1);
        }
    }

    private static JCommander initializeJCommander(final String[] argv, final Args args) {
        final JCommander jCommander = JCommander.newBuilder()
                .addObject(args)
                .build();

        try {
            jCommander.parse(argv);
        } catch (final ParameterException exception) {
            LOG.debug("Invalid arguments", exception);
            LOG.error("Parsing arguments failed: {}\n", exception.getMessage());
            printHelp(exception.getJCommander());
            System.exit(1);
        }
        return jCommander;
    }

    private static void generateAspectModelJavaClasses(final Args args) {
        final VersionedModel model = loadModelOrFail(args);

        final boolean enableJacksonAnnotations = !args.disableJacksonAnnotations;
        final AspectModelJavaGenerator aspectModelJavaGenerator = args.packageName.isEmpty() ?
                new AspectModelJavaGenerator(model, enableJacksonAnnotations) :
                new AspectModelJavaGenerator(model, args.packageName, enableJacksonAnnotations);

        aspectModelJavaGenerator.generate(artifact -> {
            final String path = artifact.getPackageName();
            final String fileName = artifact.getClassName();
            return getStreamForFile(path.replace('.', File.separatorChar),
                    fileName + ".java", args);
        });
    }

    private static void generateStaticMetaModelJavaClasses(final Args args) {
        final VersionedModel model = loadModelOrFail(args);
        final StaticMetaModelJavaGenerator staticMetaModelJavaGenerator = args.packageName.isEmpty() ?
                new StaticMetaModelJavaGenerator(model) :
                new StaticMetaModelJavaGenerator(model, args.packageName);
        staticMetaModelJavaGenerator.generate(artifact -> {
            final String path = artifact.getPackageName();
            final String fileName = artifact.getClassName();
            return getStreamForFile(path.replace('.', File.separatorChar), fileName + ".java", args);
        });
    }

    private static void generateDiagram(final Args args) throws IOException {
        final VersionedModel model = loadModelOrFail(args);
        final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator(model);
        final Set<AspectModelDiagramGenerator.Format> targetFormats = new HashSet<>();
        if (args.generatePngDiagram) {
            targetFormats.add(AspectModelDiagramGenerator.Format.PNG);
        }
        if (args.generateSvgDiagram) {
            targetFormats.add(AspectModelDiagramGenerator.Format.SVG);
        }
        if (args.generateDotDiagram) {
            targetFormats.add(AspectModelDiagramGenerator.Format.DOT);
        }

        generator.generateDiagrams(targetFormats, name -> getStreamForFile(name, args));
    }

    private static void generateJsonPayload(final Args args) throws IOException {
        final AspectModelJsonPayloadGenerator generator = new AspectModelJsonPayloadGenerator(
                loadModelOrFail(args));
        generator.generateJsonPretty(name -> getStreamForFile(name + ".json", args));
    }

    private static void generateAasAasxSpec(final Args args) throws IOException {
        final AspectModelAASGenerator generator = new AspectModelAASGenerator();
        final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked(loadModelOrFail(args));
        generator.generateAASXFile(aspect, name -> getStreamForFile(name + ".aasx", args));
    }

    private static void generateOpenApiSpecYaml(final Args args) {
        final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator();
        final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked(loadModelOrFail(args));
        try {
            final String yamlSpec = generator
                    .applyForYaml(aspect, args.useSemanticApiVersion, args.aspectApiBaseUrl,
                            Optional.ofNullable(args.aspectResourcePath), getFileAsString(args.aspectParameterFile),
                            args.includeQueryApi, getPagingFromArgs(args));
            final OutputStream out = getStreamForFile(aspect.getName() + ".oai.yaml", args);
            out.write(yamlSpec.getBytes());
            out.flush();
        } catch (final IOException exception) {
            LOG.error("Could not generate OpenApi yaml specification.", exception);
            System.exit(1);
        }
    }

    private static Optional<String> getFileAsString(final String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return Optional.empty();
        }
        final File f = new File(filePath);
        if (f.exists() && !f.isDirectory()) {
            try {
                final InputStream inputStream = new FileInputStream(filePath);
                return Optional.of(IOUtils.toString(inputStream, StandardCharsets.UTF_8.name()));
            } catch (final IOException e) {
                LOG.error(String.format("Could not load file %s.", filePath), e);
                System.exit(1);
            }
        }
        final String errorMsg = String.format("File does not exist %s.", filePath);
        LOG.error(errorMsg);
        System.exit(1);
        return Optional.empty();
    }

    private static void generateOpenApiSpecJson(final Args args) {
        final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator();
        final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked(loadModelOrFail(args));
        JsonNode result = JsonNodeFactory.instance.objectNode();
        final Optional<String> res = getFileAsString(args.aspectParameterFile);
        if (res.isPresent()) {
            try {
                result = objectMapper.readTree(res.get());
            } catch (final JsonProcessingException e) {
                LOG.error("Could not parse the file to JSON.", e);
                System.exit(1);
            }
        }
        final JsonNode jsonSpec = generator
                .applyForJson(aspect, args.useSemanticApiVersion, args.aspectApiBaseUrl,
                        Optional.ofNullable(args.aspectResourcePath), Optional.of(result),
                        args.includeQueryApi, getPagingFromArgs(args));

        final OutputStream out = getStreamForFile(aspect.getName() + ".oai.json", args);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(out, jsonSpec);
            out.flush();
        } catch (final IOException exception) {
            LOG.error("Could not format OpenApi Json.", exception);
            System.exit(1);
        }
    }

    private static Optional<PagingOption> getPagingFromArgs(final Args args) {
        if (args.excludePaging) {
            return Optional.of(PagingOption.NO_PAGING);
        }
        if (args.aspectCursorBasedPaging) {
            return Optional.of(PagingOption.CURSOR_BASED_PAGING);
        }
        if (args.aspectOffsetBasedPaging) {
            return Optional.of(PagingOption.OFFSET_BASED_PAGING);
        }
        if (args.aspectTimeBasedPaging) {
            return Optional.of(PagingOption.TIME_BASED_PAGING);
        }
        return Optional.empty();
    }

    private static void generateJsonSchema(final Args args) {
        final AspectModelJsonSchemaGenerator generator = new AspectModelJsonSchemaGenerator();
        final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked(loadModelOrFail(args));
        final JsonNode schema = generator.apply(aspect);

        final OutputStream out = getStreamForFile(aspect.getName() + ".schema.json", args);
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(out, schema);
            out.flush();
        } catch (final IOException exception) {
            LOG.error("Could not format JSON Schema", exception);
            System.exit(1);
        }
    }

    protected static void generateHtml(final Args args) throws IOException {
        final AspectModelDocumentationGenerator generator = new AspectModelDocumentationGenerator(loadModelOrFail(args));
        final Map<AspectModelDocumentationGenerator.HtmlGenerationOption, String> generationArgs = new HashMap<>();
        generationArgs.put(AspectModelDocumentationGenerator.HtmlGenerationOption.STYLESHEET, "");
        if (args.htmlCustomCSSFile != null) {
            final String css = FileUtils.readFileToString(new File(args.htmlCustomCSSFile), "UTF-8");
            generationArgs.put(AspectModelDocumentationGenerator.HtmlGenerationOption.STYLESHEET, css);
        }
        generator.generate(artifact -> getStreamForFile(artifact, args), generationArgs);
    }
}
