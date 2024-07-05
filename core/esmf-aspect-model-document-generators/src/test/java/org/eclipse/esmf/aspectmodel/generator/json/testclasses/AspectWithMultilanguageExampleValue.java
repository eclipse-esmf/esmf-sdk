package org.eclipse.esmf.aspectmodel.generator.json.testclasses;

import java.util.Objects;

import org.eclipse.esmf.metamodel.datatype.LangString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * Generated class for AspectWithMultilanguageExampleValue.
 *
 */
public class AspectWithMultilanguageExampleValue {

   @NotNull
   private LangString prop;

   @JsonCreator
   public AspectWithMultilanguageExampleValue( @JsonProperty( value = "prop" ) final LangString prop ) {
      super(

      );
      this.prop = prop;
   }

   /**
    * Returns prop
    *
    * @return {@link #prop}
    */
   public LangString getProp() {
      return prop;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }

      final AspectWithMultilanguageExampleValue that = (AspectWithMultilanguageExampleValue) o;
      return Objects.equals( prop, that.prop );
   }

   @Override
   public int hashCode() {
      return Objects.hash( prop );
   }
}
