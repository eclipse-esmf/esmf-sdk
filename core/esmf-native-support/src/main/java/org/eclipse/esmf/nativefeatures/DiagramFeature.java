/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.nativefeatures;

import static org.eclipse.esmf.nativefeatures.Native.clazz;
import static org.eclipse.esmf.nativefeatures.Native.isLinux;
import static org.eclipse.esmf.nativefeatures.Native.isWindows;

import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

/**
 * This feature configures AWT, JDK font classes etc. for reflection and JNI access, as is required by the
 * {@link AspectModelDiagramGenerator}.
 */
@Platforms( Platform.HOSTED_ONLY.class )
public class DiagramFeature implements Feature {
   @Override
   public void beforeAnalysis( final Feature.BeforeAnalysisAccess access ) {
      setupAwt();
      setupFontConfiguration();
      setupJava2d();
      setupBatikConfiguration();
      setupGraphSupportConfiguration();
   }

   private void setupJava2d() {
      if ( isWindows() ) {
         Native.forClass( "sun.java2d.d3d.D3DGraphicsDevice" )
               .registerEverythingForReflection()
               .registerEverythingForJni();
         Native.forClass( "sun.java2d.d3d.D3DGraphicsDevice$1" )
               .registerMethodForJni( "run" );
         Native.forClass( "sun.java2d.d3d.D3DRenderQueue$1" )
               .registerMethodForJni( "run" );
         Native.forClass( "sun.java2d.windows.WindowsFlags" )
               .registerFieldsForJni( "d3dEnabled", "d3dSet", "offscreenSharingEnabled", "setHighDPIAware" );
      }

      Native.forClass( "sun.java2d.marlin.DMarlinRenderingEngine" )
            .registerConstructorForReflection();

      Native.forClass( "sun.java2d.loops.GraphicsPrimitive" )
            .initializeAtBuildTime()
            .registerFieldsForJni( "pNativePrim" );
      Native.forArrayClass( "sun.java2d.loops.GraphicsPrimitive" )
            .registerEverythingForJni();
      Native.forClass( "sun.java2d.Disposer" )
            .registerMethodForJni( "addRecord", java.lang.Object.class, long.class, long.class );
      Native.forClass( "sun.java2d.InvalidPipeException" )
            .registerEverythingForJni();
      Native.forClass( "sun.java2d.NullSurfaceData" )
            .registerEverythingForJni();
      Native.forClass( "sun.java2d.SunGraphics2D" )
            .registerFieldsForJni( "clipRegion", "composite", "eargb", "lcdTextContrast", "pixel", "strokeHint" );
      Native.forClass( "sun.java2d.SunGraphicsEnvironment" )
            .registerMethodForJni( "isDisplayLocal" );
      Native.forClass( "sun.java2d.SurfaceData" )
            .registerFieldsForJni( "pData", "valid" );
      Native.forClass( "sun.java2d.loops.SurfaceType" )
            .initializeAtBuildTime();
      Native.forClass( "sun.java2d.loops.CompositeType" )
            .initializeAtBuildTime();
      Native.forClass( "sun.java2d.loops.Blit" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.BlitBg" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.CompositeType" )
            .registerFieldsForJni( "AnyAlpha", "Src", "SrcNoEa", "SrcOver", "SrcOverNoEa", "Xor" );
      Native.forClass( "sun.java2d.loops.DrawGlyphList" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.DrawGlyphListAA" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.DrawGlyphListLCD" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.DrawLine" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.DrawParallelogram" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.DrawPath" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.DrawPolygons" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.DrawRect" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.FillParallelogram" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.FillPath" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.FillRect" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.FillSpans" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.GraphicsPrimitiveMgr" )
            .registerMethodForJni( "register", clazz( "sun.java2d.loops.GraphicsPrimitive" ).arrayType() );
      Native.forClass( "sun.java2d.loops.MaskBlit" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.MaskFill" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.ScaledBlit" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.SurfaceType" )
            .registerFieldsForJni( "Any3Byte", "Any4Byte", "AnyByte", "AnyColor", "AnyInt", "AnyShort", "ByteBinary1Bit", "ByteBinary2Bit",
                  "ByteBinary4Bit", "ByteGray", "ByteIndexed", "ByteIndexedBm", "FourByteAbgr", "FourByteAbgrPre", "Index12Gray",
                  "Index8Gray", "IntArgb", "IntArgbBm", "IntArgbPre", "IntBgr", "IntRgb", "IntRgbx", "OpaqueColor", "ThreeByteBgr",
                  "Ushort4444Argb", "Ushort555Rgb", "Ushort555Rgbx", "Ushort565Rgb", "UshortGray", "UshortIndexed" );
      Native.forClass( "sun.java2d.loops.TransformHelper" )
            .registerConstructorForJni( long.class, clazz( "sun.java2d.loops.SurfaceType" ),
                  clazz( "sun.java2d.loops.CompositeType" ), clazz( "sun.java2d.loops.SurfaceType" ) );
      Native.forClass( "sun.java2d.loops.XORComposite" )
            .registerFieldsForJni( "alphaMask", "xorColor", "xorPixel" );
      Native.forClass( "sun.java2d.pipe.Region" )
            .registerFieldsForJni( "bands", "endIndex", "hix", "hiy", "lox", "loy" );
      Native.forClass( "sun.java2d.pipe.RegionIterator" )
            .registerFieldsForJni( "curIndex", "numXbands", "region" );
      Native.forClass( "sun.java2d.pipe.ShapeSpanIterator" )
            .registerFieldsForJni( "pData" );
      Native.forClass( "sun.java2d.xr.XRSurfaceData" )
            .registerFieldsForJni( "picture", "xid" );
   }

   private void setupAwt() {
      if ( isLinux() ) {
         Native.forClass( "sun.awt.X11.XToolkit" )
               .registerEverythingForReflection()
               .registerEverythingForJni();
         Native.forClass( "sun.awt.X11.XErrorHandlerUtil" )
               .registerMethodForJni( "init", long.class );
         Native.forClass( "sun.awt.X11GraphicsConfig" )
               .registerFieldsForJni( "aData", "bitsPerPixel" );
         Native.forClass( "sun.awt.X11GraphicsDevice" )
               .registerMethodForJni( "addDoubleBufferVisual", int.class );
      } else if ( isWindows() ) {
         Native.forClass( "sun.awt.Win32GraphicsDevice" )
               .registerEverythingForReflection();
         Native.forClass( "sun.awt.Win32FontManager" )
               .registerAllConstructorsForReflection();
         Native.forClass( "sun.awt.Win32GraphicsDevice" )
               .registerEverythingForJni();
         Native.forClass( "sun.awt.Win32GraphicsConfig" )
               .registerFieldsForJni( "visual" );
         Native.forClass( "sun.awt.Win32GraphicsEnvironment" )
               .registerMethodForJni( "dwmCompositionChanged", boolean.class );
         Native.forClass( "sun.awt.windows.WComponentPeer" )
               .registerFieldsForJni( "hwnd", "winGraphicsConfig" )
               .registerMethodForJni( "disposeLater" )
               .registerMethodForJni( "replaceSurfaceData" )
               .registerMethodForJni( "replaceSurfaceDataLater" );
         Native.forClass( "sun.awt.windows.WDesktopPeer" )
               .registerMethodForJni( "systemSleepCallback", boolean.class )
               .registerMethodForJni( "userSessionCallback", boolean.class, java.awt.desktop.UserSessionEvent.Reason.class );
         Native.forClass( "sun.awt.windows.WObjectPeer" )
               .registerFieldsForJni( "createError", "destroyed", "pData", "target" )
               .registerMethodForJni( "getPeerForTarget", java.lang.Object.class );
         Native.forClass( "sun.awt.windows.WToolkit" )
               .registerMethodForJni( "displayChanged" )
               .registerMethodForJni( "paletteChanged" )
               .registerMethodForJni( "windowsSettingChange" );
      }

      Native.forClass( java.awt.Color.class )
            .registerAllFieldsForReflection()
            .registerAllMethodsForReflection();
      Native.forClass( java.awt.Insets.class )
            .registerAllFieldsForReflection()
            .registerAllMethodsForReflection();
      Native.forClass( java.awt.event.InputEvent.class )
            .registerEverythingForReflection();
      Native.forClass( java.awt.AWTEvent.class )
            .registerEverythingForReflection();
      Native.forClass( java.awt.Component.class )
            .registerEverythingForReflection();
      Native.forClass( java.awt.Font.class )
            .registerConstructorForReflection( String.class, int.class, int.class );
      Native.forClass( java.awt.Canvas.class )
            .registerConstructorForReflection();
      Native.forClass( java.awt.FontMetrics.class )
            .registerMethodForReflection( "getHeight" )
            .registerMethodForReflection( "stringWidth", String.class );

      Native.forClass( java.awt.Cursor.class )
            .registerFieldsForJni( "pData", "type" )
            .registerMethodForJni( "setPData", long.class );
      Native.forClass( java.awt.Dimension.class )
            .registerFieldsForJni( "height", "width" );
      Native.forClass( java.awt.Font.class )
            .registerFieldsForJni( "name", "pData", "size", "style" )
            .registerMethodForJni( "getFont", String.class )
            .registerMethodForJni( "getFontPeer" );
      Native.forClass( java.awt.Point.class )
            .registerFieldsForJni( "x", "y" );
      Native.forClass( java.awt.Toolkit.class )
            .registerMethodForJni( "getDefaultToolkit" )
            .registerMethodForJni( "getFontMetrics", java.awt.Font.class );
      Native.forClass( java.awt.desktop.UserSessionEvent.Reason.class )
            .registerFieldsForJni( "CONSOLE", "LOCK", "REMOTE", "UNSPECIFIED" );
      Native.forClass( java.awt.Color.class )
            .registerAllFieldsForJni()
            .registerAllMethodsForJni();
      Native.forClass( java.awt.Insets.class )
            .registerAllFieldsForJni()
            .registerAllMethodsForJni();
      Native.forClass( java.awt.event.InputEvent.class )
            .registerEverythingForJni();
      Native.forClass( java.awt.image.BufferedImage.class )
            .registerFieldsForJni( "colorModel", "imageType", "raster" )
            .registerMethodForJni( "getRGB", int.class, int.class, int.class, int.class, int[].class, int.class, int.class )
            .registerMethodForJni( "setRGB", int.class, int.class, int.class, int.class, int[].class, int.class, int.class );
      Native.forClass( java.awt.image.Raster.class )
            .registerFieldsForJni( "dataBuffer", "height", "minX", "minY", "numBands", "numDataElements", "sampleModel",
                  "sampleModelTranslateX", "sampleModelTranslateY", "width" );
      Native.forClass( java.awt.image.SampleModel.class )
            .registerFieldsForJni( "height", "width" )
            .registerMethodForJni( "getPixels", int.class, int.class, int.class, int.class, int[].class, java.awt.image.DataBuffer.class )
            .registerMethodForJni( "setPixels", int.class, int.class, int.class, int.class, int[].class, java.awt.image.DataBuffer.class );
      Native.forClass( java.awt.image.SinglePixelPackedSampleModel.class )
            .registerFieldsForJni( "bitMasks", "bitOffsets", "bitSizes", "maxBitSize" );
      Native.forClass( java.awt.AWTEvent.class )
            .registerEverythingForJni();
      Native.forClass( java.awt.AlphaComposite.class )
            .registerFieldsForJni( "extraAlpha", "rule" );
      Native.forClass( java.awt.Component.class )
            .registerEverythingForJni();
      Native.forClass( java.awt.GraphicsEnvironment.class )
            .registerMethodForJni( "getLocalGraphicsEnvironment" )
            .registerMethodForJni( "isHeadless" );
      Native.forClass( java.awt.event.KeyEvent.class )
            .registerFieldsForJni( "isProxyActive" );
      Native.forClass( java.awt.geom.AffineTransform.class )
            .registerFieldsForJni( "m00", "m01", "m02", "m10", "m11", "m12" );
      Native.forClass( java.awt.geom.GeneralPath.class )
            .registerConstructorForJni()
            .registerConstructorForJni( int.class, byte[].class, int.class, float[].class, int.class );
      Native.forClass( java.awt.geom.Path2D.class )
            .registerFieldsForJni( "numTypes", "pointTypes", "windingRule" );
      Native.forClass( java.awt.geom.Path2D.Float.class )
            .registerFieldsForJni( "floatCoords" );
      Native.forClass( java.awt.geom.Point2D.Float.class )
            .registerFieldsForJni( "x", "y" )
            .registerConstructorForJni( float.class, float.class );
      Native.forClass( java.awt.geom.Rectangle2D.Float.class )
            .registerFieldsForJni( "height", "width", "x", "y" )
            .registerConstructorForJni()
            .registerConstructorForJni( float.class, float.class, float.class, float.class );
      Native.forClass( java.awt.image.ColorModel.class )
            .registerFieldsForJni( "colorSpace", "colorSpaceType", "isAlphaPremultiplied", "is_sRGB", "nBits", "numComponents", "pData",
                  "supportsAlpha", "transparency" )
            .registerMethodForJni( "getRGBdefault" );
      Native.forClass( java.awt.image.IndexColorModel.class )
            .registerFieldsForJni( "allgrayopaque", "map_size", "rgb", "transparent_index", "colorData" );
      Native.forClass( "sun.awt.AWTAutoShutdown" )
            .registerMethodForJni( "notifyToolkitThreadBusy" )
            .registerMethodForJni( "notifyToolkitThreadFree" );
      Native.forClass( "sun.awt.SunHints" )
            .registerFieldsForJni( "INTVAL_STROKE_PURE" );
      Native.forClass( "sun.awt.SunToolkit" )
            .registerMethodForJni( "awtLock" )
            .registerMethodForJni( "awtLockNotify" )
            .registerMethodForJni( "awtLockNotifyAll" )
            .registerMethodForJni( "awtLockWait", long.class )
            .registerMethodForJni( "awtUnlock" )
            .registerMethodForJni( "isTouchKeyboardAutoShowEnabled" );
      Native.forClass( "sun.awt.image.ByteComponentRaster" )
            .registerFieldsForJni( "data", "dataOffsets", "pixelStride", "scanlineStride", "type" );
      Native.forClass( "sun.awt.image.GifImageDecoder" )
            .registerFieldsForJni( "outCode", "prefix", "suffix" )
            .registerMethodForJni( "readBytes", byte[].class, int.class, int.class )
            .registerMethodForJni( "sendPixels", int.class, int.class, int.class, int.class, byte[].class,
                  java.awt.image.ColorModel.class );
      Native.forClass( "sun.awt.image.BufImgSurfaceData$ICMColorData" )
            .registerFieldsForJni( "pData" )
            .registerConstructorForJni( long.class );
      Native.forClass( "sun.awt.image.ImageRepresentation" )
            .registerFieldsForJni( "numSrcLUT", "srcLUTtransIndex" );
      Native.forClass( "sun.awt.image.IntegerComponentRaster" )
            .registerFieldsForJni( "data", "dataOffsets", "pixelStride", "scanlineStride", "type" );
      Native.forClass( "sun.awt.image.SunVolatileImage" )
            .registerFieldsForJni( "volSurfaceManager" );
      Native.forClass( "sun.awt.image.VolatileSurfaceManager" )
            .registerFieldsForJni( "sdCurrent" );

      Native.addClassBasedResourceBundle( "sun.awt.resources.awt", "sun.awt.resources.awt" );
   }

   private void setupBatikConfiguration() {
      Native.forClass( "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl" )
            .registerConstructorForReflection();
      Native.forClass( "org.apache.batik.bridge.RhinoInterpreterFactory" )
            .registerConstructorForReflection();
      Native.forClass( "org.apache.batik.css.parser.Parser" )
            .registerEverythingForReflection();
      Native.forClass( "org.apache.batik.ext.awt.image.codec.png.PNGTranscoderInternalCodecWriteAdapter" )
            .registerConstructorForReflection();

      Native.addResource( "org/apache/batik/anim/dom/resources/UserAgentStyleSheet.css" );
      Native.addResource( "org/apache/batik/bridge/resources/help.gif" );
      Native.addResourcesPattern( "org/apache/batik.*\\.properties" );
   }

   private void setupGraphSupportConfiguration() {
      Native.forArrayClass( "org.graphper.layout.dot.DNode" )
            .registerClassForReflection();
      Native.forClass( "org.graphper.api.LineAttrs" )
            .registerAllFieldsForReflection();
      Native.forClass( "org.graphper.api.NodeAttrs" )
            .registerAllFieldsForReflection();

      Native.addResource( "META-INF/services/org.graphper.draw.CustomizeShapeRender" );
      Native.addResource( "META-INF/services/org.graphper.layout.MeasureText" );
   }

   private void setupFontConfiguration() {
      Native.forClass( "sun.font.FontConfigManager$FontConfigInfo" )
            .registerAllFieldsForReflection();
      Native.forClass( "sun.font.FontConfigManager$FcCompFont" )
            .registerAllFieldsForReflection();
      Native.forClass( "sun.font.FontConfigManager$FontConfigFont" )
            .registerEverythingForReflection();
      Native.forClass( "sun.font.CompositeFontDescriptor" )
            .registerEverythingForReflection();
      Native.forClass( "sun.font.FontDesignMetrics" )
            .registerEverythingForReflection();
      Native.forClass( "sun.font.FontDesignMetrics" )
            .registerEverythingForJni();
      Native.forClass( "sun.font.FontConfigManager" )
            .registerAllFieldsForJni()
            .registerAllMethodsForJni();
      Native.forClass( "sun.font.FontConfigManager$FontConfigInfo" )
            .registerAllFieldsForJni()
            .registerAllMethodsForJni();
      Native.forClass( "sun.font.FontConfigManager$FcCompFont" )
            .registerAllFieldsForJni()
            .registerAllMethodsForJni();
      Native.forClass( "sun.font.FontConfigManager$FontConfigFont" )
            .registerEverythingForJni();
      Native.forClass( java.awt.FontMetrics.class )
            .registerFieldsForJni( "font" )
            .registerMethodForJni( "getHeight" );
      Native.forClass( "sun.font.CharToGlyphMapper" )
            .registerMethodForJni( "charToGlyph", int.class );
      Native.forClass( "sun.font.Font2D" )
            .registerMethodForJni( "canDisplay", char.class )
            .registerMethodForJni( "charToGlyph", int.class )
            .registerMethodForJni( "charToVariationGlyph", int.class, int.class )
            .registerMethodForJni( "getMapper" )
            .registerMethodForJni( "getTableBytes", int.class );
      Native.forClass( "sun.font.FontStrike" )
            .registerMethodForJni( "getGlyphMetrics", int.class );
      Native.forClass( "sun.font.FreetypeFontScaler" )
            .registerMethodForJni( "invalidateScaler" );
      Native.forClass( "sun.font.GlyphList" )
            .registerFieldsForJni( "gposx", "gposy", "images", "lcdRGBOrder", "lcdSubPixPos", "len", "positions", "usePositions" );
      Native.forClass( "sun.font.GlyphLayout$GVData" )
            .registerFieldsForJni( "_count", "_flags", "_glyphs", "_indices", "_positions" )
            .registerMethodForJni( "grow" );
      Native.forClass( "sun.font.PhysicalStrike" )
            .registerFieldsForJni( "pScalerContext" )
            .registerMethodForJni( "adjustPoint", java.awt.geom.Point2D.Float.class )
            .registerMethodForJni( "getGlyphPoint", int.class, int.class );
      Native.forClass( "sun.font.StrikeMetrics" )
            .registerConstructorForJni( float.class, float.class, float.class, float.class, float.class, float.class, float.class,
                  float.class, float.class, float.class );
      Native.forClass( "sun.font.TrueTypeFont" )
            .registerMethodForJni( "readBlock", java.nio.ByteBuffer.class, int.class, int.class )
            .registerMethodForJni( "readBytes", int.class, int.class );
      Native.forClass( "sun.font.Type1Font" )
            .registerMethodForJni( "readFile", java.nio.ByteBuffer.class );

      if ( isWindows() ) {
         Native.addResource( "fontconfig.bfc" );
      }
   }
}
