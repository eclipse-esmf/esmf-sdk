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

package org.eclipse.esmf.aspectmodel.java;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.staticmetamodel.StaticMetaClass;
import org.eclipse.esmf.staticmetamodel.StaticProperty;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.Statement;

public class StaticClassGenerationResult extends GenerationResult {

   public StaticClassGenerationResult( final File outputDirectory, final Map<QualifiedName, Class<?>> generatedClasses,
         final Map<QualifiedName, String> sources ) throws IOException {
      super( outputDirectory, generatedClasses, sources );
   }

   /**
    * Allows asserting the instantiation of the {@link MetaModelBaseAttributes}
    * with only the required attributes for the fields of a generated class. The expected attribute values have to be
    * given as a {@code Map} where the key is the field name and the value is a {@code Set} with the expected attribute
    * values.
    * Note, that ALL fields have to be passed for assertion, you can't assert only a subset.
    *
    * @param className the name of the generated Java class analyze
    * @param expectedBaseAttributeArguments the expected fields and the expected values for their {@link
    * MetaModelBaseAttributes}
    */
   public void assertMetaModelBaseAttributesForProperties( final String className,
         final Map<String, Set<String>> expectedBaseAttributeArguments ) {
      assertThat( compilationUnits ).containsKey( className );

      final List<FieldDeclaration> fields = compilationUnits.get( className ).findAll( FieldDeclaration.class );
      for ( final FieldDeclaration field : fields ) {
         final String fieldName = field.resolve().getName();
         if ( !expectedBaseAttributeArguments.containsKey( fieldName ) ) {
            continue;
         }
         final Set<String> expectedArguments = expectedBaseAttributeArguments.get( fieldName );
         final NodeList<VariableDeclarator> declarators = field.getVariables();
         assertThat( declarators ).hasSize( 1 );

         final VariableDeclarator declarator = declarators.get( 0 );
         final Expression metaModelBaseAttributesDeclarationExpression = declarator.getInitializer().get()
               .asObjectCreationExpr()
               .getArguments().get( 0 );
         for ( final String argument : expectedArguments ) {
            assertThat( metaModelBaseAttributesDeclarationExpression.toString() ).contains( argument );
         }
      }
   }

   /**
    * Allows asserting a constructor argument for a {@link StaticProperty},
    * e.g. the instantiation of the {@link MetaModelBaseAttributes},
    * for the
    * fields of a generated class. The expected attribute values have to be given as a {@code Map} where the key is the
    * field name and the value is a {@code Set} with the expected attribute values.
    * Note, that ALL fields have to be passed for assertion, you can't assert only a subset.
    *
    * @param className the name of the generated Java class analyze
    * @param expectedConstructorArgument the expected fields and the expected values for their {@link MetaModelBaseAttributes}
    * @param constructorArgumentIndex the index of the constructor argument to be asserted
    */
   public void assertConstructorArgumentForProperties( final String className,
         final Map<String, String> expectedConstructorArgument, final int constructorArgumentIndex ) {
      assertThat( compilationUnits ).containsKey( className );

      final List<FieldDeclaration> fields = compilationUnits.get( className ).findAll( FieldDeclaration.class );

      fields.stream()
            .filter( field -> expectedConstructorArgument.containsKey( field.resolve().getName() ) )
            .forEach( field -> {
               final String fieldName = field.resolve().getName();
               final String expectedBuilderCall = expectedConstructorArgument.get( fieldName )
                     .replace( "\r", "" )
                     .replace( "\n", "" );
               final NodeList<VariableDeclarator> declarators = field.getVariables();
               assertThat( declarators ).hasSize( 1 );

               final VariableDeclarator declarator = declarators.get( 0 );
               final Expression metaModelBaseAttributesDeclarationExpression = declarator.getInitializer().get()
                     .asObjectCreationExpr()
                     .getArguments()
                     .get( constructorArgumentIndex );
               final String actualBuilderCall = metaModelBaseAttributesDeclarationExpression.toString()
                     .replace( "\r", "" )
                     .replace( "\n", "" );
               assertThat( actualBuilderCall ).isEqualTo( expectedBuilderCall );
            } );
   }

   /**
    * Allows the assertion of the methods implemented in the {@link StaticMetaClass}.
    * The expected methods and their corresponding bodies are given in a {@code Map} where the key is the name
    * of the expected method and the value is a {@code String} representation of its body.
    *
    * @param className the name of the generated Java class analyze
    * @param expectedMethodBodies the expected methods and their corresponding bodies
    */
   public void assertMethods( final String className, final Map<String, String> expectedMethodBodies ) {
      assertThat( compilationUnits ).containsKey( className );

      final List<MethodDeclaration> methodDeclarations = compilationUnits.get( className )
            .findAll( MethodDeclaration.class );

      assertThat( methodDeclarations ).hasSize( expectedMethodBodies.size() );

      assertThat( methodDeclarations ).allSatisfy( methodDeclaration -> {
         assertThat( expectedMethodBodies ).containsKey( methodDeclaration.getName().getIdentifier() );
         final String expectedBody = expectedMethodBodies.get( methodDeclaration.getName().getIdentifier() )
               .replace( "\r", "" ).replace( "\n", "" );

         final NodeList<Statement> actualStatements = methodDeclaration.getBody().get().getStatements();
         final String actualBody = actualStatements.stream().map( Node::toString ).collect( Collectors.joining( " " ) )
               .replace( "\r", "" ).replace( "\n", "" );
         assertThat( actualBody ).isEqualTo( expectedBody );
      } );
   }

   /**
    * Allows the assertion of method bodies for static meta properties in a generated meta class.
    *
    * Method bodies are asserted in a relaxed way:
    * <ul>
    *    <li>all whitespace is trimmed before comparing</li>
    *    <li>the given method body may also be just a part of the full method body, i.e. the check is performed using .contains()</li>
    * </ul>
    *
    * @param className the name of the meta class
    * @param propertyName the name of the property (given in CONSTANT_CASE)
    * @param expectedMethodBodies the expected (partial) contents of the static meta property methods, where the key is the method name
    */
   public void assertStaticMetaPropertyMethods( final String className, final String propertyName,
         final Map<String, String> expectedMethodBodies ) {
      assertThat( compilationUnits ).containsKey( className );

      final List<FieldDeclaration> fields = compilationUnits.get( className ).findAll( FieldDeclaration.class );
      for ( final FieldDeclaration field : fields ) {
         final String fieldName = field.resolve().getName();
         if ( !propertyName.equals( fieldName ) ) {
            continue;
         }

         assertThat( field.getVariables()
               .stream()
               .map( VariableDeclarator::getInitializer )
               .flatMap( Optional::stream )
               .filter( exp -> exp instanceof ObjectCreationExpr )
               .map( ObjectCreationExpr.class::cast )
               .flatMap(
                     oc -> oc.getChildNodes().stream().filter( n -> n instanceof MethodDeclaration ).map( MethodDeclaration.class::cast ) )
               .anyMatch( md -> md.getBody()
                     .map( b -> b.getStatements().stream().anyMatch(
                           s -> expectedMethodBodies.containsKey( md.getName().toString() ) && s.toString().trim()
                                 .contains( expectedMethodBodies.get( md.getName().toString() ).trim() ) ) )
                     .orElse( false ) ) ).isTrue();
      }
   }
}
