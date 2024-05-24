/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.metamodel.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.HasDescription;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.datatypes.LangString;

/**
 * Wrapper class for the attributes all Aspect Meta Model elements have.
 */
public class MetaModelBaseAttributes implements HasDescription {
   private final AspectModelUrn urn;
   private final Set<LangString> preferredNames;
   private final Set<LangString> descriptions;
   private final List<String> see;
   private final boolean isAnonymous;

   private MetaModelBaseAttributes(
         final AspectModelUrn urn,
         final Set<LangString> preferredNames,
         final Set<LangString> descriptions,
         final List<String> see,
         final boolean isAnonymous ) {
      this.urn = urn;
      this.preferredNames = preferredNames;
      this.descriptions = descriptions;
      this.see = see;
      this.isAnonymous = isAnonymous;
   }

   public AspectModelUrn urn() {
      return urn;
   }

   @Override
   public String getName() {
      return urn().getName();
   }

   @Override
   public Set<LangString> getPreferredNames() {
      return preferredNames;
   }

   @Override
   public Set<LangString> getDescriptions() {
      return descriptions;
   }

   @Override
   public List<String> getSee() {
      return see;
   }

   public boolean isAnonymous() {
      return isAnonymous;
   }

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final MetaModelBaseAttributes that = (MetaModelBaseAttributes) o;
      return isAnonymous == that.isAnonymous && Objects.equals( urn, that.urn ) && Objects.equals( preferredNames,
            that.preferredNames ) && Objects.equals( descriptions, that.descriptions ) && Objects.equals( see, that.see );
   }

   @Override
   public int hashCode() {
      return Objects.hash( urn, preferredNames, descriptions, see, isAnonymous );
   }

   /**
    * Creates an instance of MetaModelBaseAttributes by copying them from a given element.
    *
    * @param modelElement the named model element to copy the base attributes from
    * @return the newly created instance
    */
   public static MetaModelBaseAttributes fromModelElement( final ModelElement modelElement ) {
      return new MetaModelBaseAttributes( modelElement.urn(), modelElement.getPreferredNames(), modelElement.getDescriptions(),
            modelElement.getSee(), modelElement.isAnonymous() );
   }

   public static class Builder {
      private AspectModelUrn urn;
      private final Set<LangString> preferredNames = new HashSet<>();
      private final Set<LangString> descriptions = new HashSet<>();
      private final List<String> see = new ArrayList<>();
      private boolean isAnonymous = true;

      public Builder withUrn( final String urn ) {
         return withUrn( AspectModelUrn.fromUrn( urn ) );
      }

      public Builder withUrn( final AspectModelUrn urn ) {
         isAnonymous = false;
         this.urn = urn;
         return this;
      }

      public Builder isAnonymous() {
         isAnonymous = true;
         return this;
      }

      public Builder withPreferredName( final Locale locale, final String preferredName ) {
         preferredNames.add( new LangString( preferredName, locale ) );
         return this;
      }

      public Builder withPreferredNames( final Set<LangString> preferredNames ) {
         this.preferredNames.addAll( preferredNames );
         return this;
      }

      public Builder withDescription( final Locale locale, final String description ) {
         descriptions.add( new LangString( description, locale ) );
         return this;
      }

      public Builder withDescriptions( final Set<LangString> descriptions ) {
         this.descriptions.addAll( descriptions );
         return this;
      }

      public Builder withSee( final String see ) {
         this.see.add( see );
         return this;
      }

      public Builder withSee( final List<String> see ) {
         this.see.addAll( see );
         return this;
      }

      public Builder isAnonymous( final boolean isAnonymous ) {
         this.isAnonymous = isAnonymous;
         return this;
      }

      public MetaModelBaseAttributes build() {
         return new MetaModelBaseAttributes( urn, preferredNames, descriptions, see, isAnonymous );
      }
   }
}
