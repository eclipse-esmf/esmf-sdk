/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.java;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.assertj.core.api.Condition;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.util.Types;

class GenerationResult {

   private final CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
   private final long numFailedCompilationUnits;
   private final Collection<Problem> parseProblems;
   private final Map<QualifiedName, Class<?>> generatedClasses;

   final Map<String, CompilationUnit> compilationUnits;

   GenerationResult( final File outputDirectory, final Map<QualifiedName, Class<?>> generatedClasses ) throws IOException {
      combinedTypeSolver.add( new ReflectionTypeSolver() );
      combinedTypeSolver.add( new JavaParserTypeSolver( outputDirectory ) );
      combinedTypeSolver.add( new ClassLoaderTypeSolver( getClass().getClassLoader() ) );
      final JavaSymbolSolver symbolSolver = new JavaSymbolSolver( combinedTypeSolver );

      final List<ParseResult<CompilationUnit>> parseResults = new SourceRoot( outputDirectory.toPath() )
            .setParserConfiguration( new ParserConfiguration().setSymbolResolver( symbolSolver ) )
            .tryToParse();
      numFailedCompilationUnits = parseResults.stream()
            .filter( result -> !result.isSuccessful() )
            .count();
      parseProblems = parseResults.stream()
            .filter( result -> !result.isSuccessful() )
            .flatMap( result -> result.getProblems().stream() )
            .collect( Collectors.toList() );
      compilationUnits = parseResults
            .stream()
            .filter( ParseResult::isSuccessful )
            .map( ParseResult::getResult )
            .map( Optional::get )
            .collect( Collectors.toMap( unit -> unit.getPrimaryTypeName().get(), Function.identity() ) );

      this.generatedClasses = generatedClasses;
   }

   void assertNumberOfFiles( final int expectedNumberOfFiles ) {
      final long numAllFiles = numFailedCompilationUnits + compilationUnits.size();
      assertThat( numAllFiles ).isEqualTo( expectedNumberOfFiles );
      assertThat( compilationUnits )
            .withFailMessage( numAllFiles + " file(s) found, but " + numFailedCompilationUnits + " could not be parsed due to "
                  + parseProblems )
            .hasSize( expectedNumberOfFiles );
   }

   /**
    * Allows asserting the fields of a generated class. Expected field definitions have to be given as a {@code Map}
    * where the key is the field name and the value is either the expected type or the type name (type names have to
    * be used for types that are part of the generation process).
    * Note, that ALL fields have to be passed for assertion, you can't assert only a subset.
    *
    * @param className the name of the generated Java class analyze
    * @param fieldTypesOrTypeNames the expected fields and their types or type names
    * @param annotations the expected annotations concatenated as one string, indexed by field name
    */
   void assertFields( final String className, final ImmutableMap<String, Object> fieldTypesOrTypeNames, final Map<String, String> annotations ) {
      assertThat( compilationUnits ).containsKey( className );

      final List<FieldDeclaration> fields = compilationUnits.get( className ).findAll( FieldDeclaration.class );
      assertThat( fields ).hasSize( fieldTypesOrTypeNames.size() );
      assertThat( fields ).extracting( field -> field.resolve().getName() ).containsExactlyInAnyOrderElementsOf( fieldTypesOrTypeNames.keySet() );
      assertThat( fields )
            .allSatisfy(
                  field -> {
                     final Object fieldTypeOrTypeName = fieldTypesOrTypeNames.get( field.resolve().getName() );
                     if ( fieldTypeOrTypeName instanceof String ) {
                        assertThat( field.getElementType().asString() ).isEqualTo( fieldTypeOrTypeName );
                     } else if ( fieldTypeOrTypeName instanceof ResolvedType ) {
                        assertThat( field.resolve().getType() ).isEqualTo( fieldTypeOrTypeName );
                     } else {
                        final TypeToken<?> actual = typeTokenForField( field );
                        final TypeToken expected = typeTokenToCheck( fieldTypeOrTypeName );
                        assertThat( actual ).is( anyOf( new Condition<TypeToken>( expected::equals, "fully equal" ),
                              new Condition<>( token -> token.toString().equals( expected.toString() ), expected.toString() ) ) );
                     }
                  } );

      if ( !annotations.isEmpty() ) {
         assertThat( fields ).allSatisfy( field ->
               assertThat( annotations.get( field.resolve().getName() ) ).isEqualTo(
                     field.getAnnotations().stream().map( Node::toString ).collect( Collectors.joining() ) ) );
      }
   }

