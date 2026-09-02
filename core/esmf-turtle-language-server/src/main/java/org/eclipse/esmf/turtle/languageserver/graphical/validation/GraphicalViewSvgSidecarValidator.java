/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.graphical.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewAttributeSelection;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewAttributeTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderTarget;
import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewTarget;

/** Validates the one-to-one relationship between trusted sidecars and SVG marker IDs. */
public final class GraphicalViewSvgSidecarValidator {
   private static final Pattern HEADER_MARKER = Pattern.compile( "gv-header-[a-z0-9]{16,32}" );
   private static final Pattern ATTRIBUTE_MARKER = Pattern.compile( "gv-attribute-[a-z0-9]{16,32}" );
   private static final Pattern LANGUAGE = Pattern.compile( "[a-z]{2,8}(?:-[a-z0-9]{1,8})*" );
   private static final Pattern SVG_ID = Pattern.compile( "\\bid=\\\"([^\\\"]+)\\\"" );

   public boolean isValid( final String svg, final List<? extends GraphicalViewRenderTarget> targets ) {
      if ( svg == null || targets == null ) {
         return false;
      }
      final Set<String> svgMarkers = new HashSet<>();
      final var matcher = SVG_ID.matcher( svg );
      while ( matcher.find() ) {
         final String id = matcher.group( 1 );
         final boolean applicationMarker = id.startsWith( "gv-header-" ) || id.startsWith( "gv-attribute-" );
         final boolean graphperDescendant = id.matches( "gv-(?:header|attribute)-[a-z0-9]{16,32}_(?:polygon|text_0)" );
         if ( applicationMarker && !graphperDescendant && ( !markerMatchesEitherKind( id ) || !svgMarkers.add( id ) ) ) {
            return false;
         }
      }
      final Set<String> sidecarMarkers = new HashSet<>();
      for ( final GraphicalViewRenderTarget target : targets ) {
         final boolean validTarget = switch ( target ) {
            case final GraphicalViewTarget header -> HEADER_MARKER.matcher( header.id() ).matches()
                  && GraphicalViewTarget.ELEMENT_HEADER.equals( header.kind() )
                  && AspectModelUrn.from( header.elementUrn() ).toJavaOptional().isPresent();
            case final GraphicalViewAttributeTarget attribute -> ATTRIBUTE_MARKER.matcher( attribute.id() ).matches()
                  && GraphicalViewAttributeTarget.ATTRIBUTE_ROW.equals( attribute.kind() )
                  && AspectModelUrn.from( attribute.ownerUrn() ).toJavaOptional().isPresent()
                  && AspectModelUrn.from( attribute.predicateUrn() ).toJavaOptional().isPresent()
                  && GraphicalViewAttributeSelection.fromWireValue( attribute.selection() ).isPresent()
                  && ( attribute.language() == null || ( LANGUAGE.matcher( attribute.language() ).matches()
                        && GraphicalViewAttributeSelection.SINGLE_OCCURRENCE.wireValue().equals( attribute.selection() ) ) );
         };
         if ( !validTarget || !sidecarMarkers.add( target.id() ) ) {
            return false;
         }
      }
      return svgMarkers.equals( sidecarMarkers );
   }

   private static boolean markerMatchesEitherKind( final String id ) {
      return HEADER_MARKER.matcher( id ).matches() || ATTRIBUTE_MARKER.matcher( id ).matches();
   }
}
