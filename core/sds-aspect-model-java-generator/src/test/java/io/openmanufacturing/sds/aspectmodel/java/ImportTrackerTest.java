package io.openmanufacturing.sds.aspectmodel.java;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ImportTrackerTest {
   @Test
   void testType() {
      var importTracker = new ImportTracker();
      var type = "java.lang.Long";
      importTracker.trackPotentiallyParameterizedType( type );
      assertThat( importTracker.getUsedImports() ).containsExactlyInAnyOrder( "java.lang.Long" );
   }

   @Test
   void testGenericType() {
      var importTracker = new ImportTracker();
      var type = "java.util.LinkedHashSet<java.lang.Long>>";
      importTracker.trackPotentiallyParameterizedType( type );
      assertThat( importTracker.getUsedImports() ).containsExactlyInAnyOrder( "java.lang.Long", "java.util.LinkedHashSet" );
   }

   @Test
   void testOptionalGenericType() {
      var importTracker = new ImportTracker();
      var type = "java.util.Optional<java.util.LinkedHashSet<java.lang.Long>>";
      importTracker.trackPotentiallyParameterizedType( type );
      assertThat( importTracker.getUsedImports() ).containsExactlyInAnyOrder( "java.util.Optional", "java.util.LinkedHashSet", "java.lang.Long" );
   }

   @Test
   void testDoubleGenericType() {
      var importTracker = new ImportTracker();
      var type = "test.importTracker.StaticContainerProperty<test.importTracker.TestEntity,java.util.Optional<test.importTracker.TestEntity>>";
      importTracker.trackPotentiallyParameterizedType( type );
      assertThat( importTracker.getUsedImports() ).containsExactlyInAnyOrder( "test.importTracker.StaticContainerProperty", "test.importTracker.TestEntity",
            "java.util.Optional" );
   }
}
