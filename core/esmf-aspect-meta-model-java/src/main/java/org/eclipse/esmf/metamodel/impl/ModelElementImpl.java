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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;

/**
 * The base implemenation of all model elements.
 */
public abstract class ModelElementImpl implements ModelElement, NamedElement, Comparable<ModelElementImpl> {
   private final KnownVersion metaModelVersion;
   private final Optional<AspectModelUrn> urn;
   private final String name;
   private final Set<LangString> preferredNames;
   private final Set<LangString> descriptions;
   private final List<String> see;
   private final boolean hasSyntheticName;

   ModelElementImpl( final MetaModelBaseAttributes metaModelBaseAttributes ) {
      metaModelVersion = metaModelBaseAttributes.getMetaModelVersion();
      urn = metaModelBaseAttributes.getUrn();
      name = metaModelBaseAttributes.getName();
      preferredNames = metaModelBaseAttributes.getPreferredNames();
      descriptions = metaModelBaseAttributes.getDescriptions();
      see = metaModelBaseAttributes.getSee();
      hasSyntheticName = metaModelBaseAttributes.hasSyntheticName();
   }

   /**
    * The URN for the element, if present. Certain elements (such as Constraints) are allowed to not have URNs, which is why the URN is optional.
    *
    * @return the URN.
    */
   @Override
   public Optional<AspectModelUrn> getAspectModelUrn() {
      return urn;
   }

   /**
    * Returns the metamodel version this model element is defined against
    */
   @Override
   public KnownVersion getMetaModelVersion() {
      return metaModelVersion;
   }

   /**
    * The name of the element.
    *
    * @return the name.
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * A language specific name for the Element. There may be multiple preferred names.
    *
    * @return the preferredNames.
    */
   @Override
   public Set<LangString> getPreferredNames() {
      return preferredNames;
   }

   /**
    * A language specific description of the Element. There may be multiple descriptions.
    *
    * @return the descriptions.
    */
   @Override
   public Set<LangString> getDescriptions() {
      return descriptions;
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
      final ModelElementImpl base = (ModelElementImpl) o;
      return Objects.equals( urn, base.urn ) &&
            Objects.equals( name, base.name );
   }

   @Override
   public int hashCode() {
      return Objects.hash( urn, name );
   }

   @Override
   public int compareTo( ModelElementImpl o ) {
      if ( this.urn.isPresent() && o.urn.isPresent() )
         return this.urn.get().compareTo( o.urn.get() );
      return Comparator
            .comparing( ModelElementImpl::getMetaModelVersion )
            .thenComparing( ModelElementImpl::getName )
            .thenComparing( ModelElementImpl::hasSyntheticName )
            .compare( this, o );
   }
}
