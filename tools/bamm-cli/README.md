# BAMM Aspect Meta Model Command Line Tool

## Building

mvn package

## Running

`java -jar ./target/bamm-cli-DEV-SNAPSHOT.jar` _options_

where options are:

```
--aspects-root, -ar
  Root directory to find aspects
--generate-documentation, -html
  Generate HTML documentation for an Aspect Model
  Default: false
--generate-dot-diagram, -dot
  Generate DOT (graphviz) diagram for an Aspect Model
  Default: false
--generate-java-classes, -java
  Generate Java domain classes for an Aspect Model
  Default: false
--generate-json-payload, -json
  Generate sample JSON payload for an Aspect Model
  Default: false
--generate-png-diagram, -png
  Generate PNG diagram for an Aspect Model
  Default: false
--generate-static-java-classes, -static-java
  Generate Java domain classes for an Static Meta Model
  Default: false
--generate-svg-diagram, -svg
  Generate SVG diagram for an Aspect Model
  Default: false
--help, -h
  Display help
--input, -i
  Input file name of the Aspect Model .ttl file
--output-directory, -d
  Output directory to write files to
  Default: .
--package-name, -pn
  Package to use for generated Java classes
  Default: <empty string>
--validate, -v
  Validate input Aspect Model
  Default: false
```

e.g., validate a model:

`java -jar ./target/bamm-cli-DEV-SNAPSHOT.jar -v -i Aspect.ttl`

Generate HTML documentation:

`java -jar ./target/bamm-cli-DEV-SNAPSHOT.jar -html -i Aspect.ttl`

Validate, generate documentation, diagram and JSON sample payload:

`java -jar ./target/bamm-cli-DEV-SNAPSHOT.jar -v -html -svg -json -i Aspect.ttl`
