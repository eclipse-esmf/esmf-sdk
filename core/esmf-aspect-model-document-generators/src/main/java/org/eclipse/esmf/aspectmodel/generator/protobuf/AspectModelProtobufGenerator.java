/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

package org.eclipse.esmf.aspectmodel.generator.protobuf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.HasProperties;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Trait;

/**
 * Generates a self-contained proto3 message schema for an Aspect's payload.
 */
public class AspectModelProtobufGenerator extends AspectGenerator<String, String, ProtobufGenerationConfig, ProtobufArtifact> {
   public static final ProtobufGenerationConfig DEFAULT_CONFIG = ProtobufGenerationConfigBuilder.builder().build();

   /**
    * Creates a generator for the given Aspect.
    *
    * @param aspect the Aspect whose payload is described
    */
   public AspectModelProtobufGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   /**
    * Creates a generator with the given configuration.
    *
    * @param aspect the Aspect whose payload is described
    * @param config the generation configuration
    */
   public AspectModelProtobufGenerator( final Aspect aspect, final ProtobufGenerationConfig config ) {
      super( aspect, config );
   }

   /**
    * Generates the schema with deterministic field numbers in model property order.
    *
    * @return a stream containing the schema artifact
    */
   @Override
   public Stream<ProtobufArtifact> generate() {
      final Schema schema = new Schema( aspect().urn() );
      schema.message( aspect() );
      final String content = "syntax = \"proto3\";\n\npackage " + schema.packageName + ";\n\n"
            + String.join( "\n", schema.messages.values() );
      return Stream.of( new ProtobufArtifact( aspect().getName() + ".proto", content ) );
   }

   private static class Schema {
      private final String packageName;
      private final Map<Object, String> names = new HashMap<>();
      private final Set<String> usedNames = new HashSet<>();
      private final Map<String, String> messages = new LinkedHashMap<>();

      private Schema( final AspectModelUrn aspectUrn ) {
         final String namespace = Stream.of( aspectUrn.getNamespaceMainPart().split( "\\." ) )
               .map( this::identifier ).collect( Collectors.joining( "." ) );
         // Keep the full version in one valid package segment, e.g. 1.2.3 becomes v1_2_3.
         packageName = namespace + "." + identifier( "v" + aspectUrn.getVersion() );
      }

      private String qualifiedName( final String name ) {
         // Absolute names avoid shadowing by field names and include the package of all emitted messages.
         // See https://protobuf.dev/programming-guides/proto3/#name-resolution
         return "." + packageName + "." + name;
      }

      private String allocate( final Object key, final String suggested ) {
         final String base = identifier( suggested );
         String name = base;
         int suffix = 2;
         while ( !usedNames.add( name ) ) {
            name = base + suffix++;
         }
         names.put( key, name );
         messages.put( name, "" );
         return name;
      }

      private String message( final HasProperties element ) {
         if ( names.containsKey( element ) ) {
            return qualifiedName( names.get( element ) );
         }
         // Register before descending so recursive and shared Entities reference one definition.
         final String name = allocate( element, element.getName() );
         final List<Property> properties = element instanceof ComplexType complex ? complex.getAllProperties() : element.getProperties();
         final StringBuilder body = new StringBuilder( "message " + name + " {\n" );
         final Set<String> fields = new HashSet<>();
         int number = 1;
         for ( final Property property : properties ) {
            if ( property.isNotInPayload() || property.isAbstract() ) {
               continue;
            }
            final String field = identifier( property.getPayloadName() );
            if ( !fields.add( field ) ) {
               throw new IllegalArgumentException( "Duplicate Protobuf field name: " + field );
            }
            final Characteristic characteristic = property.getEffectiveCharacteristic().orElseThrow();
            final boolean repeated = characteristic instanceof Collection;
            final String type = repeated ? collectionType( (Collection) characteristic ) : type( characteristic );
            final String cardinality = repeated ? "repeated " : property.isOptional() ? "optional " : "";
            // Skip field numbers 19000-19999, which are reserved for the Protobuf implementation.
            // See https://protobuf.dev/programming-guides/proto3/#assigning
            if ( number == 19000 ) {
               number = 20000;
            }
            body.append( "  " ).append( cardinality ).append( type ).append( " " ).append( field )
                  .append( " = " ).append( number++ ).append( ";\n" );
         }
         messages.put( name, body.append( "}\n" ).toString() );
         return qualifiedName( name );
      }

      private String collectionType( final Collection collection ) {
         return collection.getElementCharacteristic().map( this::type )
               .orElseGet( () -> dataType( collection.getDataType().orElseThrow() ) );
      }

      private String type( final Characteristic characteristic ) {
         if ( characteristic instanceof Trait trait ) {
            return type( trait.getBaseCharacteristic() );
         }
         if ( characteristic instanceof Either || characteristic instanceof Collection ) {
            if ( names.containsKey( characteristic ) ) {
               return qualifiedName( names.get( characteristic ) );
            }
            final String name = allocate( characteristic, characteristic.getName() );
            final String body;
            if ( characteristic instanceof Either either ) {
               // oneof cannot contain repeated fields; collection alternatives use wrapper messages.
               body = "  oneof value {\n    " + type( either.getLeft() ) + " left = 1;\n    "
                     + type( either.getRight() ) + " right = 2;\n  }\n";
            } else {
               body = "  repeated " + collectionType( (Collection) characteristic ) + " values = 1;\n";
            }
            messages.put( name, "message " + name + " {\n" + body + "}\n" );
            return qualifiedName( name );
         }
         return dataType( characteristic.getDataType().orElseThrow() );
      }

      private String dataType( final Type type ) {
         if ( type instanceof ComplexType complex ) {
            return message( complex );
         }
         final String uri = type.getUrn();
         if ( !uri.startsWith( "http://www.w3.org/2001/XMLSchema#" ) ) {
            return "string";
         }
         // Unbounded integers and decimals use strings to preserve precision and range.
         return switch ( uri.substring( uri.indexOf( '#' ) + 1 ) ) {
            case "boolean" -> "bool";
            case "byte", "short", "int" -> "int32";
            case "long" -> "int64";
            case "unsignedByte", "unsignedShort", "unsignedInt" -> "uint32";
            case "unsignedLong" -> "uint64";
            case "float" -> "float";
            case "double" -> "double";
            case "base64Binary", "hexBinary" -> "bytes";
            default -> "string";
         };
      }

      private String identifier( final String value ) {
         final String name = value.replaceAll( "[^A-Za-z0-9_]", "_" );
         return name.matches( "[A-Za-z][A-Za-z0-9_]*" ) && !Set.of(
               "syntax", "message", "enum", "service", "rpc", "returns", "stream", "import", "public", "weak",
               "package", "option", "optional", "required", "repeated", "oneof", "map", "reserved", "extensions",
               "to", "max", "group", "extend", "true", "false", "double", "float", "int32", "int64", "uint32",
               "uint64", "sint32", "sint64", "fixed32", "fixed64", "sfixed32", "sfixed64", "bool", "string", "bytes"
         ).contains( name ) ? name : "samm_" + name;
      }
   }
}
