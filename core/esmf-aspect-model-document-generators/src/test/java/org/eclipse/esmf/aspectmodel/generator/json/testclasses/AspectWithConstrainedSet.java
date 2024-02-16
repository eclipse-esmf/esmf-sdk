package org.eclipse.esmf.aspectmodel.generator.json.testclasses;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Generated class for Test Aspect. This is a test description
 */
public class AspectWithConstrainedSet {

   @NotNull
   @Size( min = 1 )

   private Set<String> testProperty;

   @JsonCreator
   public AspectWithConstrainedSet( @JsonProperty( value = "testProperty" ) final Set<String> testProperty ) {
      super(

      );
      this.testProperty = testProperty;
   }

   /**
    * Returns Test Property
    *
    * @return {@link #testProperty}
    */
   public Set<String> getTestProperty() {
      return testProperty;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }

      final AspectWithConstrainedSet that = (AspectWithConstrainedSet) o;
      return Objects.equals( testProperty, that.testProperty );
   }

   @Override
   public int hashCode() {
      return Objects.hash( testProperty );
   }
}
