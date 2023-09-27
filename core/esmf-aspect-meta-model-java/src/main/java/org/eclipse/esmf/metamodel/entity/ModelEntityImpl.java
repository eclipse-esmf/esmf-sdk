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
package org.eclipse.esmf.metamodel.entity;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.samm.KnownVersion;

import java.util.*;

/**
 * The base implemenation of all model elements.
 */
public abstract class ModelEntityImpl implements ModelElement, NamedElement, Comparable<ModelEntityImpl> {
   private KnownVersion metaModelVersion;
   private Optional<AspectModelUrn> urn;
   private String name;
   private Set<LangString> preferredNames;
   private Set<LangString> descriptions;
   private List<String> see;
   private boolean hasSyntheticName;

   ModelEntityImpl(final MetaModelBaseAttributes metaModelBaseAttributes ) {
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
   public boolean equals( Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      ModelEntityImpl base = (ModelEntityImpl) o;
      return Objects.equals( urn, base.urn ) &&
            Objects.equals( name, base.name );
   }

   @Override
   public int hashCode() {
      return Objects.hash( urn, name );
   }

   @Override
   public int compareTo( ModelEntityImpl o ) {
      if ( this.urn.isPresent() && o.urn.isPresent() )
         return this.urn.get().compareTo( o.urn.get() );
      return Comparator
            .comparing( ModelEntityImpl::getMetaModelVersion )
            .thenComparing( ModelEntityImpl::getName )
            .thenComparing( ModelEntityImpl::hasSyntheticName )
            .compare( this, o );
   }
}
