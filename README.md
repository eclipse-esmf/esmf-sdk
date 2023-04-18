# ESMF SDK

## Table of Contents

- [Introduction](#introduction)
- [Getting help](#getting-help)
- [Build and contribute](#build-and-contribute)
- [SDK Structure](#sdk-structure)
- [Java Core Components](#java-core-components)
    - [esmf-aspect-meta-model](#esmf-aspect-meta-model)
    - [esmf-aspect-static-meta-model](#esmf-aspect-static-meta-model)
    - [esmf-aspect-model-validator](#esmf-aspect-model-validator)
- [Version Handling](#version-handling)
    - [esmf-sdk Versioning](#esmf-sdk-versioning)
    - [SAMM Java Implementation Packaging](#samm-java-implementation-packaging)
- [SAMM CLI](#samm-cli)
- [Artifact Generation facilities](#artifact-generation-facilities)
    - [POJO Generator](#pojo-generator)
    - [Static Meta Model Generator](#static-meta-model-generator)
    - [Aspect Model Diagram Generator](#aspect-model-diagram-generator)
    - [Aspect Model Documentation Generator](#aspect-model-documentation-generator)
- [License](#license)

## Introduction

The ESMF SDK contains artifacts and resources for all parties that intent to use, extend or
integrate with the Semantic Aspect Meta Model (SAMM), e.g., solution developers, domain experts or
OEMs.

At its core are components which help to work with the Semantic Aspect Meta Model. The SDK comprises
components to load and validate models generate artifacts such as static classes, documentation,
DTOs (data transfer objects) and so on.

To fully leverage SAMM, provided components include language-specific meta model implementations,
code generators, validators etc.

This document provides an overall overview of the SDK, and the concepts applied throughout it.
Detailed documentation and concepts for each component can be found in the respective subfolders.

This repository contains a detailed developer documentation written in AsciiDoc. The source files
(AsciiDoc) are located [here](documentation/developer-guide) and are built using
[Antora](https://antora.org/) which generates the documentation as HTML files.

## Getting help

Are you having trouble with ESMF SDK? We want to help!

* Check the [ESMF SDK developer documentation](https://eclipse-esmf.github.io/esmf-developer-guide/index.html)
* Ask a question the [community forum](https://www.eclipse.org/forums/index.php/f/617/).
* Check the SAMM [specification](https://eclipse-esmf.github.io/samm-specification/snapshot/index.html)
* Having issues with the ESMF SDK? Open a [GitHub issue](https://github.com/eclipse-esmf/esmf-sdk/issues).

## Build and contribute

The top level elements of the SDK structure are all carried out as Maven multimodule projects.
Building the SDK requires a Java 17-compatible [GraalVM JDK](https://www.graalvm.org/) with the 'js'
component installed. If you want to build the samm-cli (see below), you will also have to have the
'native-image' GraalVM component installed.

To build the SDK core components, run the following command:
```bash
mvn -pl '!org.eclipse.esmf:samm-cli' clean install
```

To also build SDK core components and the CLI tool, run the above command, followed by:
```bash
cd tools/samm-cli
mvn -B clean verify
mvn -B verify -Pnative
```

We are always looking forward to your contributions. For more details on how to contribute just take
a look at the [contribution guidelines](CONTRIBUTING.md). Please create an issue first before
opening a pull request.

## SDK Structure

To ease navigation through the SDK and its components, the repository employs the following structure:

```
esmf-sdk
 │
 ├─── core                                      # e.g. meta model implementations etc.
 │     ├─── esmf-aspect-meta-model-interface
 │     ├─── esmf-aspect-meta-model-java
 │     ├─── ...
 ├─── tools                                     # accompanying tools, build system integrations etc.
 │     ├─── samm-cli
 │     └─── ...
 └─── documentation                             # the Antora documentation module for the SDK
```

## Java Core Components

The Java core components are those to be consumed by developers that aim to build applications or
tools dealing with SAMM.

### `esmf-aspect-meta-model`

Contains the Java implementation of the SAMM.

An Aspect Model can be used in (primarily) two ways:

* Dynamically instantiated - this is done by tooling and applications that don't have any a-priori
  knowledge except for the Aspect Model file/URN and also don't or can't use generated source code
  artifacts. Any form of Java source code generator will use the meta model this way.
* Statically generated - the preferred way for applications that are built against known Aspect
  Models. A static meta model provides full information about Java types, including types
  (`Entity`s, `Enumeration`s etc.) derived from the Aspect Model itself. Implementation parts
  specific to the static variant are located in their own component `esmf-aspect-static-meta-model`.

### `esmf-aspect-static-meta-model`

Contains the parts of the Java implementation of the SAMM that are specific to the static variant,
e.g. common base classes of static meta model elements. Consumers of a static meta model can use
this component as their main entrance dependency.

### `esmf-aspect-model-validator`

Can be used to validate an Aspect Model against the SAMM definition. This component is mainly of
interest for tool developers or as a utility during a modeling phase.

The model validator is also available through the [SAMM CLI](#samm-cli).

## Version Handling

SAMM and its SDKs evolve over time. While measures are taken to do this in a non-breaking manner,
some changes cannot be carried out without the need to define a new, breaking version. Due to this
fact it is important to understand the versioning concept that is applied to the SAMM, APIs and SDK
components that are derived from them.

In case of a prerelease a postfix will be added to the version, such as `-M1`, and it will be
released on Github. The way to access the artifact is described in the [Java Aspect Tooling
Guide](https://eclipse-esmf.github.io/esmf-developer-guide/tooling-guide/java-aspect-tooling.html#versioning).

### esmf-sdk Versioning

For the esmf-sdk, semantic versioning (`major.minor.micro`) is applied with the following rules:

* The `major` part designates the supported SAMM major version
* A breaking change increases the `minor` part
* Backwards compatible new features, changes to existing features or bugfixes increase the `minor` part

A new SAMM version implies new releases of the SDK components that depend on this SAMM version,
however not the other way round. New releases of SDK components may be released that are built on an
existing SAMM version.

### SAMM Java Implementation Packaging

Complex applications might have the need to be implemented against different versions of the SAMM,
especially if Aspects should be integrated where there is no control over the underlying meta model
version. Thus, Java artifacts for different SAMM versions exist.

Version information encoded into the package name and/or class name solve any issues concerning
unique addressability of a class in a concrete version, however any version update forces consumers
to change the code base if they want to stay on the latest version.

As this is not very convenient, the following concept is applied:

The base package for all meta model elements does not carry any version encoding, neither do any
classes that represent the latest version of the SAMM. This has the advantage that consumers who
want to stay on the latest version and don't need multiple versions of the meta model can simply
upgrade and don't need to apply any changes to their code base (that is, if the newer version did
not introduce any breaking changes).

## Artifact Generation Facilities

One primary use case of a meta model is to use it as a basis for generated artifacts. Most prominent
example is generating domain model classes, however usage is of course by no means restricted to
this.

The ESMF SDK already comes with a set of useful generators. They all accept any Aspect Model given
as a Turtle (`.ttl`) file. All generators are also available through the [SAMM CLI](#samm-cli).

### POJO Generator

The POJO generator takes any Aspect Model and generates all Java classes that are necessary to
represent the Aspect. This includes at least a class for the Aspect itself, in most cases however
multiple files are generated, e.g. for embedded `Entity`s or `Enumeration`s.

### Static Meta Model Generator

Especially when the Aspect Models that are worked with are known a priori (which should be the case
for most solutions built on the ESMF SDK) using the dynamically instantiated meta model does not
give developers the full potential of what's possible. For those use cases a generator can be used
to generate Java source code representing the meta model in a static form, with meta classes that
allow direct access to all important information of one or more Aspect Models, notably properties
and their types.

The static meta model uses the Java implementation from `esmf-aspect-meta-model` adding further
details that are specific to the static case through the component `esmf-aspect-static-meta-model`.

### Aspect Model Diagram Generator

The diagram generator takes any Aspect Model and provides a rendering of a diagram representing the
Aspect Model with all its elements.

Possible output formats are `png`, `svg`  and `dot`.

### Aspect Model Documentation Generator

The documentation generator takes any Aspect Model and provides an HTML document describing the
Aspect Model.

The resulting document can be seen as a reference documentation as it includes any information that
can be derived from the Aspect Model such as the diagram and a list of all properties with
information about their characteristics, data type etc.

### Model migration

All migrators are in the module `esmf-aspect-meta-model-version-migrator` to execute a model
migration. If the internal SDK migrations are not sufficient, it is possible to add custom
migrator(s). For this the interface `MigratorFactory` has to be implemented and added to the
classpath. The AspectResolver collects this automatically and executes the custom migrator depending
on availability. Important: The implementation of the `MigratorFactory` have to be the package name
or a subpackage of `org.eclipse.esmf.aspectmodel.versionupdate`

## SAMM CLI

The [SAMM CLI](tools/samm-cli) provides all generation as well as validation functionality as a CLI
for ad-hoc usage outside an IDE, automated build or any project at all. Provided functions are:

* Model validation
* Model documentation generation
* Model diagram generation (multiple output formats)
* Sample JSON payload generation
* Java class generation
* Static meta model generation

New SDK functionality based on the Aspect Meta Model will always be also provided through the SAMM
CLI. Built binary versions of the SAMM CLI are available as part of the [ESMF SDK releases on
GitHub](https://github.com/eclipse-esmf/esmf-sdk/releases).

### Running

For information regarding running the command line tool, the available commands and their
description, please have a look at the
[documentation](https://eclipse-esmf.github.io/esmf-developer-guide/tooling-guide/samm-cli.html).

## License

SPDX-License-Identifier: MPL-2.0

This program and the accompanying materials are made available under the terms of the
[Mozilla Public License, v. 2.0](LICENSE).

The [Notice file](NOTICE.md) details contained third party materials.
