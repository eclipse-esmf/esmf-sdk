# SDS Aspect Model Java Generator

This project provides functionality to generate Java domain classes from aspect models.

## How it works

An aspect model is used to generate the output. 
The basic steps are: 

1. The aspect model definition file (`.ttl`) has to be loaded using the `AspectModelLoader` from the `sds-aspect-meta-model-java` project
2. This instantiated aspect meta model is passed to an `AspectModelJavaGenerator` for the actual generation step
3. The generator writes files (usually multiple) for the different elements of a model (e.g. entities, enumerations, ...)
using Xtend templates 

## How to develop
### IntelliJ IDEA setup
In order for the IDE to find the classes generated from Xtend:
- build the project/module once with maven (`compile` is sufficient)
- mark the 'xtend-gen' folder as a 'Generated Sources Root'

In general, when changes are made to the templates, running maven `compile` on the `sds-aspect-model-java-generator` project is
required.

### Extending the generator
Each type of generated compilation unit has its own template. When changes have to be made on existing supported features,
these templates need to be adjusted:

- `AspectModelJavaTemplate.xtend` - the template for the class of the aspect itself
- `EntityJavaTemplate.xtend` - generates separate classes for `Entity`s defined in the aspect model
- `EnumerationModelJavaTemplate.xtend` - generates separate enum classes for `Enumeration`s defined in the aspect model
- Common convenience functionality (especially for type handling) can be found in `AspectModelJavaUtil.xtend` 

As long as the existing instantiated meta model is sufficient for the changes made, these are the only places that need
to be touched. If further data or meta model elements however are necessary, the `sds-aspect-meta-model-java` project has to
be extended as well.

For editing the Xtend templates it is recommended to use Eclipse, as the Xtend support (through a plugin) for IntelliJ IDEA
is rather weak.

### Debugging
Xtend generates vanilla Java files in `xtend-gen`. Breakpoints can be set as in any other source file when debugging the
generator.
