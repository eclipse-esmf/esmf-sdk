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

public class ShaclJsFeature extends AbstractSammCliFeature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      register( JsBlankNode.class );
      register( JsFactory.class );
      register( JsGraph.class );
      register( JsGraph.JsTripleIterator.class );
      register( JsLiteral.class );
      register( JsNamedNode.class );
      register( JsTerm.class );
      register( JsTriple.class );
      register( TermFactory.class );
   }
}
