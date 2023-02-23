# ESMF Aspect Model Document Generator

This project provides functionality to generate a diagram, HTML page and a JSON payload from an
Aspect Model.

## Diagram Generator

The diagram generator can generate an image in SVG, PNG or DOT format.

The `boxmodel.ttl` file contains the definition of an intermediate format which describes the boxes
in the diagram as well as the edges between these boxes. For each SAMM element a SPARQL construct
query resides in its own file, see the `*2boxmodel.sparql` files, which creates a box or edge for
the SAMM element. Finally the `boxmodel2dot.sparql` construct query creates DOT statements from the
boxes created for an Aspect Model. These statements are then added to the `aspect2dot.mustache`
template to create the final digraph required by Graphviz. Graphviz is then used to render an image
in the specified format.

### How it works

1. For each specified language the `Aspect to Box Model` files (.sparql) are loaded
2. The resulting query is executed against the loaded Aspect Model to create box for each Aspect
   Model element as well as the connections between them
3. The `Box Model to DOT` query is executed against the resulting Model containing these boxes
4. The final digraph is rendered using the `Aspect to DOT` mustache template applied to the
   resulting DOT string
5. If the requested format was DOT, the result is directly written to the specified location
6. For the SVG and PNG formats the result is passed to the Graphviz engine to be rendered in the
   corresponding format and written to the specified location
For more information on SPARQL construct queries, Graphviz and Mustache see:
- [SPARQL Construct](https://www.w3.org/TR/rdf-sparql-query/#construct)
- [Graphviz](https://www.graphviz.org/)
- [Mustache](https://mustache.github.io/)

## Document Generator

The document generator creates an HTML page for an Aspect Model. The page includes the diagram for
the Aspect Model. The HTML page embedding all required styles, fonts and images for making the HTML
page to run standalone without dependencies to the internet.

### How it works

For the generation the aspect java meta model is used.

1. The Aspect Model definition file (.ttl) is loaded
2. The aspect meta model is instantiated with the contents of the loaded aspect meta model
   definition
3. The instantiated aspect meta model is used to generate the HTML page using Velocity templates
4. The diagram for the Aspect Model is generated in SVG format and embedded into the HTML as a
   Base64 encoded String
5. The required JavaScript, styles and font will be embedded into the HTML as a String
6. The HTML is written to the specified location

The HTML page is generated for each language which is present in the Aspect Model.

### Minimize Tailwind for production

The HTML page layout is based on Tailwind CSS. To minimize the general CSS provided via
[CDN](https://unpkg.com/tailwindcss@^2/dist/tailwind.min.css) using PurgeCSS (see [Build for
production](https://tailwindcss.com/docs/installation#building-for-production)).

#### How it works

Precondition: PurgeCSS is installed following the
[documentation](https://purgecss.com/CLI.html#installation).

1. Generate with the CLI an HTML page first
2. Update the purgecss.config.js and enter the path to the generated HTML page
3. Run PurgeCSS 'purgecss -c purgecss.config.js'

For more information on Velocity see:
- [Velocity](https://velocity.apache.org/engine/1.7/user-guide.html)
- [PurgeCSS](https://purgecss.com)
- [Tailwind](https://tailwindcss.com/docs)

## JSON Generator

The JSON generator creates a sample JSON payload for an Aspect Model.

### How it works

For the generation the Aspect Java model is used.

 1. The Aspect Model definition file (.ttl) is loaded
 2. The aspect meta model is instantiated with the contents of the loaded aspect meta model
    definition
 3. The instantiated aspect meta model is used to generate the JSON payload by iterating the
    elements of the aspect meta model and extracting the given example values for the properties. If
    no example value is given a random value is generated
 4. The generated JSON is written to the specified location

