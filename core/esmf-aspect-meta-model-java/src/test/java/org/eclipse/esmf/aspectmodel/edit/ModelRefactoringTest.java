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

package org.eclipse.esmf.aspectmodel.edit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;

public class ModelRefactoringTest {
   @Test
   void testRenameElement() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Aspect aspect = aspectModel.aspect();

      final AspectModelUrn aspectUrn = aspect.urn();
      final String originalName = aspect.getName();
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( originalName );

      final String newName = "RenamedAspect";
      final ModelRefactoring ctx = new ModelRefactoring( aspectModel );
      final Change renameAspect = ctx.new RenameElement( aspectUrn, newName );
      ctx.applyChange( renameAspect );
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( newName );
   }

   @Test
   void testUndoRedo() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Aspect aspect = aspectModel.aspect();

      final AspectModelUrn aspectUrn = aspect.urn();
      final String originalName = aspect.getName();
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( originalName );

      final String newName = "RenamedAspect";
      final ModelRefactoring ctx = new ModelRefactoring( aspectModel );
      final Change renameAspect = ctx.new RenameElement( aspectUrn, newName );
      ctx.applyChange( renameAspect );
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( newName );
      ctx.undoChange();
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( originalName );
      ctx.redoChange();
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( newName );
   }

   @Test
   void testChangeGroups() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      final Aspect aspect = aspectModel.aspect();

      final AspectModelUrn aspectUrn = aspect.urn();
      final String oldAspectName = aspect.getName();
      final Property property = aspect.getProperties().get( 0 );
      final String oldPropertyName = property.getName();

      final String newAspectName = "RenamedAspect";
      final String newPropertyName = "renamedProperty";
      final ModelRefactoring ctx = new ModelRefactoring( aspectModel );
      final Change renameAspect = ctx.new RenameElement( aspectUrn, newAspectName );
      final Change renameProperty = ctx.new RenameElement( property.urn(), newPropertyName );

      final Change group = ctx.new ChangeGroup( List.of( renameAspect, renameProperty ) );
      ctx.applyChange( group );
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( newAspectName );
      assertThat( aspectModel.aspect().getProperties().get( 0 ).getName() ).isEqualTo( newPropertyName );
   }
}
