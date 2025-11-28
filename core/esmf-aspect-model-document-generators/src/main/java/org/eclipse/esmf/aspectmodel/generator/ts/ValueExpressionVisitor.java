/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.ts;

import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.exception.CodeGenerationException;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

/**
 * Builds an initializer expression for a {@link Value}. For example:
 * <ul>
 *    <li>If the value is (int) 3, it will return "3"</li>
 *    <li>If the value is (String) "hi", it will return "\"hi\""</li>
 *    <li>If the value is (LangString) "hi"@en, it will return "{ value: 'hi', language: '@en' }"</li>
 *    <li>If the value is a collection, it will return the corresponding collection, not implemented"
 *    </li>
 *    <li>If the value is an Entity, it will return the corresponding constructor call, e.g. "new MyEntity(\"foo\", 2, 3)"</li>
 * </ul>
 */
public class ValueExpressionVisitor implements AspectVisitor<String, ValueExpressionVisitor.Context> {
   private final ValueInitializer valueInitializer = new ValueInitializer();

   public record Context(
         TsCodeGenerationConfig codeGenerationConfig,
         boolean isOptional
   ) {}

   @Override
   public String visitBase( final ModelElement modelElement, final Context context ) {
      throw new UnsupportedOperationException( "Can't create value expression for model element: " + modelElement );
   }

   @Override
   public String visitScalarValue( final ScalarValue value, final Context context ) {
      return generateValueExpression( value, context );
   }

   private String generateValueExpression( final ScalarValue value, final Context context ) {
      final String typeUri = value.getType().as( Scalar.class ).getUrn();
      if ( typeUri.equals( RDF.langString.getURI() ) ) {
         context.codeGenerationConfig().importTracker()
               .importLibExplicit( "MultiLanguageText", "instantiator/characteristic/characteristic-instantiator-util" );
         final LangString langStringValue = (LangString) value.as( ScalarValue.class ).getValue();
         return String.format( "{ value: '%s', language: '%s' }",
               AspectModelTsUtil.escapeForLiteral( langStringValue.getValue() ),
               langStringValue.getLanguageTag().toLanguageTag() );
      }
      final Resource typeResource = ResourceFactory.createResource( typeUri );
      final Class<?> javaType = SammXsdType.getJavaTypeForMetaModelType( typeResource );
      final String valueExpression = AspectModelTsUtil.createLiteral( value.getValue().toString() );
      return valueInitializer.apply( typeResource, javaType, valueExpression );
   }

   @Override
   public String visitCollectionValue( final CollectionValue collection, final Context context ) {
      final Class<?> collectionClass = collection.getValues().getClass();
      // Phase 2: Implement collection initializers
      final StringBuilder result = new StringBuilder();
      result.append( "undefined" );
//      result.append( "new " );
//      result.append( collectionClass.getSimpleName() );
//      result.append( "<>() {{" );
//      collection.getValues().forEach( value -> {
//         result.append( "add(" );
//         result.append( value.accept( this, context ) );
//         result.append( ");" );
//      } );
//      result.append( "}}" );
      return result.toString();
   }

   @Override
   public String visitEntityInstance( final EntityInstance instance, final Context context ) {
      final Entity entity = instance.getType().as( Entity.class );
      return "new " + entity.getName() + entity.getProperties().stream().sequential().map( property -> {
         final Value value = instance.getAssertions().get( property );
         if ( value == null ) {
            if ( property.isOptional() ) {
               return "null";
            }
            throw new CodeGenerationException(
                  "EntityInstance " + instance.getName() + " is missing value for Property " + property.getName() );
         }
         final Context newContext = new Context( context.codeGenerationConfig, property.isOptional() );
         return value.accept( this, newContext );
      } ).collect( Collectors.joining( ",", "(", ")" ) );
   }
}
