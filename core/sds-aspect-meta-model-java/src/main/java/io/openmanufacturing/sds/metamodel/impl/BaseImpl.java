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
package io.openmanufacturing.sds.metamodel.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.IsDescribed;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;

/**
 * The basis meta model of our model.
 */
public abstract class BaseImpl implements Base, IsDescribed {
   private final KnownVersion metaModelVersion;
   private final Optional<AspectModelUrn> urn;
   private final String name;
   private final Map<Locale, String> preferredNames;
   private final Map<Locale, String> descriptions;
   private final List<String> see;
   private final boolean hasSyntheticName;

   BaseImpl( final MetaModelBaseAttributes metaModelBaseAttributes ) {
      metaModelVersion = metaModelBaseAttributes.getMetaModelVersion();
      urn = metaModelBaseAttributes.getUrn();
      name = metaModelBaseAttributes.getName();
      preferredNames = metaModelBaseAttributes.getPreferredNames();
      descriptions = metaModelBaseAttributes.getDescriptions();
      see = metaModelBaseAttributes.getSee();
      hasSyntheticName = metaModelBaseAttributes.hasSyntheticName();
   }

   /**
    * The urn for the element.
    *
    * @return the urn.
    */
   @Override
   public Optional<AspectModelUrn> getAspectModelUrn() {
      return urn;
   }

   /**
    * Returns the Meta Model version this model element is defined against
    */
   @Override
   public KnownVersion getMetaModelVersion() {
      return metaModelVersion;
   }

   /**
    * The name of the Element.
    *
    * @return the name.
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * A language specific name for the Element. There may be multiple preferred
    * names.
    *
    * @return the preferredNames.
    */
   @Override
   public Map<Locale, String> getPreferredNames() {
      return preferredNames;
   }

   /**
    * A language specific description of the Element. There may be multiple
    * descriptions.
    *
    * @return the descriptions.
    */
   @Override
   public Map<Locale, String> getDescriptions() {
      return descriptions;
   }

   /**
    * A language specific name for the Element. There may be multiple preferred
    * names.
    *
    * @param locale of the specific text
    * @return the language specific text.
    */
   @Override
   public String getPreferredName( final Locale locale ) {
      return preferredNames.getOrDefault( locale, getName() );
   }

   /**
    * A language specific description of the Element. There may be multiple
    * descriptions.
    *
    * @param locale of the specific text
    * @return the language specific text or default is {@link Locale#ENGLISH}.
    */
   @Override
   public String getDescription( final Locale locale ) {
      final String defaultDescription = descriptions.get( Locale.ENGLISH );
      return descriptions.getOrDefault( locale, defaultDescription );
   }

   @Override
   public List<String> getSee() {
      return see;
   }

   @Override
   public boolean hasSyntheticName() {
      return hasSyntheticName;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final BaseImpl base = (BaseImpl) o;
      return Objects.equals( urn, base.urn ) &&
            Objects.equals( name, base.name );
   }

   @Override
   public int hashCode() {
      return Objects.hash( urn, name );
   }
}
