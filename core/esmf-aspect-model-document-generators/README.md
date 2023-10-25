# ESMF Aspect Model Document Generator

This project provides functionality to generate a diagram, HTML page and a JSON payload from an
Aspect Model.

## Diagram Generator

The diagram generator can generate an image in SVG or PNG format.

### How it works

1. The Aspect Model file (.ttl) is loaded
2. The Aspect Java model is instantiated with the contents of the loaded Aspect Model
3. A graph representation of the model is built
4. The graph representation is rendered into SVG
5. The used font is embedded as a Base64 encoded string
6. If necessary, the adjusted SVG is rendered into a PNG
7. The result is written to the specified location

## Document Generator

The document generator creates an HTML page for an Aspect Model. The page includes the diagram for
the Aspect Model. The HTML page embedding all required styles, fonts and images for making the HTML
page to run standalone without dependencies to the internet.

### How it works

For the generation the Aspect Java model is used.

1. The Aspect Model file (.ttl) is loaded
2. The Aspect Java model is instantiated with the contents of the loaded Aspect Model
3. The instantiated Aspect Model is used to generate the HTML page using Velocity templates
4. The diagram for the Aspect Model is generated in SVG format and embedded into the HTML as a
   Base64 encoded string
5. The required JavaScript, styles and font are embedded in the HTML
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

1. The Aspect Model file (.ttl) is loaded
2. The Aspect Java model is instantiated with the contents of the loaded Aspect Model
3. The instantiated Aspect Model is used to generate the JSON payload by iterating the
   elements of the Aspect Model and extracting the given example values for the properties. If
   no example value is given a random value with the correct type is generated
4. The generated JSON is written to the specified location
