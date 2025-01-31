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

package org.eclipse.esmf.metamodel;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

/**
 * Model elements pre-defined in SAMM, sorted by namespace.
 */
// Names of namespace structures and element names are following original RDF definition names
@SuppressWarnings( { "NewClassNamingConvention", "checkstyle:TypeName", "checkstyle:ConstantName" } )
public class Elements {
   /**
    * All model elements defined in the scope of SAMM (i.e., samm-c:, samm-e: and unit:), indexed by URN
    */
   private static final Map<String, ModelElement> ELEMENTS = new AspectModelLoader()
         .loadAspectModelFiles( MetaModelFile.getElementDefinitionsFiles().stream().<AspectModelFile> map( Function.identity() ).toList() )
         .elements().stream()
         .collect( Collectors.toMap( element -> element.urn().toString(), Function.identity() ) );

   public static class samm_c {
      public static final Characteristic MimeType = (Characteristic) ELEMENTS.get( SammNs.SAMMC.MimeType().getURI() );
      public static final Characteristic ResourcePath = (Characteristic) ELEMENTS.get( SammNs.SAMMC.ResourcePath().getURI() );
      public static final Characteristic UnitReference = (Characteristic) ELEMENTS.get( SammNs.SAMMC.UnitReference().getURI() );
      public static final Characteristic Language = (Characteristic) ELEMENTS.get( SammNs.SAMMC.Language().getURI() );
      public static final Characteristic Locale = (Characteristic) ELEMENTS.get( SammNs.SAMMC.Locale().getURI() );
      public static final Characteristic Boolean = (Characteristic) ELEMENTS.get( SammNs.SAMMC.Boolean().getURI() );
      public static final Characteristic MultiLanguageText = (Characteristic) ELEMENTS.get( SammNs.SAMMC.MultiLanguageText().getURI() );
      public static final Characteristic Text = (Characteristic) ELEMENTS.get( SammNs.SAMMC.Text().getURI() );
      public static final Characteristic Timestamp = (Characteristic) ELEMENTS.get( SammNs.SAMMC.Timestamp().getURI() );
   }

   public static class samm_e {
      public static final AbstractEntity TimeSeriesEntity = (AbstractEntity) ELEMENTS.get( SammNs.SAMME.TimeSeriesEntity().getURI() );
      public static final AbstractEntity Point3d = (AbstractEntity) ELEMENTS.get( SammNs.SAMME.Point3d().getURI() );
      public static final Property x = (Property) ELEMENTS.get( SammNs.SAMME.x().getURI() );
      public static final Property y = (Property) ELEMENTS.get( SammNs.SAMME.y().getURI() );
      public static final Property z = (Property) ELEMENTS.get( SammNs.SAMME.z().getURI() );
      public static final Entity FileResource = (Entity) ELEMENTS.get( SammNs.SAMME.FileResource().getURI() );
      public static final Property resource = (Property) ELEMENTS.get( SammNs.SAMME.resource().getURI() );
      public static final Property mimeType = (Property) ELEMENTS.get( SammNs.SAMME.mimeType().getURI() );
   }
}
