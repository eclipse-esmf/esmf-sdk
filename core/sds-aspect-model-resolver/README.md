# Aspect Model Resolver

The Aspect Model Resolver provides functionality to resolve an Aspect Model URN.

The following strategies are provides in order to resolve Aspect Models
* ```FileSystemStrategy``` resolves an Aspect Model for a given file path in the local file system
* ```EitherStrategy``` resolves an Aspect Model by trying two other strategies
* ```ClasspathStrategy``` resolves an Aspect Model for a given Aspect Model URN
  from the class path

## Aspect Model folder structure
The ```AspectModelResolver``` assumes the following directory structure when resolving Aspect Models residing in files in the local file system:
```/${directory-name}/${element-version}/${element-name}.ttl```

Where

* ```${directory-name}``` is any directory. This directory must be given to the ```ClasspathStrategy``` and the ```FileSystemStrategy``` as the ```modelsRoot```.
* ```${element-version}``` the version of the Aspect Model element defined in the given Turtle file, e.g. ```1.0.0```
* ```${element-name}``` the name of the Aspect Model element defined in the given Turtle file, e.g. ```Errors```

## Using the ```AspectModelResolver```

The following examples assumes an Aspect model in the given directory structure:
```/foo/1.0.0/Test.ttl```.

To obtain the resolved raw RDF model:

```java


AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing:1.0.0#Test" );
      Path modelsRoot = Paths.get("/foo");
      AspectModelResolver resolver = new AspectModelResolver();

      resolver.resolveAspectModel( new FileSystemStrategy( modelsRoot), aspectModelUrn ).forEach( versionedModel -> {
      // Get the RDF model
      Model model = versionedModel.getModel();

      // Get the meta model version used in the Aspect
      KnownVersion version = versionedModel.getVersion();

      // ...
      });
```

To obtain an instance of the Java representation of an Aspect model
(a ```org.eclipse.esmf.metamodel.Aspect```):

```java


import loader.org.eclipse.esmf.metamodel.AspectModelLoader;

AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing:1.0.0#Test" );
      Path modelsRoot = Paths.get("/foo");
      AspectModelResolver resolver = new AspectModelResolver();

      resolver.resolveAspectModel( new FileSystemStrategy(modelsRoot), aspectModelUrn )
      .flatMap( AspectModelLoader::fromVersionedModel )
      .onFailure( throwable -> { /* something went wrong while loading the model */ }
      .forEach(aspect -> {
      // Get the meta model version used in the Aspect
      KnownVersion version = aspect.getMetaModelVersion();

      // ... e.g. aspect.getProperties().stream().forEach( ... )
      });
```
