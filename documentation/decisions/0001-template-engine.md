# Template Engine

## Context and Problem Statement

Various modules in the SDK generate code and other artifacts, such as documentation, from Aspect
Models. For this, a suitable template engine is required that can be embedded in the generation
APIs.

## Decision Drivers

* Sufficient expressiveness for the task at hand.
* Good Java integration, since the engine is to be embedded in the Java code base.
* Beneficial: Good tool support (editing from editors/IDEs, in particular Intellij IDEA).

## Considered Options

* [Xtend](https://eclipse.dev/Xtext/xtend/): Language development framework on the Eclipse Platform;
  works like a cross between the Java language with smaller syntax and a template engine. Xtend
  works by cross-compiling Xtend source code to Java source code.
* [Apache Velocity](https://velocity.apache.org/): Java-based template engine. IntelliJ IDEA
  integration exists (syntax highlighting, auto complete, etc.). Templates are defined in VTL
  (Velocity Template Language).
* [Apache Freemarker](https://freemarker.apache.org/): Template engine with Java API integration,
  with an XML-like syntax. Templates are defined in FTL (Freemarker Template Language). No IntelliJ
  IDEA plugin seems to exist.

## Decision Outcome

Chosen option: "Apache Velocity", because it is a mature project. In terms of features, Velocity and
Freemarker are very similar; the only notable difference for our daily work seems to be the
availability of an IntelliJ IDEA plugin to work with the templates. Xtend is difficult to use for
multiple reasons: Firstly, IDE support only exists for the Eclipse IDE, secondly, errors in the
template result in stack traces that refer to the lines of the Java source code that was generated
from the template, which makes debugging very difficult. Lastly, the cross-compilation approach on
top of regular Java compilation makes building notably slower and evaluation of template snippets
from (e.g., in unit tests) impossible.

### Consequences

* Good, because Velocity is a robust library with good IDE support.
* Bad, because there seems to be not much syntax support for FTL in other editors.

