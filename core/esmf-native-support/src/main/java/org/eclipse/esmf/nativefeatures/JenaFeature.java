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

import org.graalvm.nativeimage.hosted.Feature;

/**
 * Configuration of eagerly initialized Jena classes
 */
public class JenaFeature implements Feature {
   @Override
   public void beforeAnalysis( final Feature.BeforeAnalysisAccess access ) {
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.Constants" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLEntityManager" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLEntityManager$EncodingInfo" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLEntityScanner" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLScanner" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.XMLVersionDetector" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.util.FeatureState" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.util.PropertyState" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.util.XMLChar" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.util.XMLSymbols" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.org.apache.xerces.internal.xni.NamespaceContext" )
            .initializeAtBuildTime();
      Native.forClass( "com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator" )
            .initializeAtBuildTime();
      Native.forClass( "javax.xml.parsers.FactoryFinder" )
            .initializeAtBuildTime();
      Native.forClass( "jdk.xml.internal.JdkXmlUtils" )
            .initializeAtBuildTime();
      Native.forClass( "jdk.xml.internal.SecuritySupport" )
            .initializeAtBuildTime();
      Native.forClass( org.apache.jena.ext.xerces.impl.dv.xs.AnyURIDV.class )
            .initializeAtBuildTime();
      Native.forClass( org.apache.jena.ext.xerces.impl.dv.xs.BaseSchemaDVFactory.class )
            .initializeAtBuildTime();
      Native.forClass( org.apache.jena.ext.xerces.impl.dv.xs.ExtendedSchemaDVFactoryImpl.class )
            .initializeAtBuildTime();
      Native.forClass( org.apache.jena.ext.xerces.impl.dv.xs.QNameDV.class )
            .initializeAtBuildTime();
      Native.forClass( org.apache.jena.ext.xerces.impl.dv.xs.SchemaDVFactoryImpl.class )
            .initializeAtBuildTime();
      Native.forClass( org.apache.jena.ext.xerces.impl.dv.xs.XSSimpleTypeDecl.class )
            .initializeAtBuildTime();
      Native.forClass( org.apache.jena.ext.xerces.impl.dv.xs.XSSimpleTypeDecl.class )
            .initializeAtBuildTime();
      Native.forClass( "org.apache.jena.ext.xerces.impl.xpath.regex.Token$CharToken" )
            .initializeAtBuildTime();
      Native.forClass( "org.apache.jena.ext.xerces.impl.xpath.regex.Token$ClosureToken" )
            .initializeAtBuildTime();
      Native.forClass( "org.apache.jena.ext.xerces.impl.xpath.regex.Token$ParenToken" )
            .initializeAtBuildTime();
      Native.forClass( "org.apache.jena.ext.xerces.impl.xpath.regex.Token$UnionToken" )
            .initializeAtBuildTime();
      Native.forClass( org.apache.jena.ext.xerces.util.XercesXMLChar.class )
            .initializeAtBuildTime();

      Native.addResource( "META-INF/services/org.apache.jena.sys.JenaSubsystemLifecycle" );
      Native.addResource( "org/apache/jena/arq/arq-properties.xml" );
      Native.addResource( "org/apache/jena/jena-properties.xml" );

      Native.addResourceBundle( "org.apache.jena.ext.xerces.impl.xpath.regex.message" );
      Native.addResourceBundle( "org.apache.jena.ext.xerces.impl.msg.XMLSchemaMessages" );
   }
}
