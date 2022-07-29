package io.openmanufacturing.sds.test;

import org.apache.commons.text.CaseUtils;

public enum TestSharedAspect implements TestSharedModel {
   ASPECT_WITH_COLLECTION_ENTITY,
   ASPECT_WITH_CONSTRAINT_ENTITY,
   ASPECT_WITH_EITHER_ENTITY,
   ASPECT_WITH_EXTENDED_ENTITY;

   @Override
   public String getName() {
      return CaseUtils.toCamelCase( toString().toLowerCase(), true, '_' );
   }
}
