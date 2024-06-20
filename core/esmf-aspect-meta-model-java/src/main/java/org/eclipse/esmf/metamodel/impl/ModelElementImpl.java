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
package org.eclipse.esmf.metamodel.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.ModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;

/**
 * The base implemenation of all model elements.
 */
public abstract class ModelElementImpl implements ModelElement, Comparable<ModelElement> {
   protected final MetaModelBaseAttributes baseAttributes;
   protected final ModelFile sourceFile;

   ModelElementImpl( final MetaModelBaseAttributes baseAttributes ) {
      this( baseAttributes, null );
   }

   ModelElementImpl( final MetaModelBaseAttributes baseAttributes, final ModelFile sourceFile ) {
      this.baseAttributes = baseAttributes;
      this.sourceFile = sourceFile;
   }

   @Override
   public AspectModelUrn urn() {
      return baseAttributes.isAnonymous() && baseAttributes.urn() == null
            ? ModelElement.super.urn()
            : baseAttributes.urn();
   }

   @Override
   public String getName() {
      return baseAttributes.getName() != null ? baseAttributes.getName() : urn().getName();
   }

   @Override
   public boolean isAnonymous() {
      return baseAttributes.isAnonymous();
   }

   @Override
   public Optional<ModelFile> getSourceFile() {
      return Optional.of( sourceFile );
   }

   /**
    * A language specific name for the Element. There may be multiple preferred names.
    *
    * @return the preferredNames.
    */
   @Override
   public Set<LangString> getPreferredNames() {
      return baseAttributes.getPreferredNames();
   }

   /**
    * A language specific description of the Element. There may be multiple descriptions.
    *
    * @return the descriptions.
    */
   @Override
   public Set<LangString> getDescriptions() {
      return baseAttributes.getDescriptions();
   }

   @Override
   public List<String> getSee() {
      return baseAttributes.getSee();
   }

   @Override
   public int compareTo( final ModelElement o ) {
      return urn().compareTo( o.urn() );
   }

   @Override
   public boolean equals( final Object obj ) {
      if ( this == obj ) {
         return true;
      }
      if ( obj == null || getClass() != obj.getClass() ) {
         return false;
      }
      final ModelElementImpl that = (ModelElementImpl) obj;
      return urn().equals( that.urn() );
   }

   @Override
   public int hashCode() {
      return urn().hashCode();
   }
}
