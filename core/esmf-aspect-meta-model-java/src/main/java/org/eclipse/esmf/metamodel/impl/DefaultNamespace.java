package org.eclipse.esmf.metamodel.impl;

import java.util.List;

import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultNamespace extends ModelElementImpl implements Namespace {

   public DefaultNamespace( final MetaModelBaseAttributes metaModelBaseAttributes ) {
      super( metaModelBaseAttributes );
   }

   @Override
   public List<Property> getProperties() {
      return null;
   }

   @Override
   public <T, C> T accept( AspectVisitor<T, C> visitor, C context ) {
      return null;
   }
}
