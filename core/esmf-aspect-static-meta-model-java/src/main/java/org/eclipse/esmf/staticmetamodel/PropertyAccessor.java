package org.eclipse.esmf.staticmetamodel;

/**
 * Interface for any function that provides a property value access.
 *
 * @param <C> the type containing the property
 * @param <T> the type of the property
 * @see StaticProperty
 */
@FunctionalInterface
public interface PropertyAccessor<C, T> {

   /**
    * Performs the property access and returns its value.
    *
    * @param object the instance containing the property
    * @return the property value of the given instance.
    */
   T getValue( C object );
}
