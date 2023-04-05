# ESMF Code Conventions
The following document contains a compilation of conventions and guidelines to format, structure and
write code for the ESMF SDK.

## General Conventions
Our code conventions are based on the [Google Java Style
Guide](https://google.github.io/styleguide/javaguide.html) but detailed and adjusted for the needs
of the ESMF SDK.
 
## Copyright header
See [CONTRIBUTING](CONTRIBUTING.md)

## Code Recommendations

### Utility Classes
[Utility classes](https://wiki.c2.com/?UtilityClasses) as such should be avoided - domain concepts
must be expressed in domain classes. Thus, only for "real", non-domain operations not belonging to
any class, utility methods and classes may be used. However, chances are pretty close to 100% that
all of everyday utility needs are already covered by high-quality 3rd-party libraries.

Usually we apply the following rule to decide on the introduction of new libraries
1. Check your framework's and its dependency's utility/static constants classes (e.g. Spring or Vert.x)
2. If not covered, use Guava (https://github.com/google/guava/wiki)
3. If not covered, use Apache Commons (usually .lang module, https://commons.apache.org)
4. If really not covered, write your own (highly unlikely)

### Optional<> usage
The Optional<> type, common for some time in Guava and in the Java core since Version 8, has found
widespread use for return values, however still a lot of discussions emerge concerning a fitting
scope of usage. You may not return null where an Optional<> is expected Whenever an Optional<> is
passed, you may safely assume it to be non-null. So the following snippet must never appear
anywhere:
```
if (someOptional != null && someOptional.isPresent()) ...
```

* Using Optional<> as return types for values that might be missing is always fine
* Using Optional<> for fields is fine (see notes about "Avoid optionality" though)
* Using Optional<> for method parameters is fine (see notes about "Avoid optionality" though)
* Writing if-Statements checking for a present instance (and calling .get() explicitly) is considered an anti-pattern. You should
  1. Use .map() or .ifPresent() functional style patterns
  2. Use .orElse*() methods for clearly defined fallbacks (or exceptions)
* When having collections/streams of Optionals use .filter(Optional::isPresent) accordingly
* Using Optional.ofNullable(someValue).orElseThrow() to create one-liner check/assignment combinations is considered an anti-pattern.
* You should be using Objects.requireNonNull() for those sort of checks (or Guava's Preconditions if you're having more types of assertions than non-null and aim for a maximum of consistency).

### Lombok
We use Lombok in the ESMF SDK. Project Lombok is a Java library that automatically plugs into your
editor and build tools. It removes the burden to write getter or equals methods, adds fully featured
builders to a class with one annotation, automates logging variables, and much more. Consider using
its features if you are in the need to do something like that.

## Documentation

### Source Code Documentation
Public classes and interfaces should carry appropriate JavaDoc explaining the responsibility of the
class. All public methods except getters/setters/toString etc. must be documented as well. Private
methods should be simple enough and well-named such that they don't need documentation. If
appropriate they of course may be documented as well. Inline comments, especially those that merely
separate logical blocks of code, must be avoided as they are usually an indicator that a private
method can be extracted or that bad naming was used that needs explaining.

### Developer Documentation
Developer documentation is put into a README.md placed in the project root. This should contain documentation like:
* Checking out the source code and getting it to run/build
* Mandatory (external system) dependencies and how to set them up (e.g. databases)
* Configuration options and how to apply them
* General important concepts that are relevant to working on the project but are not directly obvious from the source code 
itself. Links to further readings and information, e.g. wiki or other external sources.

### User documentation
User documentation (this includes technical documentation on how to use an application or tool from the SDK) should be on 
its own.
It is written in AsciiDoc, rendered with [Antora](https://antora.org) and the generated static content is
publically hosted for direct user access. 
The source files of the documentation are placed in a subfolder /documentation from the project root.
Documentation is structured so that it can be processed by Antora. This e.g. involves structuring the documentation files
according to [Antora's specification](https://docs.antora.org/antora/2.3/organize-content-files/) and organizing resources
so that Antora [can handle them](https://docs.antora.org/antora/2.3/page/resource-id/).
[AsciiDoc's syntax](https://docs.antora.org/antora/2.3/asciidoc/asciidoc/) is pretty close to Markdown, however it is 
way more targeted towards writing fully fledged documents and with its multitude of backends (HTML, PDF, ...) it is a 
very good source format.
Publishing is realized by means of [Github pages](https://docs.antora.org/antora/2.3/publish-to-github-pages/).

