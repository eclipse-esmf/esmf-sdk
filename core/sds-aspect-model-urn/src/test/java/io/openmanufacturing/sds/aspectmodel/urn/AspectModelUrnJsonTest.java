/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship. 
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package io.openmanufacturing.sds.aspectmodel.urn;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

public class AspectModelUrnJsonTest {
   private static final ObjectMapper MAPPER = new ObjectMapper();

   private static final TestModel TEST_MODEL = new TestModel( "test",
         AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing:aspect-model:Aspect:1.0.0" ) );

   private static final String TEST_MODEL_JSON = "{\"description\":\"test\",\"urn\":\"urn:bamm:io.openmanufacturing:aspect-model:Aspect:1.0.0\"}";

   @Test
   public void serializeToJson() throws JsonProcessingException {
      assertThat( MAPPER.writeValueAsString( TEST_MODEL ) )
            .isEqualTo( TEST_MODEL_JSON );
   }

   @Test
   public void deserializeFromJson() throws IOException {
      assertThat( MAPPER.readValue( TEST_MODEL_JSON, TestModel.class ) ).isEqualTo( TEST_MODEL );
   }

   private static class TestModel {
      public String description;

      public AspectModelUrn urn;

      public TestModel( @JsonProperty( "description" ) final String description, @JsonProperty( "urn" )
      final AspectModelUrn urn ) {
         this.description = description;
         this.urn = urn;
      }

      @Override
      public boolean equals( final Object o ) {
         if ( this == o ) {
            return true;
         }
         if ( o == null || getClass() != o.getClass() ) {
            return false;
         }
         final TestModel testModel = (TestModel) o;
         return Objects.equals( description, testModel.description ) &&
               Objects.equals( urn.getUrn(), testModel.urn.getUrn() );
      }

      @Override
      public int hashCode() {
         return Objects.hash( description, urn.getUrn() );
      }
   }
}
