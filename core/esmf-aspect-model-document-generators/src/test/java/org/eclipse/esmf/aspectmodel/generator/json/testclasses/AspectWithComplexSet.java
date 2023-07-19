package org.eclipse.esmf.aspectmodel.generator.json.testclasses;

import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generated class for Test Aspect. This is a test description
 */
public class AspectWithComplexSet {

   @NotNull
   @Size( min = 2 )

   private Set<Id> testProperty;

   @JsonCreator
   public AspectWithComplexSet( @JsonProperty( value = "testProperty" ) final Set<Id> testProperty ) {
      super(

      );
      this.testProperty = testProperty;
   }

   /**
    * Returns Test Property
    *
    * @return {@link #testProperty}
    */
   public Set<Id> getTestProperty() {
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

      final AspectWithComplexSet that = (AspectWithComplexSet) o;
      return Objects.equals( testProperty, that.testProperty );
   }

   @Override
   public int hashCode() {
      return Objects.hash( testProperty );
   }
}