   void assertCollectionElementValidationAnnotations( final String className, final String fieldName, final String expectedFieldDeclaration ) {
      final List<FieldDeclaration> fields = compilationUnits.get( className ).findAll( FieldDeclaration.class );
      fields.stream().filter( fieldDeclaration -> fieldDeclaration.resolve().getName().equals( fieldName ) )
            .forEach( fieldDeclaration -> {
               final String actualFieldDeclaration = fieldDeclaration.toString().replaceAll( "\\s", "" );
               assertThat( actualFieldDeclaration ).isEqualTo( expectedFieldDeclaration.replaceAll( "\\s", "" ) );
            } );
   }

   void assertConstructor( final String className, final ImmutableMap<String, Object> fieldTypesOrTypeNames,
         final Map<String, String> expectedParameterAnnotations ) {
      final Optional<ConstructorDeclaration> constructor = compilationUnits.get( className ).findFirst( ConstructorDeclaration.class );
      assertThat( constructor ).isPresent();

      final NodeList<Parameter> parameters = constructor.get().getParameters();
      assertConstructorParameters( parameters, fieldTypesOrTypeNames, expectedParameterAnnotations );
   }

   void assertConstructorParameters( final NodeList<Parameter> parameters, final ImmutableMap<String, Object> fieldTypesOrTypeNames,
         final Map<String, String> expectedParameterAnnotations ) {
      assertThat( parameters ).hasSize( fieldTypesOrTypeNames.size() );

      if ( expectedParameterAnnotations.size() > 0 ) {
         assertThat( parameters ).allSatisfy( parameter ->
               assertThat( expectedParameterAnnotations.get( parameter.resolve().getName() ) ).isEqualTo(
                     parameter.getAnnotations().stream().map( Node::toString ).collect( Collectors.joining() ) ) );
         return;
      }
      assertThat( parameters ).allSatisfy( parameter -> assertThat( parameter.getAnnotations() ).isEmpty() );
   }

   void assertConstructorBody( final String className, final Set<String> expectedbody ) {
      final Optional<ConstructorDeclaration> constructor = compilationUnits.get( className ).findFirst( ConstructorDeclaration.class );
      assertThat( constructor ).isPresent();

      final NodeList<Statement> bodyStatements = constructor.get().getBody().getStatements();
      assertThat( bodyStatements ).hasSize( expectedbody.size() );

      assertThat( expectedbody.stream().collect( Collectors.joining() ) ).contains(
            bodyStatements.stream().map( Node::toString ).collect( Collectors.joining() ) );
   }

   private TypeToken typeTokenToCheck( final Object fieldTypeOrTypeName ) {
      TypeToken typeToCheck = null;
      if ( fieldTypeOrTypeName instanceof TypeToken ) {
         typeToCheck = ((TypeToken) fieldTypeOrTypeName);
      } else {
         typeToCheck = TypeToken.of( (Type) fieldTypeOrTypeName );
      }
      return typeToCheck;
   }

   private TypeToken<?> typeTokenForField( final FieldDeclaration field ) {
      return TypeToken.of( resolveRecursive( TypeResolution.toResolve( field.resolve().getType().asReferenceType() ) ).getResolved() );
   }

   private TypeResolution resolveRecursive( final TypeResolution toResolve ) {
      Type rawType;
      try {
         rawType = Class.forName( toResolve.getClassNameToResolve() );
      } catch ( final ClassNotFoundException e ) {
         // No class loadable using default class loader, try to find it in generated classes
         rawType = generatedClasses.entrySet().stream().filter( entry ->
                     entry.getKey().toString().equals( toResolve.getClassNameToResolve() ) )
               .findAny().map( Map.Entry::getValue ).orElseThrow( () -> new RuntimeException( e ) );
      }
      if ( !toResolve.getToResolve().isReferenceType() || ((ResolvedReferenceType) toResolve.getToResolve())
            .getTypeParametersMap().isEmpty() ) {
         return TypeResolution.resolved( rawType );
      }
      final Type[] typeParameters = ((ResolvedReferenceType) toResolve.getToResolve())
            .getTypeParametersMap()
            .stream()
            .map( pair -> pair.b )
            .map( TypeResolution::toResolve )
            .map( this::resolveRecursive )
            .map( TypeResolution::getResolved )
            .toArray( Type[]::new );

      return TypeResolution.resolved( Types.newParameterizedType( rawType, typeParameters ) );
   }

