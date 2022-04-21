package io.openmanufacturing.sds.metamodel;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines the data type of a {@link Characteristic} as being a complex value.
 */
public interface ComplexType extends Type, StructureElement {

   default boolean isAbstractEntity() {
      return false;
   }

   /**
    * @return all {@link Property}s defined in the context of this Complex Type as well as all extended Complex Types
    */
   default List<Property> getAllProperties() {
      if ( getExtends().isPresent() ) {
         return Stream.of( getProperties(), getExtends().get().getProperties() ).flatMap( Collection::stream ).collect( Collectors.toList() );
      }
      return List.copyOf( getProperties() );
   }

   @Override
   default String getUrn() {
      return getAspectModelUrn().get().toString();
   }

   /**
    * @return the {@link ComplexType} that is extended by this Complex Type, if present
    */
   default Optional<ComplexType> getExtends() {
      return Optional.empty();
   }
}
