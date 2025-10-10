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

package org.eclipse.esmf.aspectmodel.importer.jsonschema;

import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.$ANCHOR;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.$COMMENT;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.$DEFS;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.$REF;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.ARRAY;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.BOOLEAN;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.DESCRIPTION;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.ENUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.EXAMPLES;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.EXCLUSIVE_MAXIMUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.EXCLUSIVE_MINIMUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.FORMAT;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.INTEGER;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.ITEMS;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.MAXIMUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.MAX_LENGTH;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.MINIMUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.MIN_LENGTH;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.NUMBER;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.OBJECT;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.PATTERN;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.PROPERTIES;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.REQUIRED;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.STRING;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.TITLE;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.TYPE;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaImporter.JsonSchemaVocabulary.UNIQUE_ITEMS;
import static org.eclipse.esmf.metamodel.DataTypes.xsd;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.characteristic;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.entity;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.enumeration;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.lengthConstraint;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.list;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.property;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.rangeConstraint;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.regularExpressionConstraint;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.singleEntity;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.sortedSet;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.trait;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.value;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.generator.Generator;
import org.eclipse.esmf.aspectmodel.importer.exception.AspectImportException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.builder.SammBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Streams;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for translators from JSON Schema to {@link StructureElement}s
 *
 * @param <T> the type of StructureElement, e.g., {@link Aspect} or {@link Entity}.
 * @param <A> the corresponding {@link Artifact} type
 */
