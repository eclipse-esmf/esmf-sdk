package org.eclipse.esmf.staticmetamodel;

/**
 * Allows to enhance static properties with information about containing type and property type.
 *
 * @param <C> the containing type
 * @param <T> the property type
 */
public interface PropertyTypeInformation<C, T> {
   /**
    * @return the type of the Property represented as a class.
    */
   Class<T> getPropertyType();

   /**
    * @return the type of the class containing the Property, represented as a class
    */
   Class<C> getContainingType();
}
