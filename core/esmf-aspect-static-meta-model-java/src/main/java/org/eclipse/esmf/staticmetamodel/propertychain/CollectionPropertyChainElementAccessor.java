package org.eclipse.esmf.staticmetamodel.propertychain;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.propertychain.spi.PropertyChainElementAccessor;

/**
 * A {@link PropertyChainElementAccessor} that extracts the next value from a {@link Collection}.
 */
public class CollectionPropertyChainElementAccessor
      implements PropertyChainElementAccessor<Collection<Object>> {
   @Override
   public Class<Collection<Object>> getHandledElementClass() {
      return (Class) Collection.class;
   }

   @Override
   public Object getValue( final Collection<Object> currentValue,
         final StaticProperty<Object, Object> property ) {
      return currentValue.stream().flatMap( v -> {
         final Object nextValue = property.getValue( v );
         if ( nextValue instanceof Collection<?> nextCollection ) {
            return nextCollection.stream();
         }

         return Stream.of( nextValue );
      } ).toList();
   }
}