@SuppressWarnings( { "NewClassNamingConvention", "checkstyle:ClassTypeParameterName", "checkstyle:MethodTypeParameterName" } )
public abstract class JsonSchemaImporter<T extends StructureElement, A extends Artifact<AspectModelUrn, T>> extends
      Generator<JsonNode, AspectModelUrn, T, AspectGenerationConfig, A> {
   private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaImporter.class );

   /**
    * Defined in RFC 5321
    */
   private static final String EMAIL_REGEX = "^([a-zA-Z0-9\\+\\._\\/&!][-a-zA-Z0-9\\+\\._\\/&!]*)@(([a-zA-Z0-9][-a-zA-Z0-9]*\\.)"
         + "([-a-zA-Z0-9]+\\.)*[a-zA-Z]{2,})$";

   /**
    * Defined in RFC 6531 and implemented in <a href="https://gist.github.com/baker-ling/3b4b014ee809aa9732f9873fe060c098">gist</a>
    */
   private static final String IDN_EMAIL_REGEX = "^(?<localPart>(?<dotString>[0-9a-zA-Z!#$%&'*+\\-\\/=?^_`\\{|\\}~\\u{80}-\\u{10FFFF}]+(\\"
         + ".[0-9a-zA-Z!#$%&'*+\\-\\/=?^_`\\{|\\}~\\u{80}-\\u{10FFFF}]+)*)|(?<quotedString>\""
         + "([\\x20-\\x21\\x23-\\x5B\\x5D-\\x7E\\u{80}-\\u{10FFFF}]|\\\\[\\x20-\\x7E])*\"))(?<!.{64,})@(?<domainOrAddressLiteral>"
         + "(?<addressLiteral>\\[((?<IPv4>\\d{1,3}(\\.\\d{1,3}){3})|(?<IPv6Full>IPv6:[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7})|"
         + "(?<IPv6Comp>IPv6:([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){0,5})?::([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){0,5})?)|"
         + "(?<IPv6v4Full>IPv6:[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){5}:\\d{1,3}(\\.\\d{1,3}){3})|(?<IPv6v4Comp>IPv6:([0-9a-fA-F]{1,4}"
         + "(:[0-9a-fA-F]{1,4}){0,3})?::([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){0,3}:)?\\d{1,3}(\\.\\d{1,3}){3})|"
         + "(?<generalAddressLiteral>[a-zA-Z0-9\\-]*[[a-zA-Z0-9]:[\\x21-\\x5A\\x5E-\\x7E]+))\\])|(?<Domain>(?!.{256,})("
         + "([0-9a-zA-Z\\u{80}-\\u{10FFFF}]([0-9a-zA-Z\\-\\u{80}-\\u{10FFFF}]*[0-9a-zA-Z\\u{80}-\\u{10FFFF}])?))(\\."
         + "([0-9a-zA-Z\\u{80}-\\u{10FFFF}]([0-9a-zA-Z\\-\\u{80}-\\u{10FFFF}]*[0-9a-zA-Z\\u{80}-\\u{10FFFF}])?))*))$";

   /**
    * Defined in RFC 2673
    */
   private static final String IPV4_REGEX = "^(((?!25?[6-9])[12]\\d|[1-9])?\\d\\.?\\b){4}$";

   /**
    * Defined in RFC 4291 section 2.2 and implemented in <a href="https://stackoverflow.com/a/17871737/12105820">this post</a>
    */
   private static final String IPV6_REGEX = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,"
         + "6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|"
         + "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:("
         + "(:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:)"
         + "{0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:("
         + "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";

   /**
    * Defined in RFC 1123
    */
   private static final String HOSTNAME_REGEX = "^[a-z0-9]([-a-z0-9]*[a-z0-9])?$";

   /**
    * Defined in RFC 4122
    */
   private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

   /**
    * Defined in RFC 6570 and implemented in <a href="https://stackoverflow.com/a/61645285/12105820">this post</a>
    */
   private static final String URI_TEMPLATE_REGEX = "^([^\\x00-\\x20\\x7f\"'%<>\\\\^`{|}]|%[0-9A-Fa-f]{2}|{[+#./;?&=,!@|]?("
         + "(\\w|%[0-9A-Fa-f]{2})(\\.?(\\w|%[0-9A-Fa-f]{2}))*(:[1-9]\\d{0,3}|\\*)?)(,((\\w|%[0-9A-Fa-f]{2})(\\.?(\\w|%[0-9A-Fa-f]{2}))*"
         + "(:[1-9]\\d{0,3}|\\*)?))*})*$";

   protected static final String TODO_COMMENT = "TODO";

   public enum JsonSchemaVocabulary {
      $ANCHOR, $COMMENT, $DEFS, $REF, ARRAY, BOOLEAN, DESCRIPTION, ENUM, EXAMPLES, EXCLUSIVE_MAXIMUM, EXCLUSIVE_MINIMUM, FORMAT, INTEGER,
      ITEMS, MAXIMUM, MAX_LENGTH, MINIMUM, MIN_LENGTH, NUMBER, OBJECT, PATTERN, PROPERTIES, REQUIRED, STRING, TITLE, TYPE, UNIQUE_ITEMS;

      public String key() {
         return CaseFormat.LOWER_UNDERSCORE.to( CaseFormat.LOWER_CAMEL, toString().toLowerCase() );
      }
   }

   public enum JsonSchemaFormat {
      DATE, DATE_TIME, DURATION, EMAIL, HOSTNAME, IDN_EMAIL, IDN_HOSTNAME, IPV4, IPV6, IRI, IRI_REFERENCE, JSON_POINTER, REGEX,
      RELATIVE_JSON_POINTER, TIME, URI, URI_REFERENCE, URI_TEMPLATE, UUID;

      public String key() {
         return toString().toLowerCase().replace( "_", "-" );
      }
   }

   protected record Context(
         Map<AspectModelUrn, ModelElement> generatedElements
   ) {
      protected Context() {
         this( new HashMap<>() );
      }
   }

   public JsonSchemaImporter( final JsonNode focus, final AspectGenerationConfig config ) {
      super( focus, config );
   }

   /**
    * Returns the builder for the result element type
    *
    * @param <SELF> the builder's self type
    * @return the builder
    */
   protected abstract <SELF extends SammBuilder.StructureElementBuilder<SELF, T>>
   SammBuilder.StructureElementBuilder<SELF, T> elementBuilder();

   /**
    * Creates an {@link Artifact} for the result
    *
    * @return the artifact
    */
   protected abstract A buildArtifact();

   @Override
   public Stream<A> generate() {
      return Stream.of( buildArtifact() );
   }

   /**
    * Main entry point of the JSON Schema to SAMM translation
    *
    * @return the resulting element, an {@link Aspect} or {@link Entity}
    */
   protected T buildElement() {
      final JsonNode schema = getFocus();
      final Context context = new Context();
      return elementBuilder()
            .preferredName( jsonProperty( schema, TITLE ).map( JsonNode::asText )
                  .orElseGet( () -> determinePreferredName( config.aspectModelUrn().getName() ) ) )
            .description( determineDescription( schema ).orElse( null ) )
            .properties( buildPropertiesList( schema, context ) )
            .build();
   }

   protected JsonNode root() {
      return getFocus();
   }

   protected Optional<JsonNode> jsonProperty( final JsonNode node, final JsonSchemaVocabulary property ) {
      return Optional.ofNullable( node.get( property.key() ) );
   }

   protected String determinePreferredName( final String nodeName ) {
      return CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, nodeName ).replace( "_", " " );
   }

   protected Optional<String> todoComment() {
      return config.addTodo()
            ? Optional.of( TODO_COMMENT )
            : Optional.empty();
   }

   protected Optional<String> determineDescription( final JsonNode node ) {
      return jsonProperty( node, DESCRIPTION )
            .or( () -> jsonProperty( node, $COMMENT ) )
            .map( JsonNode::asText )
            .or( this::todoComment );
   }

   protected Set<String> determineRequiredProperties( final JsonNode node ) {
      return jsonProperty( node, REQUIRED ).map( required ->
            Streams.stream( required.elements() )
                  .map( JsonNode::asText )
                  .collect( Collectors.toSet() ) ).orElse( Set.of() );
   }

   protected List<Property> buildPropertiesList( final JsonNode node, final Context context ) {
      return jsonProperty( node, PROPERTIES )
            .or( () -> jsonProperty( node, $DEFS ) )
            .stream()
            .flatMap( propertyNode -> Streams.stream( propertyNode.fields() ) )
            .map( field -> {
               final AspectModelUrn propertyUrn = buildPropertyUrn( field.getKey() );
               final ModelElement modelElement = context.generatedElements().get( propertyUrn );
               if ( modelElement instanceof final Property property ) {
                  return property;
               }
               return buildProperty( field, determineRequiredProperties( node ), context );
            } )
            .toList();
   }

   protected String escapeLocalName( final String elementName ) {
      return elementName.replace( "$", "" );
   }

   protected record ConstraintsAndImpliedType( List<Constraint> contraints, Scalar type ) {}

   protected ConstraintsAndImpliedType buildConstraints( final JsonNode propertyNode, final String propertyName,
         final String jsonType ) {
      final List<Constraint> constraints = new ArrayList<>();

      final String pattern = jsonProperty( propertyNode, PATTERN ).map( JsonNode::textValue ).orElse( null );
      final JsonSchemaFormat format = jsonProperty( propertyNode, FORMAT )
            .map( JsonNode::textValue )
            .flatMap( formatString -> Try.of( () ->
                  JsonSchemaFormat.valueOf( formatString.toUpperCase().replace( "-", "_" ) ) ).toJavaOptional() )
            .orElse( null );

      // RegularExpressionConstraint
      if ( pattern != null ) {
         constraints.add( regularExpressionConstraint().value( pattern ).build() );
      }

      final Scalar type;
      switch ( format ) {
         case null:
            type = Optional.ofNullable( Map.of(
                  STRING.key(), xsd.string,
                  NUMBER.key(), xsd.double_,
                  INTEGER.key(), xsd.integer,
                  BOOLEAN.key(), xsd.boolean_
            ).get( jsonType ) ).orElseThrow( () ->
                  new AspectImportException( "Unknown type for '" + propertyName + "': " + jsonType ) );
            break;
         case DATE:
            type = xsd.date;
            break;
         case DATE_TIME:
            type = xsd.dateTimeStamp;
            break;
         case TIME:
            type = xsd.time;
            break;
         case DURATION:
            type = xsd.duration;
            break;
         case EMAIL:
            type = xsd.string;
            constraints.add(
                  regularExpressionConstraint()
                        .preferredName( "email address" )
                        .description( "Matches email addresses as defined in RFC 5321 section 4.1.2" )
                        .see( "https://www.rfc-editor.org/rfc/rfc5321.html#section-4.1.2" )
                        .value( EMAIL_REGEX )
                        .build() );
            break;
         case IDN_EMAIL:
            type = xsd.string;
            constraints.add(
                  regularExpressionConstraint()
                        .preferredName( "internationalized email address" )
                        .description( "Matches international email addresses as defined in RFC 6531 section 3.3" )
                        .see( "https://datatracker.ietf.org/doc/html/rfc6531#section-3.3" )
                        .value( IDN_EMAIL_REGEX )
                        .build() );
            break;
         case HOSTNAME:
            type = xsd.string;
            constraints.add(
                  regularExpressionConstraint()
                        .preferredName( "hostname" )
                        .description( "Matches hostnames as defined in RFC 1123" )
                        .see( "https://www.rfc-editor.org/rfc/rfc1123.html" )
                        .value( HOSTNAME_REGEX )
                        .build() );
            break;
         case IDN_HOSTNAME:
            type = xsd.string;
            constraints.add(
                  regularExpressionConstraint()
                        .preferredName( "internationalized host name" )
                        .description(
                              "Matches hostnames as defined in RFC 1123 or internationalized hostnames as defined by RFC 5890 section 2.3"
                                    + ".2.3" )
                        .see( "https://www.rfc-editor.org/rfc/rfc1123.html" )
                        .see( "https://www.rfc-editor.org/rfc/rfc5890.html#section-2.3.2.3" )
                        .value( HOSTNAME_REGEX )
                        .build() );
            break;
         case IPV4:
            type = xsd.string;
            constraints.add(
                  regularExpressionConstraint()
                        .preferredName( "IPv4 address" )
                        .description( "Matches IPv4 addresses as defined in RFC 2763 section 3.2" )
                        .see( "https://www.rfc-editor.org/rfc/rfc2673.html#section-3.2" )
                        .value( IPV4_REGEX )
                        .build() );
            break;
         case IPV6:
            type = xsd.string;
            constraints.add(
                  regularExpressionConstraint()
                        .preferredName( "IPv6 address" )
                        .description( "Matches IPv6 addresses as defined in RFC 4291 section 2.2" )
                        .see( "https://www.rfc-editor.org/rfc/rfc4291.html#section-2.2" )
                        .value( IPV6_REGEX )
                        .build() );
            break;
         case URI, URI_REFERENCE, IRI, IRI_REFERENCE:
            type = xsd.anyURI;
            break;
         case UUID:
            type = xsd.string;
            constraints.add(
                  regularExpressionConstraint()
                        .preferredName( "universally unique identifier (UUID)" )
                        .description( "Matches UUIDs as defined by RFC 4122" )
                        .see( "https://www.rfc-editor.org/rfc/rfc4122.html" )
                        .value( UUID_REGEX )
                        .build() );
            break;
         case URI_TEMPLATE:
            type = xsd.string;
            constraints.add(
                  regularExpressionConstraint()
                        .preferredName( "URI template" )
                        .description( "Matches URI templates as defined by RFC 6570" )
                        .see( "https://www.rfc-editor.org/rfc/rfc6570.html" )
                        .value( URI_TEMPLATE_REGEX )
                        .build() );
            break;
         default:
            type = xsd.string;
      }

      // LengthConstraint
      final Optional<Integer> minLength = jsonProperty( propertyNode, MIN_LENGTH )
            .map( JsonNode::intValue );
      final Optional<Integer> maxLength = jsonProperty( propertyNode, MAX_LENGTH )
            .map( JsonNode::intValue );
      if ( minLength.isPresent() || maxLength.isPresent() ) {
         constraints.add( lengthConstraint()
               .minValue( minLength.map( BigInteger::valueOf ).orElse( null ) )
               .maxValue( maxLength.map( BigInteger::valueOf ).orElse( null ) ).build() );
      }

      // RangeConstraint
      final Optional<String> minimum = jsonProperty( propertyNode, MINIMUM )
            .map( JsonNode::asText );
      final Optional<String> maximum = jsonProperty( propertyNode, MAXIMUM )
            .map( JsonNode::asText );
      if ( minimum.isPresent() || maximum.isPresent() ) {
         final BoundDefinition lowerBound = jsonProperty( propertyNode, EXCLUSIVE_MINIMUM )
               .map( JsonNode::booleanValue )
               .map( value -> value ? BoundDefinition.GREATER_THAN : BoundDefinition.AT_LEAST )
               .orElse( BoundDefinition.OPEN );
         final BoundDefinition upperBound = jsonProperty( propertyNode, EXCLUSIVE_MAXIMUM )
               .map( JsonNode::booleanValue )
               .map( value -> value ? BoundDefinition.LESS_THAN : BoundDefinition.AT_MOST )
               .orElse( BoundDefinition.OPEN );
         constraints.add( rangeConstraint()
               .minValue( minimum.map( m -> value( m, type ) ).orElse( null ) )
               .maxValue( maximum.map( m -> value( m, type ) ).orElse( null ) )
               .lowerBound( lowerBound )
               .upperBound( upperBound )
               .build() );
      }
      return new ConstraintsAndImpliedType( constraints, type );
   }

   protected String determinEntityName( final String propertyName ) {
      return StringUtils.capitalize( propertyName );
   }

   protected Characteristic buildCharacteristic( final JsonNode propertyNode, final String propertyName, final Context context ) {
      final Optional<JsonNode> refNode = jsonProperty( propertyNode, $REF );
      if ( refNode.isPresent() ) {
         return buildCharacteristic( resolveRef( refNode.get().asText() ), propertyName, context );
      }

      final Optional<String> jsonTypeName = jsonProperty( propertyNode, TYPE ).map( JsonNode::asText );
      if ( jsonTypeName.isEmpty() ) {
         LOG.error( "Property node for '{}' is missing the required 'type' attribute, ignoring property", propertyName );
         return null;
      }

      final String jsonType = jsonTypeName.get();
      if ( jsonType.equals( ARRAY.key() ) ) {
         final boolean uniqueItems = jsonProperty( propertyNode, UNIQUE_ITEMS )
               .map( JsonNode::asBoolean )
               .orElse( false );
         final JsonNode items = jsonProperty( propertyNode, ITEMS )
               .orElseThrow( () -> new AspectImportException( "Property node for '" + propertyName + "' is of type array, "
                     + "but is missing an items property" ) );
         final Characteristic elementCharacteristic = buildCharacteristic( items, propertyName + "Element", context );
         return ( uniqueItems ? sortedSet() : list() )
               .elementCharacteristic( elementCharacteristic ).build();
      }

      if ( jsonType.equals( OBJECT.key() ) ) {
         return singleEntity()
               .dataType( buildEntity( propertyNode, propertyName, context ) )
               .build();
      }

      final ConstraintsAndImpliedType constraintsAndImpliedType = buildConstraints( propertyNode, propertyName, jsonType );
      final List<Constraint> constraints = constraintsAndImpliedType.contraints();
      final Scalar type = constraintsAndImpliedType.type();

      final Characteristic baseCharacteristic;
      final Optional<JsonNode> enumNode = jsonProperty( propertyNode, ENUM );
      if ( enumNode.isPresent() ) {
         final List<ScalarValue> values = Streams.stream( enumNode.get().elements() )
               .map( node -> value( node.asText(), type ) )
               .toList();
         baseCharacteristic = enumeration()
               .dataType( type )
               .description( todoComment().orElse( null ) )
               .values( values )
               .build();
      } else {
         baseCharacteristic = characteristic()
               .dataType( type )
               .description( todoComment().orElse( null ) )
               .build();
      }

      Characteristic characteristic = null;
      if ( !constraints.isEmpty() ) {
         characteristic = trait()
               .description( todoComment().orElse( null ) )
               .baseCharacteristic( baseCharacteristic )
               .constraints( constraints )
               .build();
      }

      if ( characteristic == null ) {
         characteristic = baseCharacteristic;
      }

      return characteristic;
   }

   protected Entity buildEntity( final JsonNode item, final String propertyName, final Context context ) {
      return entity( config.aspectModelUrn().withName( determinEntityName( propertyName ) ) )
            .description( determineDescription( item ).orElse( null ) )
            .properties( buildPropertiesList( item, context ) )
            .build();
   }

   protected JsonNode resolveRef( final String ref ) {
      // First check if a custom ref resolver is present
      final Function<String, JsonNode> customResolver = config.customRefResolver();
      if ( customResolver != null ) {
         final JsonNode result = customResolver.apply( ref );
         if ( result != null ) {
            return result;
         }
      }

      if ( !ref.startsWith( "#" ) ) {
         throw new AspectImportException( "Can not resolve non-relative $refs ($ref must start with '#')" );
      }

      // Regular, structural references: #/foo/bar
      if ( ref.substring( 1 ).startsWith( "/" ) ) {
         final String[] parts = ref.substring( 2 ).split( "/" );
         JsonNode result = root();
         for ( final String fieldName : parts ) {
            result = result.get( fieldName );
            if ( result == null ) {
               throw new AspectImportException( "Could not resolve $ref: " + ref );
            }
         }
         return result;
      }

      // Anchor-based refs: #ElementName
      final String elementName = ref.substring( 1 );
      try {
         return Streams.stream( root().get( $DEFS.key() ).elements() )
               .filter( node -> node.get( $ANCHOR.key() ).asText().equals( elementName ) )
               .findFirst()
               .orElseThrow();
      } catch ( final Exception exception ) {
         throw new AspectImportException( "Could not resolve $ref '" + ref + "': No structure #/$defs/* with $anchor found" );
      }
   }

   protected AspectModelUrn buildPropertyUrn( final String propertyName ) {
      return config.aspectModelUrn().withName( escapeLocalName( propertyName ) );
   }

   protected Property buildProperty( final Map.Entry<String, JsonNode> field, final Set<String> requiredProperties,
         final Context context ) {
      final String propertyName = field.getKey();
      final JsonNode propertyNode = field.getValue();

      final Optional<JsonNode> refNode = jsonProperty( propertyNode, $REF );
      if ( refNode.isPresent() ) {
         return buildProperty( Map.entry( propertyName, resolveRef( refNode.get().asText() ) ), requiredProperties, context );
      }

      final Characteristic characteristic = buildCharacteristic( propertyNode, propertyName, context );

      final Optional<ScalarValue> exampleValue = jsonProperty( propertyNode, EXAMPLES )
            .filter( JsonNode::isArray )
            .flatMap( node -> Streams.stream( node.elements() ).findFirst() )
            .flatMap( example -> characteristic.getDataType().isPresent()
                  && characteristic.getDataType().get() instanceof final Scalar scalar
                  ? Optional.of( value( example.asText(), scalar ) )
                  : Optional.empty() );

      final AspectModelUrn propertyUrn = buildPropertyUrn( propertyName );
      final Property result = property( propertyUrn )
            .preferredName( determinePreferredName( propertyName ) )
            .description( determineDescription( propertyNode ).orElse( null ) )
            .characteristic( characteristic )
            .exampleValue( exampleValue.orElse( null ) )
            .optional( !requiredProperties.contains( propertyName ) )
            .build();
      context.generatedElements().put( propertyUrn, result );
      return result;
   }
}
