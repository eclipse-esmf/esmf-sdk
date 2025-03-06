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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache_gs.commons.lang3.StringUtils;
import org.graphper.api.attributes.FontStyle;
import org.graphper.def.FlatPoint;
import org.graphper.layout.MeasureText;

public class FontMeasurement implements MeasureText {
   @Override
   public int order() {
      return -1;
   }

   @Override
   public boolean envSupport() {
      return true;
   }

   @Override
   public FlatPoint measure( final String text, final String fontName, final double fontSize, final FontStyle... fontStyles ) {
      if ( StringUtils.isEmpty( text ) || fontSize <= 0 ) {
         return new FlatPoint( 0, 0 );
      }

      final Font font = new Font( fontName, Font.PLAIN, (int) fontSize );
      final AffineTransform affineTransform = font.getTransform();
      final FontRenderContext fontRenderContext = new FontRenderContext( affineTransform, true, true );
      final Rectangle2D stringBounds = font.getStringBounds( text, fontRenderContext );
      final int width = (int) ( stringBounds.getWidth() ) + 10;
      final int height = (int) ( stringBounds.getHeight() );
      return new FlatPoint( height, width );
   }
}
