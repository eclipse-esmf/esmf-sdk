# ESMF Aspect Model Java Generator

This project provides functionality to generate Java domain classes from aspect models.

## How it works

An aspect model is used to generate the output. 
The basic steps are: 

1. The aspect model definition file (`.ttl`) has to be loaded using the `AspectModelLoader` from the `esmf-aspect-meta-model-java` project
2. This instantiated aspect meta model is passed to an `AspectModelJavaGenerator` for the actual generation step
3. The generator writes files (usually multiple) for the different elements of a model (e.g. entities, enumerations, ...)
