package org.eclipse.esmf.aspectmodel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.asyncapi.AspectModelAsyncApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaArtifact;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.vavr.control.Try;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateAsyncApiSpec", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateAsyncApiSpec extends AspectModelMojo {
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final ObjectMapper YAML_MAPPER = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );
   private static final Logger LOG = LoggerFactory.getLogger( GenerateAsyncApiSpec.class );

   private final AspectModelAsyncApiGenerator generator = new AspectModelAsyncApiGenerator();

   @Parameter( required = true )
   private String outputFormat = "";

   @Parameter( defaultValue = "false" )
   private boolean separateFiles;

   @Parameter
   private String applicationId;

   @Parameter
   private String channelAddress;

   @Parameter( defaultValue = "false" )
   private boolean useSemanticApiVersion;

   @Parameter( defaultValue = "en" )
   private String language;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      final Set<Aspect> aspects = loadAspects();
      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );
      final ApiFormat format = Try.of( () -> ApiFormat.valueOf( outputFormat.toUpperCase() ) )
            .getOrElseThrow( () -> new MojoExecutionException( "Invalid output format." ) );
      for ( final Aspect aspect : aspects ) {
         final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
               .useSemanticVersion( useSemanticApiVersion )
               .applicationId( applicationId )
               .channelAddress( channelAddress )
               .locale( locale )
               .build();

         final AsyncApiSchemaArtifact asyncApiSpec = generator.apply( aspect, config );
         try {
            if ( separateFiles ) {
               writeSchemaWithSeparateFiles( format, asyncApiSpec );
            } else {
               writeSchemaWithInOneFile( aspect.getName() + ".aai." + format.toString().toLowerCase(), format, asyncApiSpec );
            }
         } catch ( final IOException exception ) {
            throw new MojoExecutionException( "Could not generate AsyncAPI specification.", exception );
         }
      }
   }

   private void writeSchemaWithInOneFile( final String schemaFileName, final ApiFormat format, final AsyncApiSchemaArtifact asyncApiSpec )
         throws IOException {
      try ( final OutputStream out = getOutputStreamForFile( schemaFileName, outputDirectory ) ) {
         if ( format == ApiFormat.JSON ) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, asyncApiSpec.getContent() );
         } else {
            out.write( jsonToYaml( asyncApiSpec.getContent() ).getBytes( StandardCharsets.UTF_8 ) );
         }
      }
   }

   private void writeSchemaWithSeparateFiles( final ApiFormat format, final AsyncApiSchemaArtifact asyncApiSpec ) throws IOException {
      final Path root = Path.of( outputDirectory );
      if ( format == ApiFormat.JSON ) {
         final Map<Path, JsonNode> separateFilesContent = asyncApiSpec.getContentWithSeparateSchemasAsJson();
         for ( final Map.Entry<Path, JsonNode> entry : separateFilesContent.entrySet() ) {
            try ( final OutputStream out = new FileOutputStream( root.resolve( entry.getKey() ).toFile() ) ) {
               OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue( out, entry.getValue() );
            }
         }
      } else {
         final Map<Path, String> separateFilesContentAsYaml = asyncApiSpec.getContentWithSeparateSchemasAsYaml();
         for ( final Map.Entry<Path, String> entry : separateFilesContentAsYaml.entrySet() ) {
            try ( final OutputStream out = new FileOutputStream( root.resolve( entry.getKey() ).toFile() ) ) {
               out.write( entry.getValue().getBytes( StandardCharsets.UTF_8 ) );
            }
         }
      }
   }

   private String jsonToYaml( final JsonNode json ) {
      try {
         return YAML_MAPPER.writeValueAsString( json );
      } catch ( final JsonProcessingException exception ) {
         LOG.error( "JSON could not be converted to YAML", exception );
         return json.toString();
      }
   }
}
