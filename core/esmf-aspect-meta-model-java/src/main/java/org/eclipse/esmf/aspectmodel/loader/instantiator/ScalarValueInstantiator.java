package org.eclipse.esmf.aspectmodel.loader.instantiator;

import org.eclipse.esmf.aspectmodel.loader.Instantiator;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;

import org.apache.jena.rdf.model.Resource;

public class ScalarValueInstantiator extends Instantiator<ScalarValue> {

   public ScalarValueInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, ScalarValue.class );
   }

   @Override
   public ScalarValue apply( final Resource value ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( value );
      return new DefaultScalarValue( metaModelBaseAttributes, value, new DefaultScalar( value.getURI() ) );
   }
}
