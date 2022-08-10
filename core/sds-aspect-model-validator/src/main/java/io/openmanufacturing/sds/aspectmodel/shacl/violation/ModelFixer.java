/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.shacl.violation;

import java.util.List;
import java.util.function.BiFunction;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import io.openmanufacturing.sds.aspectmodel.shacl.fix.Fix;
import io.openmanufacturing.sds.aspectmodel.shacl.fix.ReplaceValue;

public class ModelFixer implements BiFunction<Model, List<Fix>, Model> {
   @Override
   public Model apply( final Model model, final List<Fix> fixes ) {
      final FixVisitor visitor = new FixVisitor( model );
      final Model result = ModelFactory.createDefaultModel();
      result.add( model );
      for ( final Fix fix : fixes ) {
         final ModelChange fixResult = fix.accept( visitor );
         model.remove( fixResult.toRemove );
         model.add( fixResult.toAdd );
      }

      return result;
   }

   private record ModelChange(Model toAdd, Model toRemove) {
      ModelChange() {
         this( ModelFactory.createDefaultModel(), ModelFactory.createDefaultModel() );
      }
   }

   private static class FixVisitor implements Fix.Visitor<ModelChange> {
      private final Model oldModel;

      FixVisitor( final Model oldModel ) {
         this.oldModel = oldModel;
      }

      @Override
      public ModelChange visit( final Fix fix ) {
         return new ModelChange();
      }

      @Override
      public ModelChange visitReplaceValue( final ReplaceValue replaceValue ) {
         final Model toAdd = ModelFactory.createDefaultModel();
         final Model toRemove = ModelFactory.createDefaultModel();
         final EvaluationContext context = replaceValue.context();
         context.property().ifPresent( property -> {
            toRemove.add( replaceValue.context().element(), property, replaceValue.oldValue() );
            toAdd.add( replaceValue.context().element(), property, replaceValue.newValue() );
         } );
         return new ModelChange( toAdd, toRemove );
      }
   }
}
