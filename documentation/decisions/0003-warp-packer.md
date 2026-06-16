# Use warp-packer instead of native binaries

* **Supersedes:** [ADR-0002: Native Binaries](0002-native-binaries.md)

## Context and Problem Statement

The original problem statement from [ADR-0002](0002-native-binaries.md) still applies:

> The SDK's command line interface should be usable as convenient to use as possible, i.e., it should
> be usable without requiring a user to have a certain version of Java installed. It should also be
> directly launchable, i.e., by typing its name (without the `java -jar` incantation).

The `samm-cli` module makes use of GraalVM and its native-image compilation to create a native binary.
Although this achieves the original goal of providing a runnable command that does not require users
to install a Java runtime first, it comes with several problems:

* Compilation takes a long time; a full esmf-sdk build takes about ~1h, half of which is due to
  native-image compilation. GraalVM native image compilation takes a lot of memory, which is only
  possible to get on the default GitHub Actions workers by configuring swap space.
* The native-image setup introduces additional classes of failures. In addition to complation
  failures and regular runtime failures, there's a new class of runtime failures that _only_ occur
  in the native binary but not the executable .jar (e.g., when resource configuration was wrong).
  This requires a complex setup to catch. Our solution currently is to separately run all
  integration tests both with the executable .jar and the native binary.
* Creation and maintenance of the native-image configuration (reflection/JNI/resources/etc.) is
  complex and expensive.
    * Just to make aas4j and its rather involved reflection setup work, there's a specific
      [build-time
      only](https://github.com/eclipse-esmf/esmf-sdk/blob/v2.13.1/core/esmf-native-support/src/main/java/org/eclipse/esmf/buildtime/Aas4jClassSetup.java)
      code that reproduces it, creates a .properties file from it, which is included in the build as
      a resource, to be loaded at native initialization time again.
    * Other non-trivial setup specific to third-party dependencies [is
      necessary](https://github.com/eclipse-esmf/esmf-sdk/blob/v2.13.1/core/esmf-native-support/src/main/java/org/eclipse/esmf/nativefeatures).
    * AWT font configuration (transitive requirement for diagram generation) is broken on Windows as
      it requires - hardcoded in JRE - a font config file which exists in the JRE but not in the
      native-image. The
      [hack](https://github.com/eclipse-esmf/esmf-sdk/blob/v2.13.1/core/esmf-native-support/src/main/java/org/eclipse/esmf/nativefeatures/DiagramFeature.java#L403-L407)
      to make it work is to copy the resource from the JRE into the project resources at build time.
    * AWT with native-image on MacOS [remains broken](https://github.com/oracle/graal/issues/4124).
      Since the build does not work, we use a different means for building the samm-cli binary on
      that platform (see also "solution" section below).
* There is no (straightforward) way for extending samm-cli other than at compile time. While using
  dynamic class loading mechanisms would be feasible in plain Java, we don't have this possibility
  with the native image. This might become necessary to introduce an extension mechanism, in
  particular to be used by the upcoming [LSP server](https://github.com/eclipse-esmf/esmf-sdk/issues/955).

## Decision Drivers

* Reduce build time and build complexity.
  * Ideally, the solution works identically on all supported platforms.
* Reduce class of failures that only occur in native binary.
* Keep current benefits:
  * CLI should be runnable without having Java installed.
  * CLI should be directly executable (i.e., without calling `java -jar`).
  * CLI should be a self-contained binary.
* Ideally allow to introduce some kind of runtime extension mechanism.

## Considered Options

* Keep current GraalVM setup
  * Advantages
    * Keep currently supported benefits.
  * Disadvantages
    * See problem statement above.
  * Notes
    * An extension mechanism could in theory be realised by providing native libraries of extensions
      and have the loaded at runtime using `loadLibrary()`. This however requires also requires
      building every extensions for every supported platform.

* Use [warp-packer](https://github.com/kirbylink/warp) to bundle executable .jar with JRE.
  A binary created with warp is self-contained and can extract and launch a Java runtime with the
  application provided as an executable .jar. It caches the extracted parts, so subsequent launches
  have next to no launch time overhead.
  * Advantages
    * No native compilation necessary; creating a regular executable .jar suffices as the basis for
      the binary.
      * Build time and complexity is reduced.
      * Testing setup can be simplified.
    * Can still provide a self-contained binary.
    * Runtime extension is possible using regular .jar loading (e.g., using an approach such as
      [PF4J](https://pf4j.org/) or [OSGi](https://www.osgi.org/)), because at runtime, the
      application is a regular Java process.
    * Contrary to GraalVM, warp can create binaries for platforms other than the currently running
      one.
      * Developers can locally create binaries for other platforms.
      * The GitHub Actions using workers on multiple platforms can be simplified. For example, one
        build running on Linux could create samm-cli binaries for all of Linux, Windows and Mac for
        x86-64 and aarch64.
  * Disadvantages
    * CLI startup is slower compared to native binary.
    * CLI download size is larger.

## Decision Outcome

Chosen option: "Use warp-packer".

