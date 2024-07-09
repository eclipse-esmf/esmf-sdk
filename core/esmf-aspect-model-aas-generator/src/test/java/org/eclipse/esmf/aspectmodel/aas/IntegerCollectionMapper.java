package org.eclipse.esmf.aspectmodel.aas;

import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.eclipse.digitaltwin.aas4j.v3.model.AasSubmodelElements;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;

public class IntegerCollectionMapper implements PropertyMapper<SubmodelElementList> {
   @Override
   public SubmodelElementList mapToAasProperty( final Type type, final Property property, final Context context ) {
      final List<? extends SubmodelElement> values = context.getRawPropertyValue()
            .stream()
            .filter( JsonNode::isArray )
            .map( ArrayNode.class::cast )
            .flatMap( arrayNode -> StreamSupport.stream( arrayNode.spliterator(), false )
                  .map( value -> new DefaultProperty.Builder().idShort( "intValue" )
                        .valueType( DataTypeDefXsd.INT )
                        .value( value.asText() )
                        .build() ) )
            .toList();

      return new DefaultSubmodelElementList.Builder()
            .idShort( property.getName() )
            .displayName( LangStringMapper.NAME.map( property.getPreferredNames() ) )
            .description( LangStringMapper.TEXT.map( property.getDescriptions() ) )
            .value( (List<SubmodelElement>) values )
            .typeValueListElement( AasSubmodelElements.SUBMODEL_ELEMENT )
            .build();
   }

   @Override
   public boolean canHandle( final Property property ) {
      return property.urn().equals( AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#testList" ) );
   }

   @Override
   public int getOrder() {
      return 0;
   }
}
