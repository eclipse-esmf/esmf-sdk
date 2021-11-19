package io.openmanufacturing.sds.metamodel;

import java.util.Collections;
import java.util.List;

/**
 * Represents a generic domain specific concept which may have multiple specific variants.
 *
 * @since BAMM 2.0.0
 */
public interface AbstractEntity extends ComplexType {

   /**
    * @return a {@link java.util.List} of {@link ComplexType}s which extend this Abstract Entity
    */
   default List<ComplexType> getExtendingElements() {
      return Collections.emptyList();
   }

   @Override
   default boolean isAbstractEntity() {
      return true;
   }
}
