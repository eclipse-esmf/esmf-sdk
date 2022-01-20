# SDS SDK

## Table of Contents

- [Introduction](#introduction)
- [Getting help](#getting-help)
- [Build and contribute](#build-and-contribute)
- [SDK Structure](#sdk-structure)
- [Java Core Components](#java-core-components)
    - [sds-aspect-meta-model](#sds-aspect-meta-model)
    - [sds-aspect-static-meta-model](#sds-aspect-static-meta-model)
    - [sds-aspect-model-validator](#sds-aspect-model-validator)
- [Version Handling](#version-handling)
    - [BAMM Versioning](#bamm-versioning)
    - [BAMM Java Implementation Packaging](#bamm-java-implementation-packaging)
    - [API Versioning](#api-versioning)
- [BAMM CLI](#bamm-cli)
- [Artifact Generation facilities](#artifact-generation-facilities)
    - [POJO Generator](#pojo-generator)
    - [Static Meta Model Generator](#static-meta-model-generator)
    - [Aspect Model Diagram Generator](#aspect-model-diagram-generator)
    - [Aspect Model Documentation Generator](#aspect-model-documentation-generator)
- [License](#license)

## Introduction

The SDS SDK contains artifacts and resources for all parties that intent to use, extend or integrate with the BAMM
Aspect Meta Model, e.g., Solution Developers, Domain Experts or OEMs.

At its core are components which help to work with the BAMM Aspect Meta Model (BAMM). The SDK comprises components to
load and validate models generate artifacts such as static classes, documentation, DTOs (data transfer objects) and so on.

To fully leverage BAMM, provided components include language-specific meta model implementations, code generators,
validators etc.

This document provides an overall overview of the SDK, and the concepts applied throughout it. Detailed documentation and
concepts for each component can be found in the respective subfolders.

This repository contains a detailed developer documentation written in AsciiDoc. The source files (AsciiDoc) are
located [here](documentation/developer-guide) and are built using
[Antora](https://antora.org/) which generates the documentation as HTML files.
[//](TODO explain how to build the documentation locally)

## Getting help

Are you having trouble with SDS SDK? We want to help!

* Check the [SDS SDK developer documentation](https://openmanufacturingplatform.github.io/sds-documentation/sds-developer-guide/dev-snapshot/index.html)
* Check the BAMM
  specification [specification](https://openmanufacturingplatform.github.io/sds-documentation/bamm-specification/snapshot/index.html)
* Having issues with the SDS SDK? Open a [GitHub issue]( https://github.com/OpenManufacturingPlatform/sds-sdk/issues).

## Build and contribute

The top level elements of the SDK structure are all carried out as Maven multimodule projects. Building the SDK
components of one group (e.g. all core components) then is as easy as running `mvn package` or `mvn install`
in the root directory of the repository.

We are always looking forward to your contributions. For more details on how to contribute just take a look at the
[contribution guidelines](CONTRIBUTING.md). Please create an issue first before opening a pull request.

## SDK Structure

To ease navigation through the SDK and its components, the repository employs the following structure:

```
sds-sdk
 │
 ├─── core                                      # e.g. meta model implementations etc.
 │     ├─── sds-aspect-meta-model
 │     ├─── sds-aspect-static-meta-model
 │     ├─── ...
 └─── tools                                     # accompanying tools, build system integrations etc.
       ├─── bamm-cli
       └─── ...
```

## Java Core Components

The Java core components are those to be consumed by developers that aim to build applications or tools dealing with BAMM.

### `sds-aspect-meta-model`

Contains the Java implementation of the BAMM.

An Aspect Meta Model can be used in (primarily) two ways:

* Dynamically instantiated - this is done by tooling and applications that don't have any a-priori knowledge except for
  the Aspect Model file/URN and also don't or can't use generated source code artifacts. Any form of Java source code
  generator will use the meta model this way.
* Statically generated - the preferred way for applications that are built against known Aspect Models. A static meta
  model provides full information about Java types, including types (`Entity`s, `Enumeration`s etc.) derived from the
  Aspect Model itself. Implementation parts specific to the static variant are located in their own
  component `sds-aspect-static-meta-model`.

### `sds-aspect-static-meta-model`

Contains the parts of the Java implementation of the BAMM that are specific to the static variant, e.g. common base
classes of static meta model elements. Consumers of a static meta model can use this component as their main entrance
dependency.

### `sds-aspect-model-validator`

Can be used to validate an Aspect Model against the BAMM definition. This component is mainly of interest for tool
developers or as a utility during a modeling phase.

The model validator is also available through the [BAMM CLI](#bamm-cli).

## Version Handling

BAMM does evolve over time. While measures are made to do this in a non-breaking
manner, some changes cannot be carried out without the need to define a new, breaking version. Due to this fact it is
important to understand the versioning concept that is applied to the BAMM, APIs and SDK components that are derived
from them.

### BAMM Versioning

For the BAMM, semantic versioning (`major.minor.micro`) is applied with the following rules:

* A breaking change increases the `major` part
* Backwards compatible new features increase the `minor` part
* Changes to existing features or bug fixes increase the `micro` part

A new BAMM version always comprises new releases of the SDK components that depend on the BAMM, however not the other
way round. New releases of SDK components may be crafted that are built on the existing BAMM version.

The SDK component versions are otherwise not tied to the BAMM version, i.e., they may differ in any part of the version.

### BAMM Java Implementation Packaging

Complex applications might have the need to be implemented against different versions of the BAMM, especially if Aspects
should be integrated where there is no control over the underlying meta model version. Thus, Java artifacts for
different BAMM versions exist.

Version information encoded into the package name and/or class name solve any issues concerning unique addressability of
a class in a concrete version, however any version update forces consumers to change the code base if they want to stay
on the latest version.

As this is not very convenient, the following concept is applied:

The base package for all meta model elements does not carry any version encoding, neither do any classes that represent
the latest version of the BAMM. This has the advantage that consumers who want to stay on the latest version and don't
need multiple versions of the meta model can simply upgrade and don't need to apply any changes to their code base (that
is, if the newer version did not introduce any breaking changes).

## Artifact Generation Facilities

One primary use case of a meta model is to use it as a basis for generated artifacts. Most prominent example is
generating domain model classes, however usage is of course by no means restricted to this.

The SDS SDK already comes with a set of useful generators. They all accept any Aspect Model given as a Turtle (`.ttl`)
file. All generators are also available through the [BAMM CLI](#bamm-cli).

### POJO Generator

The POJO generator takes any Aspect Model and generates all Java classes that are necessary to represent the Aspect.
This includes at least a class for the Aspect itself, in most cases however multiple files are generated, e.g. for
embedded `Entity`s or `Enumeration`s.

### Static Meta Model Generator

Especially when the Aspect Models that are worked with are known a priori (which should be the case for most solutions
built on the SDS SDK) using the dynamically instantiated meta model does not give developers the full potential of
what's possible. For those use cases a generator can be used to generate Java source code representing the meta model in
a static form, with meta classes that allow direct access to all important information of one or more Aspect Models,
notably properties and their types.

The static meta model uses the Java implementation from `sds-aspect-meta-model` adding further details that are specific
to the static case through the component `sds-aspect-static-meta-model`.

### Aspect Model Diagram Generator

The diagram generator takes any Aspect Model and provides a rendering of a diagram representing the Aspect Model with
all its elements.

Possible output formats are `png`, `svg`  and `dot`.

### Aspect Model Documentation Generator

The documentation generator takes any Aspect Model and provides an HTML document describing the Aspect Model.

The resulting document can be seen as a reference documentation as it includes any information that can be derived from
the Aspect Model such as the diagram and a list of all properties with information about their characteristics, data
type etc.

### Model migration

All migrators are in the module `sds-aspect-meta-model-version-migrator` to execute a model migration. If the internal
SDS migrations are not sufficient, it is possible to add custom migrator(s). For this the interface `MigratorFactory`
has to be implemented and added to the classpath. The AspectResolver collects this automatically and executes the custom
migrator depending on availability. Important: The implementation of the `MigratorFactory` have to be the package name
or a subpackage of  `io.openmanufacturing.sds.aspectmodel.versionupdate`

## BAMM CLI

The [BAMM CLI](tools/bamm-cli) provides all generation as well as validation functionality as a CLI for ad-hoc usage outside an IDE,
automated build or any project at all. Provided functions are:

* Model validation
* Model documentation generation
* Model diagram generation (multiple output formats)
* Sample JSON payload generation
* Java class generation
* Static meta model generation

New SDK functionality based on the Aspect Meta Model will always be also provided through the BAMM CLI.
Build binary versions of the BAMM CLI are available as part of the [BAMM SDK releases in GitHub](https://github.com/OpenManufacturingPlatform/sds-sdk/releases).

## License

SPDX-License-Identifier: MPL-2.0

This program and the accompanying materials are made available under the terms of the
[Mozilla Public License, v. 2.0](LICENSE).

The [Notice file](NOTICE.md) details contained third party materials.
