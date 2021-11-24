# BAMM Aspect Meta Model Command Line Interface

The BAMM CLI bundles the functionality provided in the SDS SDK in a CLI application to be used in you terminal for ad-hoc usage outside an IDE.
For details on the usage and offered functionalities see [Running](#Running) 

## Download

You can find build versions of the BAMM CLI under the [GitHub releases of the SDS SDK](https://github.com/OpenManufacturingPlatform/sds-sdk/releases).

### Building

Alternatively, you can build the BAMM CLI on your local machine using: 

`mvn package`

## Running

`java -jar bamm-cli-<version>.jar -i <name-space>/<version-major>.<version-minor>.<version-patch>/<aspect.ttl> [options]`

where options are:

```
    --aspect-api-base-url, -base-url
      The base url for the Aspect API used in the OpenAPI specification.
      Default: <empty string>
    --aspect-cursor-based-paging, -acbp
      In case there is more than one paging possibility, it has to be cursor 
      based paging.
      Default: false
    --aspect-exclude-paging, -no-paging
      Exclude paging information for the Aspect API Endpoint in the OpenAPI 
      specification. 
      Default: false
    --aspect-offset-based-paging, -aobp
      In case there is more than one paging possibility, it has to be offset 
      based paging.
      Default: false
    --aspect-parameter-file, -apf
      The path to a file including the parameter for the Aspect API endpoints.
    --aspect-resource-path, -arp
      The resource path for the Aspect API endpoints.
    --aspect-time-based-paging, -atbp
      In case there is more than one paging possibility, it has to be time 
      based paging.
      Default: false
    --disable-jackson-annotations, -dja
      Disable Jackson annotation generation in generated Java classes
      Default: false
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
    --generate-json-schema, -schema
      Generate JSON schema for an Aspect Model
      Default: false
    --generate-openapi-json, -oapi-json
      Generate OpenAPI JSON specification for an Aspect Model
      Default: false
    --generate-openapi-yaml, -oapi-yaml
      Generate OpenAPI YAML specification for an Aspect Model
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
    --html-custom-css-file, -hccf
      CSS file with custom styles to be included in the generated HTML 
      documentation 
    --include-aspect-query-api, -query-api
      Include the path for the Query Aspect API Endpoint in the OpenAPI 
      specification. 
      Default: false
  * --input, -i
      Input file name of the Aspect Model .ttl file
    --migrate, -m
      Migrate Aspect Model to latest Meta Model Version
      Default: false
    --output-directory, -d
      Output directory to write files to
      Default: .
    --package-name, -pn
      Package to use for generated Java classes
      Default: <empty string>
    --pretty-print, -p
      Pretty print (format) Aspect Model
      Default: false
    --use-semantic-api-version, -sv
      Use the full semantic version from the Aspect Model as the version for 
      the Aspect API.
      Default: false
    --validate, -v
      Validate input Aspect Model
      Default: false
```

e.g., validate a model:

`java -jar bamm-cli-DEV-SNAPSHOT.jar -i mynamespace/1.0.0/Aspect.ttl -v`

Generate HTML documentation:

`java -jar bamm-cli-DEV-SNAPSHOT.jar -i mynamespace/1.0.0/Aspect.ttl -html`

Validate, generate documentation, diagram and JSON sample payload:

`java -jar bamm-cli-DEV-SNAPSHOT.jar -i mynamespace/1.0.0/Aspect.ttl -html -svg -json`