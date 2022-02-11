/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.metamodel.impl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.EntityInstance;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Value;

public class DefaultEntityInstance extends BaseValue implements EntityInstance {
   private final Map<Property, Value> assertions;
   private final Entity type;
   private final Optional<AspectModelUrn> aspectModelUrn;

   public DefaultEntityInstance( final Map<Property, Value> assertions, final Entity type, final Optional<AspectModelUrn> aspectModelUrn ) {
      this.assertions = assertions;
      this.type = type;
      this.aspectModelUrn = aspectModelUrn;
   }

   @Override
   public Optional<AspectModelUrn> getAspectModelUrn() {
      return aspectModelUrn;
   }

   @Override
   public Entity getType() {
      return type;
   }

   @Override
   public Map<Property, Value> getAssertions() {
      return assertions;
   }

   @Override
   public boolean isEntityInstance() {
      return true;
   }

   @Override
   public EntityInstance asEntityInstance() {
      return this;
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultEntityInstance.class.getSimpleName() + "[", "]" )
            .add( "assertions=" + assertions )
            .add( "type=" + type )
            .add( "aspectModelUrn=" + aspectModelUrn )
            .toString();
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final DefaultEntityInstance that = (DefaultEntityInstance) o;
      return Objects.equals( assertions, that.assertions ) && Objects.equals( type, that.type ) && Objects.equals( aspectModelUrn,
            that.aspectModelUrn );
   }

   @Override
   public int hashCode() {
      return Objects.hash( assertions, type, aspectModelUrn );
   }
}
