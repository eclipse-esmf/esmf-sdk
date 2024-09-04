package org.eclipse.esmf.aspectmodel.generator.jsonld;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.generator.AbstractGenerator;
import org.eclipse.esmf.aspectmodel.jackson.AspectModelJacksonModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

public class AspectModelToJsonLdGenerator extends AbstractGenerator {

   private final ObjectMapper objectMapper;

   public AspectModelToJsonLdGenerator() {
      objectMapper = AspectModelToJsonLdGenerator.createObjectMapper();
   }

   private static ObjectMapper createObjectMapper() {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule( new JavaTimeModule() );
      mapper.registerModule( new AspectModelJacksonModule() );
      mapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
      return mapper;
   }

   public void generate() throws JsonLdError, IOException {
      Map<String, Object> context = new HashMap<>();
      context.put( "samm", "urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#" );
      context.put( "samm-c", "urn:samm:org.eclipse.esmf.samm:characteristic:2.1.0#" );
      context.put( "samm-e", "urn:samm:org.eclipse.esmf.samm:entity:2.1.0#" );
      context.put( "unit", "urn:samm:org.eclipse.esmf.samm:unit:2.1.0#" );
      context.put( "xsd", "http://www.w3.org/2001/XMLSchema#" );

      Map<String, Object> movement = new HashMap<>();
      movement.put( "@id", "urn:uuid:123e4567-e89b-12d3-a456-426614174000" );
      movement.put( "@type", "samm:Aspect" );
      movement.put( "samm:preferredName", "movement" );
      movement.put( "samm:description", "Aspect for movement information" );
      movement.put( "isMoving", true );

      Map<String, Object> position = new HashMap<>();
      position.put( "@type", "samm-e:SpatialPosition" );
      position.put( "latitude", 9.1781 );
      position.put( "longitude", 48.80835 );
      position.put( "altitude", 153.0 );
      movement.put( "position", position );

      movement.put( "speed", -2.422416E38 );
      movement.put( "speedLimitWarning", "green" );

      Map<String, Object> jsonld = new HashMap<>();
      jsonld.put( "@context", context );
      jsonld.putAll( movement );

      JsonLdOptions options = new JsonLdOptions();
      Map<String, Object> compacted = JsonLdProcessor.compact( jsonld, context, options );

      String jsonldString = JsonUtils.toPrettyString( compacted );
      System.out.println( jsonldString );
   }
}
