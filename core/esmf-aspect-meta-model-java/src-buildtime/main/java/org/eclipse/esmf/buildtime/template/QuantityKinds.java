/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.buildtime.template;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.processing.Generated;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

/**
 * Enumeration of Quantity Kinds as defined in <a href="http://tfig.unece.org/contents/recommendation-20.htm">Recommendation 20</a>
 * by the <a href="http://www.unece.org/info/ece-homepage.html">UNECE</a> (United Nations Economic Commission for Europe).
 */
@Generated( "${generator}" )
public enum QuantityKinds implements QuantityKind {
   // ${joinedQuantityKinds};
   ;

   private final String name;
   private final String label;

   QuantityKinds( final String name, final String label ) {
      this.name = name;
      this.label = label;
   }

   @Override
   public AspectModelUrn urn() {
      return AspectModelUrn.fromUrn( SammNs.UNIT.urn( name ) );
   }

   @Override
   public AspectModelFile getSourceFile() {
      return MetaModelFile.UNITS;
   }

   /**
    * Returns the quantity kind's unique name
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * Returns the quantity kind's human-readable name
    */
   @Override
   public String getLabel() {
      return label;
   }

   /**
    * Returns the quantity kind's human-readable name
    */
   @Override
   public String toString() {
      return getLabel();
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitQuantityKind( this, context );
   }

   /**
    * Finds the quantity kind with a given name
    */
   public static Optional<QuantityKind> fromName( final String name ) {
      return Arrays.stream( QuantityKinds.values() )
            .filter( quantityKind -> quantityKind.getName().equals( name ) )
            .map( QuantityKind.class::cast )
            .findAny();
   }
}
