# ESMF tree-sitter-turtle

This module provides a parser for RDF/Turtle, to be used with the [tree-sitter Java binding](https://github.com/bonede/tree-sitter-ng).
First, native C code is generated from the grammar defined in src/main/js/grammar.js using the tree-sitter CLI. This C code is then compiled
to native libraries (.so/.dylib/.dll) for Linux/MacOS/Windows using the [Zig compiler](https://ziglang.org/); since it has built-in
cross-platform compilation capabilities. As the last step, a Java class makes the library available to Java code via JNI.
The Java code that performs the download and execution of Zig is compiled and executed during the build, but is not part of the
final module artifact.

The build works as follows, in chronological order:

| Maven lifecycle phase | Plugin                | Maven Goal | Description |
|-----------------------|-----------------------|------------|-------------|
| initialize            | frontend-maven-plugin | install-node-and-npm | Locally install node and npm |
| initialize            | fronted-maven-plugin  | npm | Use npm to install tree-sitter CLI locally |
| generate-sources      | exec-maven-plugin     | exec | Execute tree-sitter CLI to generate Turtle parser C code |
| generate-sources      | maven-compiler-plugin | compile | Compile DownloadZig and NativeCompile classes (build time code) |
| process-sources       | exec-maven-plugin     | java | Execute DownloadZig class, to download and extract Zig release |
| compile               | maven-compiler-plugin | compile | Compile regular Java binding class (TreeSitterTurtle) |
| compile               | exec-maven-plugin     | java | Execute NativeCompile class, to use Zig compiler to create native libraries from Turtle parser C code |
 
At the end of the build, platform-specific native libs are present in target/classes/libs.