   /**
    * Allows asserting the constants of an {@code Enumeration}.
    *
    * @param className the name of the {@code Enumeration} to be tested
    * @param expectedConstants a list of the expected constants
    * @param expectedConstantArguments a {@code Map} containing the {@code String} representation of the argument
    *       for a constant. The key is the name of the constant. If this map is given, the number of entries should
    *       match the number of expected constants.
    */
   void assertEnumConstants( final String className, final Collection<String> expectedConstants, final Map<String, String> expectedConstantArguments ) {
      assertThat( compilationUnits ).containsKey( className );
      assertThat( compilationUnits.get( className ).getPrimaryType() ).hasValueSatisfying( type -> {
         assertThat( type.isEnumDeclaration() ).isTrue();
      } );

      final List<EnumConstantDeclaration> constants = compilationUnits.get( className ).findAll( EnumConstantDeclaration.class );
      assertThat( constants ).hasSize( expectedConstants.size() );
      assertThat( constants ).extracting( EnumConstantDeclaration::getName )
            .extracting( SimpleName::asString )
            .containsExactlyInAnyOrderElementsOf( expectedConstants );

      if ( expectedConstantArguments.size() > 0 ) {
         assertThat( constants ).allSatisfy( enumConstantDeclaration -> {
            assertThat( enumConstantDeclaration.getArguments() ).hasSize( 1 );
            final String name = enumConstantDeclaration.getName().asString();
            final String expectedArgument = expectedConstantArguments.get( name ).replaceAll( "\\s", "" )
                  .replace( "\\r", "" )
                  .replace( "\\n", "" );
            final String actualArgument = enumConstantDeclaration.getArgument( 0 ).toString().replaceAll( "\\s", "" )
                  .replace( "\\r", "" ).replace( "\\n", "" );
            assertThat( actualArgument ).isEqualTo( expectedArgument );
         } );
      }
   }

   void assertClassDeclaration( final String className, final List<Modifier> expectedModifiers, final List<String> expectedExtendedTypes,
         final List<String> expectedImplementedTypes, final List<String> expectedAnnotations ) {
      assertThat( compilationUnits.get( className ).getPrimaryType() ).hasValueSatisfying( typeDeclaration -> {
         assertThat( typeDeclaration.isClassOrInterfaceDeclaration() ).isTrue();
         assertThat( typeDeclaration.getModifiers() ).containsAll( expectedModifiers );

         final NodeList<AnnotationExpr> annotations = typeDeclaration.getAnnotations();
         assertThat( annotations ).hasSize( expectedAnnotations.size() );
         annotations.forEach( annotationExpr -> {
            assertThat( expectedAnnotations ).contains( annotationExpr.toString() );
         } );

         final ClassOrInterfaceDeclaration classOrInterfaceDeclaration = typeDeclaration.asClassOrInterfaceDeclaration();

         assertThat( classOrInterfaceDeclaration.getExtendedTypes() ).hasSize( expectedExtendedTypes.size() );
         classOrInterfaceDeclaration.getExtendedTypes().stream().map( ClassOrInterfaceType::getName ).forEach(
               extendedClassSimpleName -> assertThat( expectedExtendedTypes ).contains( extendedClassSimpleName.asString() ) );

         final NodeList<ClassOrInterfaceType> implementedTypes = classOrInterfaceDeclaration.getImplementedTypes();
         assertThat( implementedTypes ).hasSize( expectedImplementedTypes.size() );
      } );
   }

   void assertNamespace( final String className, final String javaPackageName ) {
      assertThat( compilationUnits ).containsKey( className );
      assertThat( compilationUnits.get( className ).getPackageDeclaration().get().getName().toString() ).isEqualTo( javaPackageName );
   }

   private static class TypeResolution {
      private final Type resolved;

      private final ResolvedType toResolve;

      private TypeResolution( final Type resolved, final ResolvedType toResolve ) {
         this.resolved = resolved;
         this.toResolve = toResolve;
      }

      static TypeResolution resolved( final Type type ) {
         return new TypeResolution( type, null );
      }

      static TypeResolution toResolve( final ResolvedType resolvedType ) {
         return new TypeResolution( null, resolvedType );
      }

      String getClassNameToResolve() {
         if ( toResolve.isArray() ) {
            if ( toResolve.asArrayType().getComponentType().isReferenceType() ) {
               return "[L" + toResolve.asArrayType().getComponentType().asReferenceType().getQualifiedName() + ";";
            }
            switch ( toResolve.asArrayType().getComponentType().asPrimitive() ) {
            case BOOLEAN:
               return "[Z";
            case BYTE:
               return "[B";
            case CHAR:
               return "[C";
            case DOUBLE:
               return "[D";
            case FLOAT:
               return "[F";
            case INT:
               return "[I";
            case LONG:
               return "[J";
            case SHORT:
               return "[S";
            }
         }
         final String fullName = toResolve.describe();
         return fullName.contains( "<" ) ? fullName.substring( 0, fullName.indexOf( "<" ) ) : fullName;
      }

      Type getResolved() {
         return resolved;
      }

      ResolvedType getToResolve() {
         return toResolve;
      }
   }
}
