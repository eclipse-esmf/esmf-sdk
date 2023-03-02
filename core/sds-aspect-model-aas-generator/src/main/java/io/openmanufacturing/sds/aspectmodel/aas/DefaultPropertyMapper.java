package io.openmanufacturing.sds.aspectmodel.aas;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

import io.openmanufacturing.sds.metamodel.Type;

/**
 * The default mapper used for all properties.
 */
public class DefaultPropertyMapper implements PropertyMapper<Property> {
   @Override
   public Property mapToAasProperty( Type type, io.openmanufacturing.sds.metamodel.Property property, Context context ) {
      return new DefaultProperty.Builder()
            .idShort( context.getPropertyShortId() )
            .kind( context.getModelingKind() )
            .valueType( mapAASXSDataType( mapType( type ) ) )
            .displayName( LANG_STRING_MAPPER.map( property.getPreferredNames() ) )
            .value( context.getPropertyValue( UNKNOWN_EXAMPLE ) )
            .description( LANG_STRING_MAPPER.map( property.getDescriptions() ) )
            .semanticId( buildReferenceToConceptDescription( property ) )
            .build();
   }

   private String mapType( final Type type ) {
      return type.getUrn();
   }
}
