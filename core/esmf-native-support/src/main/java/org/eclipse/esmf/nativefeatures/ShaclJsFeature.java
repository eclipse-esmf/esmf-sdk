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

import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsBlankNode;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsFactory;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsGraph;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsLiteral;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsNamedNode;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsTerm;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.JsTriple;
import org.eclipse.esmf.aspectmodel.shacl.constraint.js.TermFactory;

import org.graalvm.nativeimage.hosted.Feature;

/**
 * Sets up classes for native image compilation that are required for evaluation of SHACL JS Constraints in the Aspect Model Validator.
 */
public class ShaclJsFeature implements Feature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      Native.forClass( JsBlankNode.class )
            .registerEverythingForReflection();
      Native.forClass( JsFactory.class )
            .registerEverythingForReflection();
      Native.forClass( JsGraph.class )
            .registerEverythingForReflection();
      Native.forClass( JsGraph.JsTripleIterator.class )
            .registerEverythingForReflection();
      Native.forClass( JsLiteral.class )
            .registerEverythingForReflection();
      Native.forClass( JsNamedNode.class )
            .registerEverythingForReflection();
      Native.forClass( JsTerm.class )
            .registerEverythingForReflection();
      Native.forClass( JsTriple.class )
            .registerEverythingForReflection();
      Native.forClass( TermFactory.class )
            .registerEverythingForReflection();
   }
}
