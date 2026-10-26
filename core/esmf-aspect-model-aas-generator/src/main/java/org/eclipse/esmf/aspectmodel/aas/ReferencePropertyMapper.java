/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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
package org.eclipse.esmf.aspectmodel.aas;

import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceElement;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReferenceElement;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;

/**
 * Maps the predefined SAMM-C Reference characteristic to an AAS ReferenceElement.
 */
public class ReferencePropertyMapper implements PropertyMapper<ReferenceElement> {
   private static final String SAMM_CHARACTERISTIC_NAMESPACE = "urn:samm:org.eclipse.esmf.samm:characteristic:";
   private static final String REFERENCE_CHARACTERISTIC_NAME = "Reference";

   @Override
   public boolean canHandle( final Property property ) {
      return property.getCharacteristic()
            .map( Characteristic::urn )
            .filter( this::isSammReferenceCharacteristic )
            .isPresent();
   }

   @Override
   public ReferenceElement mapToAasProperty( final Type type, final Property property, final Context context ) {
      final DefaultReferenceElement referenceElement = new DefaultReferenceElement();
      referenceElement.setIdShort( context.getPropertyIdShort( this ) );
      referenceElement.setDisplayName( LangStringMapper.NAME.map( property.getPreferredNames() ) );
      referenceElement.setDescription( LangStringMapper.TEXT.map( property.getDescriptions() ) );
      referenceElement.setSemanticId( buildPropertyReferenceToGlobalReference( property ) );

      final String referenceValue = context.getPropertyValue( UNKNOWN_EXAMPLE );
      if ( !UNKNOWN_EXAMPLE.equals( referenceValue ) ) {
         referenceElement.setValue( buildGlobalReference( referenceValue ) );
      }

      return referenceElement;
   }

   private boolean isSammReferenceCharacteristic( final AspectModelUrn urn ) {
      return REFERENCE_CHARACTERISTIC_NAME.equals( urn.getName() )
            && urn.getNamespaceIdentifier().startsWith( SAMM_CHARACTERISTIC_NAMESPACE );
   }

   private Reference buildGlobalReference( final String value ) {
      return new DefaultReference.Builder()
            .type( ReferenceTypes.EXTERNAL_REFERENCE )
            .keys( new DefaultKey.Builder()
                  .type( KeyTypes.GLOBAL_REFERENCE )
                  .value( value )
                  .build() )
            .build();
   }
}
