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

package org.eclipse.esmf.aspectmodel.edit.change;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.Change;
import org.eclipse.esmf.aspectmodel.edit.ChangeContext;
import org.eclipse.esmf.aspectmodel.edit.ModelChangeException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;

public abstract class StructuralChange extends AbstractChange implements Change {
   protected AspectModelFile sourceFile( final ChangeContext changeContext, final AspectModelUrn elementUrn ) {
      return changeContext.aspectModel().files().stream().filter( aspectModelFile ->
                  aspectModelFile.sourceModel()
                        .contains( aspectModelFile.sourceModel().createResource( elementUrn.toString() ), RDF.type, (RDFNode) null ) )
            .findFirst()
            .orElseThrow( () -> new ModelChangeException( "Could not locate file containing definition of " + elementUrn ) );
   }
}
