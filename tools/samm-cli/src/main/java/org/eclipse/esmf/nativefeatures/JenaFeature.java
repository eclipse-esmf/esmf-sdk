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

import static org.graalvm.nativeimage.hosted.RuntimeClassInitialization.initializeAtBuildTime;

/**
 * Configuration of eagerly initialized Jena classes
 */
public class JenaFeature extends AbstractSammCliFeature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.Constants" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLEntityManager" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLEntityManager$EncodingInfo" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLEntityScanner" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLScanner" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.XMLVersionDetector" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.util.FeatureState" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.util.PropertyState" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.util.XMLChar" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.util.XMLSymbols" ) );
      initializeAtBuildTime( getClass( "com.sun.org.apache.xerces.internal.xni.NamespaceContext" ) );
      initializeAtBuildTime( getClass( "com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator" ) );
      initializeAtBuildTime( getClass( "javax.xml.parsers.FactoryFinder" ) );
      initializeAtBuildTime( getClass( "jdk.xml.internal.JdkXmlUtils" ) );
      initializeAtBuildTime( getClass( "jdk.xml.internal.SecuritySupport" ) );
      initializeAtBuildTime( org.apache.jena.ext.xerces.impl.dv.xs.AnyURIDV.class );
      initializeAtBuildTime( org.apache.jena.ext.xerces.impl.dv.xs.BaseSchemaDVFactory.class );
      initializeAtBuildTime( org.apache.jena.ext.xerces.impl.dv.xs.ExtendedSchemaDVFactoryImpl.class );
      initializeAtBuildTime( org.apache.jena.ext.xerces.impl.dv.xs.QNameDV.class );
      initializeAtBuildTime( org.apache.jena.ext.xerces.impl.dv.xs.SchemaDVFactoryImpl.class );
      initializeAtBuildTime( org.apache.jena.ext.xerces.impl.dv.xs.XSSimpleTypeDecl.class );
      initializeAtBuildTime( org.apache.jena.ext.xerces.impl.dv.xs.XSSimpleTypeDecl.class );
      initializeAtBuildTime( getClass( "org.apache.jena.ext.xerces.impl.xpath.regex.Token$CharToken" ) );
      initializeAtBuildTime( getClass( "org.apache.jena.ext.xerces.impl.xpath.regex.Token$ClosureToken" ) );
      initializeAtBuildTime( getClass( "org.apache.jena.ext.xerces.impl.xpath.regex.Token$ParenToken" ) );
      initializeAtBuildTime( getClass( "org.apache.jena.ext.xerces.impl.xpath.regex.Token$UnionToken" ) );
      initializeAtBuildTime( org.apache.jena.ext.xerces.util.XercesXMLChar.class );
   }
}
