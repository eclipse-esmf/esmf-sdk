# Native Binaries

## Context and Problem Statement

The SDK's command line interface should be usable as convenient to use as possible, i.e., it should
be usable without requiring a user to have a certain version of Java installed. It should also be
directly launchable, i.e., by typing its name (without the `java -jar` incantation).

## Decision Drivers

* CLI should be runnable without having Java installed.
* CLI should be directly executable (i.e., without calling `java -jar`).
* Beneficial: CLI should respond quickly, in particluar when showing help texts.

Note: A more detailed explanation of those points can be found in the blog post [Building a decent
Java CLI](https://atextor.de/2020/07/27/building-a-decent-java-cli.html).

## Considered Options

* Regular executable .jar with custom launcher script and bundled JRE.
  * Advantages
    * .jar file can work as-is.
    * Flexibility to adjust script.
  * Disadvantages
    * Binary can not be distributed on its own; it will always need a complete folder (containing
      the JRE).
    * Overall startup time: startup time of the .exe + startup time of the JVM.
* [Launch4J](https://launch4j.sourceforge.net/index.html):
  * Remarks
    * Effectively provides a native binary that optionally extracts an executable .jar from itself,
      then lauches the .jar using Java.
  * Advantages
    * .jar file can work as-is.
    * Out-of-the-box solution including Maven plugin.
  * Disadvantages
    * Binary can not be distributed on its own; it will always need a complete folder (containing
      the JRE).
    * Overall startup time: startup time of the .exe + startup time of the JVM.
* [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/basics/): 
  * Remarks
    * Project driven by Oracle to create native binaries from Java applications, including compiler,
      custom runtime (that will be baked into the binary) and accompanying tooling such as source
      annotations.
  * Advantages
    * "Official" project with support present or coming in major frameworks.
    * Proper tooling available (Maven plugin, Github actions, etc.).
    * Can produce standalone binaries.
    * Startup time overhead is removed/reduced.
  * Disadvantages
    * Additional configuration effort for resources, reflection, JNI,...
    
## Decision Outcome

Chosen option: "GraalVM Native Image", because only this option will provide a quickly starting,
self-contained binary.

