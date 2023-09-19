package org.eclipse.esmf.metamodel.loader.instantiator;

import org.apache.jena.rdf.model.Resource;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.impl.DefaultNamespace;
import org.eclipse.esmf.metamodel.loader.Instantiator;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;

public class NamespaceInstantiator extends Instantiator<Namespace> {

   public NamespaceInstantiator( final ModelElementFactory modelElementFactory ) {
      super( modelElementFactory, Namespace.class );
   }

   @Override
   public Namespace apply( final Resource namespace ) {
      final MetaModelBaseAttributes metaModelBaseAttributes = buildBaseAttributes( namespace );
      return new DefaultNamespace( metaModelBaseAttributes);
   }
}
