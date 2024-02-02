package org.eclipse.esmf.staticmetamodel.propertychain;

import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.propertychain.spi.PropertyChainElementAccessor;

/**
 * The default {@link PropertyChainElementAccessor} that simply extracts the next value from the property as-is, assuming the current value
 * is an entity (i.e. not the end of the chain).
 */
public class DefaultPropertyChainElementAccessor implements PropertyChainElementAccessor<Object> {
   @Override
   public Class<Object> getHandledElementClass() {
      return Object.class;
   }

   @Override
   public Object getValue( final Object currentValue, final StaticProperty<Object, Object> property ) {
      return property.getValue( currentValue );
   }
}
