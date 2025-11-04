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

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.$ANCHOR;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.$COMMENT;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.$DEFS;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.$REF;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.ANY_OF;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.ARRAY;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.BOOLEAN;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.DESCRIPTION;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.ENUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.EXAMPLES;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.EXCLUSIVE_MAXIMUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.EXCLUSIVE_MINIMUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.FORMAT;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.INTEGER;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.ITEMS;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.MAXIMUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.MAX_LENGTH;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.MINIMUM;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.MIN_LENGTH;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.NUMBER;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.OBJECT;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.ONE_OF;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.PATTERN;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.PROPERTIES;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.REQUIRED;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.STRING;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.TITLE;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.TYPE;
import static org.eclipse.esmf.aspectmodel.importer.jsonschema.JsonSchemaVocabulary.UNIQUE_ITEMS;
import static org.eclipse.esmf.metamodel.DataTypes.xsd;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.characteristic;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.either;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.generator.Generator;
import org.eclipse.esmf.aspectmodel.importer.exception.AspectImportException;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
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
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.builder.AspectModelBuildingException;
import org.eclipse.esmf.metamodel.builder.SammBuilder;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Streams;
import io.vavr.control.Try;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for translators from JSON Schema to {@link StructureElement}s. For concrete usage, see {@link JsonSchemaToAspect} and
 * {@link JsonSchemaToEntity}.
 *
 * @param <T> the type of StructureElement, e.g., {@link Aspect} or {@link Entity}.
 * @param <A> the corresponding {@link Artifact} type
 */
public abstract class JsonSchemaImporter<T extends StructureElement, A extends Artifact<AspectModelUrn, T>> extends
      Generator<JsonNode, AspectModelUrn, T, JsonSchemaImporterConfig, A> {
   private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaImporter.class );
   /**
    * The comment that is inserted for undocumented elements
    */
   protected static final String TODO_COMMENT = "TODO";

   /**
    * The attribute that can be added parallel to enums that describes their values, as used by CycloneDX schema
    */
   protected static final JsonProperty META_ENUM = new JsonProperty.Named( "meta:enum" );

   /**
    * The evaluation context when recursively traversing the schema document
    *
    * @param propertyName the name of the JSON property currently being processed
    * @param path the nested path of JSON properties pointing to the current position in the schema document
    * @param type the JSON type of the current schema
    * @param characteristicUrn the URN of the Characteristic to create for the current schema
    * @param generatedElementsByUrn the generated elements indexed by URN
    * @param schemaCharacteristicsByNode Characteristics generated from named schemas indexed by schema node
    * @param schemaCharacteristicsByPath Characteristics generated from named schemas indexed by JSON path pointing to them
    * @param schemaCharacteristicsInProgress Characteristics generated from named schemas indexed by local name
    */
   protected record Context(
         String propertyName,
         List<String> path,
         String type,
         AspectModelUrn characteristicUrn,
         Map<AspectModelUrn, ModelElement> generatedElementsByUrn,
         Map<String, Characteristic> schemaCharacteristicsByPath,
         Map<JsonNode, Characteristic> schemaCharacteristicsByNode,
         Map<String, Collection<DefaultCharacteristicWrapper>> schemaCharacteristicsInProgress,
         Set<JsonNode> propertyNodesInProgress
   ) {
      protected Context() {
         this( null, List.of(), null, null, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashSet<>() );
      }

      public Context withPath( final String element ) {
         return new Context( propertyName(), Stream.concat( path().stream(), Stream.of( element ) ).toList(), type(), null,
               generatedElementsByUrn(), schemaCharacteristicsByPath(), schemaCharacteristicsByNode(), schemaCharacteristicsInProgress(),
               propertyNodesInProgress() );
      }

      public Context withType( final String type ) {
         return new Context( propertyName(), path(), type, null, generatedElementsByUrn(), schemaCharacteristicsByPath(),
               schemaCharacteristicsByNode(), schemaCharacteristicsInProgress(), propertyNodesInProgress() );
      }

      public Context withCharacteristicUrn( final AspectModelUrn urn ) {
         return new Context( propertyName(), path(), type(), urn, generatedElementsByUrn(), schemaCharacteristicsByPath(),
               schemaCharacteristicsByNode(), schemaCharacteristicsInProgress(), propertyNodesInProgress() );
      }

      public Context withNoCharacteristicUrn() {
         return withCharacteristicUrn( null );
      }

      public Context withPropertyName( final String name ) {
         return new Context( name, path(), type(), characteristicUrn(), generatedElementsByUrn(), schemaCharacteristicsByPath(),
               schemaCharacteristicsByNode(), schemaCharacteristicsInProgress(), propertyNodesInProgress() );
      }
   }

   public JsonSchemaImporter( final JsonNode focus, final JsonSchemaImporterConfig config ) {
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
            .preferredName( determinePreferredName( schema, config.aspectModelUrn().getName() ) )
            .description( determineDescription( schema ).orElse( null ) )
            .properties( buildPropertiesList( schema, context ) )
            .build();
   }

   /**
    * Returns the root node of the document
    *
    * @return the root node
    */
   protected JsonNode root() {
      return getFocus();
   }

   /**
    * Gets the value of a property of a node
    *
    * @param node the node
    * @param property the attribute
    * @return the value
    */
   protected Optional<JsonNode> jsonProperty( final JsonNode node, final JsonProperty property ) {
      return Optional.ofNullable( node.get( property.key() ) );
   }

   /**
    * Gets the content of the "title" attribute, if present
    *
    * @param elementNode the node
    * @return the title
    */
   protected Optional<String> determinePreferredName( final JsonNode elementNode ) {
      return jsonProperty( elementNode, TITLE ).map( JsonNode::asText );
   }

   /**
    * Derives a preferredName from a node: Either its "title" attribute, or by un-camel-casing its name
    *
    * @param elementNode the node
    * @param nodeName the name of the node
    * @return the preferred name for the node
    */
   protected String determinePreferredName( final JsonNode elementNode, final String nodeName ) {
      return determinePreferredName( elementNode )
            .orElseGet( () -> CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, nodeName ).replace( "_", " " ) );
   }

   /**
    * Gets the "TO DO" comment text, if the corresponding flag in the config is set
    *
    * @return the comment if configured, otherwise empty
    */
   protected Optional<String> todoComment() {
      return config.addTodo()
            ? Optional.of( TODO_COMMENT )
            : Optional.empty();
   }

   /**
    * Gets a description text for the node, either from its "description" attribute or its "$comment", if present
    *
    * @param node the node
    * @return the description
    */
   protected Optional<String> determineDescription( final JsonNode node ) {
      return jsonProperty( node, DESCRIPTION )
            .or( () -> jsonProperty( node, $COMMENT ) )
            .map( JsonNode::asText )
            .or( this::todoComment );
   }

   /**
    * Determines the set of properties that are marked as "required"
    *
    * @param node the node
    * @return which properties are required
    */
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
            .map( field -> buildProperty( field, determineRequiredProperties( node ), context ) )
            .toList();
   }

   protected String escapeLocalName( final String elementName ) {
      return CaseFormat.LOWER_UNDERSCORE.to( CaseFormat.LOWER_CAMEL,
            CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, elementName )
                  .replace( "$", "" )
                  .replaceAll( "[^a-zA-Z0-9]", "_" ) );
   }

   protected Optional<RegularExpressionConstraint> buildRegularExpressionConstraint( final JsonNode propertyNode ) {
      final String pattern = jsonProperty( propertyNode, PATTERN ).map( JsonNode::textValue ).orElse( null );
      return pattern != null
            ? Optional.of( regularExpressionConstraint().value( pattern ).build() )
            : Optional.empty();
   }

   protected Optional<LengthConstraint> buildLengthConstraint( final JsonNode propertyNode ) {
      final Optional<Integer> minLength = jsonProperty( propertyNode, MIN_LENGTH )
            .map( JsonNode::intValue );
      final Optional<Integer> maxLength = jsonProperty( propertyNode, MAX_LENGTH )
            .map( JsonNode::intValue );
      return minLength.isPresent() || maxLength.isPresent()
            ? Optional.of( lengthConstraint()
            .minValue( minLength.map( BigInteger::valueOf ).orElse( null ) )
            .maxValue( maxLength.map( BigInteger::valueOf ).orElse( null ) ).build() )
            : Optional.empty();
   }

   protected Optional<RangeConstraint> buildRangeConstraint( final JsonNode propertyNode, final Scalar type ) {
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
         return Optional.of( rangeConstraint()
               .minValue( minimum.map( m -> value( m, type ) ).orElse( null ) )
               .maxValue( maximum.map( m -> value( m, type ) ).orElse( null ) )
               .lowerBound( lowerBound )
               .upperBound( upperBound )
               .build() );
      }
      return Optional.empty();
   }

   protected Optional<JsonSchemaFormat> determineFormat( final JsonNode propertyNode ) {
      return jsonProperty( propertyNode, FORMAT )
            .map( JsonNode::textValue )
            .flatMap( formatString -> Try.of(
                        () -> JsonSchemaFormat.valueOf( formatString.toUpperCase().replace( "-", "_" ) ) )
                  .toJavaOptional() );
   }

   protected Scalar determinePropertyType( final JsonNode propertyNode, final String propertyName, final String jsonType ) {
      //noinspection DataFlowIssue
      return determineFormat( propertyNode )
            .map( format -> switch ( format ) {
               case DATE -> xsd.date;
               case DATE_TIME -> xsd.dateTimeStamp;
               case TIME -> xsd.time;
               case DURATION -> xsd.duration;
               case URI, URI_REFERENCE, IRI, IRI_REFERENCE -> xsd.anyURI;
               default -> xsd.string;
            } ).or( () -> Optional.ofNullable( Map.of(
                  STRING.key(), xsd.string,
                  NUMBER.key(), xsd.double_,
                  INTEGER.key(), xsd.integer,
                  BOOLEAN.key(), xsd.boolean_
            ).get( jsonType ) ) )
            .orElseThrow( () -> new AspectImportException( "Unknown type for '" + propertyName + "': " + jsonType ) );
   }

   protected List<Constraint> buildConstraints( final JsonNode propertyNode, final Scalar type ) {
      return Stream.of(
            determineFormat( propertyNode ).flatMap( JsonSchemaFormat::getRegularExpressionConstraint ),
            buildRegularExpressionConstraint( propertyNode ),
            buildLengthConstraint( propertyNode ),
            buildRangeConstraint( propertyNode, type )
      ).<Constraint> flatMap( Optional::stream ).toList();
   }

   protected String determineEntityName( final String propertyName ) {
      return StringUtils.capitalize( propertyName );
   }

   protected Characteristic buildEither( final Iterator<JsonNode> elements, final Context context ) {
      final List<Map.Entry<String, JsonNode>> result = new ArrayList<>();
      int i = 0;
      while ( elements.hasNext() ) {
         result.add( Map.entry( context.propertyName() + i, elements.next() ) );
         i++;
      }
      return buildEither( result, context );
   }

   protected Characteristic buildEither( final List<Map.Entry<String, JsonNode>> elements, final Context context ) {
      if ( elements.isEmpty() ) {
         throw new AspectImportException( "Encountered empty oneOf or anyOf for " + context.propertyName() );
      }
      if ( elements.size() == 1 ) {
         return buildCharacteristic( elements.getFirst().getValue(), context );
      }

      final Characteristic left = buildCharacteristic( elements.get( 0 ).getValue(),
            context.withNoCharacteristicUrn().withPropertyName( elements.get( 0 ).getKey() ) );
      final Characteristic right = elements.size() == 2
            ? buildCharacteristic( elements.get( 1 ).getValue(),
            context.withNoCharacteristicUrn().withPropertyName( elements.get( 1 ).getKey() ) )
            : buildEither( elements.subList( 1, elements.size() ), context.withNoCharacteristicUrn() );
      return either( context.characteristicUrn() )
            .left( left )
            .right( right )
            .build();
   }

   /**
    * When a schema's type is "array" and the value is an array itself, which indicates
    * <a href="https://json-schema.org/understanding-json-schema/reference/array#tupleValidation">Tuple validation</a>
    * in JSON Schema Draft 4 - 2019-09, build the corresponding Characteristic
    *
    * @param elements the elements of the ArrayNode
    * @param context the translation context
    * @return the corresponding Characteristic
    */
   protected Characteristic buildCharacteristicForArraySchema( final List<JsonNode> elements, final Context context ) {
      final int numElements = elements.size();
      if ( numElements == 0 ) {
         throw new AspectImportException( "Encountered empty schemas list for " + context.propertyName() );
      } else if ( numElements == 1 ) {
         return buildCharacteristic( elements.getFirst(), context );
      } else {
         LOG.warn( "Tuple validation for {} is not supported, using the schema for the first item.", context.propertyName() );
         return buildCharacteristic( elements.getFirst(), context );
      }
   }

   protected static class DefaultCharacteristicWrapper extends DefaultCharacteristic {
      private Characteristic wrappedCharacteristic;

      public DefaultCharacteristicWrapper( final MetaModelBaseAttributes metaModelBaseAttributes ) {
         super( metaModelBaseAttributes, Optional.empty() );
      }

      @Override
      public AspectModelUrn urn() {
         return wrappedCharacteristic == null ? super.urn() : wrappedCharacteristic.urn();
      }

      @Override
      public Optional<Type> getDataType() {
         return wrappedCharacteristic == null ? Optional.of( xsd.string ) : wrappedCharacteristic.getDataType();
      }

      @Override
      public AspectModelFile getSourceFile() {
         return wrappedCharacteristic == null ? null : wrappedCharacteristic.getSourceFile();
      }

      public void setWrappedCharacteristic( final Characteristic wrappedCharacteristic ) {
         this.wrappedCharacteristic = wrappedCharacteristic;
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         if ( wrappedCharacteristic == null ) {
            return super.accept( visitor, context );
         }
         return wrappedCharacteristic.accept( visitor, context );
      }
   }

   protected Characteristic buildCharacteristic( final JsonNode propertyNode, final Context context ) {
      final Optional<JsonNode> refNode = jsonProperty( propertyNode, $REF );
      if ( refNode.isPresent() ) {
         return buildCharacteristicFromReferencedSchema( refNode.get(), context );
      }

      final Optional<String> jsonTypeName = jsonProperty( propertyNode, TYPE )
            .map( JsonNode::asText )
            .or( () -> Optional.ofNullable( context.type() ) );

      final Optional<Characteristic> either = jsonProperty( propertyNode, ONE_OF )
            .or( () -> jsonProperty( propertyNode, ANY_OF ) )
            .filter( JsonNode::isArray )
            .map( node -> {
               final List<JsonNode> elements = Lists.newArrayList( node.elements() );
               // Check if this is a "oneOf [ { required: X }, { required: Y } ]" construct.
               // In this case build an Either of the Properties themselves.
               final boolean propertiesAreEitherElements = elements.stream().allMatch( elementNode ->
                     jsonProperty( elementNode, TYPE ).isEmpty() && jsonProperty( elementNode, REQUIRED ).isPresent() );
               final Optional<JsonNode> properties = jsonProperty( propertyNode, PROPERTIES );
               if ( propertiesAreEitherElements && properties.isPresent() ) {
                  return buildEither( Streams.stream( properties.get().fields() ).toList(), context );
               }

               // It's not the case, therefore create an Either from the oneOf nodes.
               return buildEither( node.elements(), jsonTypeName.map( context::withType ).orElse( context ) );
            } );
      if ( either.isPresent() && jsonTypeName.isEmpty() ) {
         return either.get();
      }

      if ( jsonTypeName.isEmpty() ) {
         if ( propertyNode.isArray() ) {
            return buildCharacteristicForArraySchema( Lists.newArrayList( propertyNode.elements() ), context.withNoCharacteristicUrn() );
         }
         throw new AspectModelBuildingException(
               "Property node for '" + context.propertyName() + "' is missing the required 'type' property, ignoring" );
      }

      final String jsonType = jsonTypeName.get();
      if ( jsonType.equals( ARRAY.key() ) ) {
         return buildCollection( propertyNode, context );
      }

      if ( jsonType.equals( OBJECT.key() ) ) {
         return buildSingleEntity( propertyNode, context );
      }

      final Scalar scalarType = determinePropertyType( propertyNode, context.propertyName(), jsonType );
      final List<Constraint> constraints = buildConstraints( propertyNode, scalarType );
      final Optional<JsonNode> enumNode = jsonProperty( propertyNode, ENUM );
      final Characteristic baseCharacteristic = enumNode.isPresent()
            ? buildEnumeration( propertyNode, enumNode.get(), scalarType, context )
            : buildPlainCharacteristic( propertyNode, scalarType, context );
      return constraints.isEmpty()
            ? baseCharacteristic
            : buildTrait( propertyNode, baseCharacteristic, constraints, context );
   }

   protected Characteristic buildCharacteristicFromReferencedSchema( final JsonNode refNode, final Context context ) {
      // Prevent infinite recursion: If the Property being referenced is already in the process of being constructed,
      // return a forward reference instead.
      final String reference = refNode.asText();
      final Characteristic schemaCharacteristic = context.schemaCharacteristicsByPath().get( reference );
      final String characteristicName = StringUtils.capitalize( determineElementNameForSchema( reference ) ) + "Characteristic";
      final AspectModelUrn characteristicUrn = buildCharacteristicUrn( characteristicName );
      // Characteristic for named schema was already constructed
      if ( schemaCharacteristic != null ) {
         final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
               .withUrn( characteristicUrn )
               .build();
         final DefaultCharacteristicWrapper defaultCharacteristicWrapper = new DefaultCharacteristicWrapper( baseAttributes );
         defaultCharacteristicWrapper.setWrappedCharacteristic( schemaCharacteristic );
         return defaultCharacteristicWrapper;
      }

      // The Characteristic for the referenced schema is currently being constructed
      if ( context.schemaCharacteristicsInProgress().containsKey( reference ) ) {
         final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
               .withUrn( characteristicUrn )
               .build();
         final DefaultCharacteristicWrapper defaultCharacteristicWrapper = new DefaultCharacteristicWrapper( baseAttributes );
         context.schemaCharacteristicsInProgress().get( reference ).add( defaultCharacteristicWrapper );
         return defaultCharacteristicWrapper;
      }

      // No Characteristic exists yet for the referenced schema. Create one.
      final Collection<DefaultCharacteristicWrapper> forwardReferences = new ArrayList<>();
      context.schemaCharacteristicsInProgress().put( reference, forwardReferences );
      final Characteristic result = buildCharacteristic( resolveRef( refNode.asText() ),
            context.withCharacteristicUrn( characteristicUrn ) );
      // If this is the end of the recursion, update the forward references with the created Characteristic
      if ( !( result instanceof DefaultCharacteristicWrapper ) ) {
         context.schemaCharacteristicsByPath().put( reference, result );
         for ( final DefaultCharacteristicWrapper p : forwardReferences ) {
            p.setWrappedCharacteristic( result );
         }
         context.schemaCharacteristicsInProgress().remove( reference );
      }
      return result;
   }

   protected Characteristic buildCollection( final JsonNode propertyNode, final Context context ) {
      if ( propertyNode.isArray() ) {
         return buildCharacteristicForArraySchema( Lists.newArrayList( propertyNode.elements() ), context.withNoCharacteristicUrn() );
      }

      final boolean uniqueItems = jsonProperty( propertyNode, UNIQUE_ITEMS )
            .map( JsonNode::asBoolean )
            .orElse( false );
      final Optional<JsonNode> items = jsonProperty( propertyNode, ITEMS );
      final Optional<JsonNode> oneOfOrAnyOf = jsonProperty( propertyNode, ONE_OF ).or( () -> jsonProperty( propertyNode, ANY_OF ) );
      final Characteristic elementCharacteristic;
      if ( items.isEmpty() && oneOfOrAnyOf.isPresent() ) {
         elementCharacteristic = buildEither( oneOfOrAnyOf.get().elements(), context.withNoCharacteristicUrn() );
      } else if ( items.isPresent() ) {
         elementCharacteristic = buildCharacteristic( items.get(),
               context.withNoCharacteristicUrn().withPropertyName( context.propertyName() + "Element" ) );
      } else {
         throw new AspectImportException(
               "Property node for '" + context.propertyName() + "' is of type array, but is missing an items property" );
      }
      return ( uniqueItems ? sortedSet( context.characteristicUrn() ) : list( context.characteristicUrn() ) )
            .preferredName( determinePreferredName( propertyNode ).orElse( null ) )
            .description( todoComment().orElse( null ) )
            .elementCharacteristic( elementCharacteristic )
            .build();
   }

   protected SingleEntity buildSingleEntity( final JsonNode propertyNode, final Context context ) {
      return singleEntity( context.characteristicUrn() )
            .preferredName( determinePreferredName( propertyNode ).orElse( null ) )
            .description( todoComment().orElse( null ) )
            .dataType( buildEntity( propertyNode, context.withPath( context.propertyName() ).withNoCharacteristicUrn() ) )
            .build();
   }

   protected Characteristic buildPlainCharacteristic( final JsonNode propertyNode, final Scalar type, final Context context ) {
      return characteristic( context.characteristicUrn() )
            .dataType( type )
            .preferredName( determinePreferredName( propertyNode ).orElse( null ) )
            .description( todoComment().orElse( null ) )
            .build();
   }

   protected Enumeration buildEnumeration( final JsonNode propertyNode, final JsonNode enumNode, final Scalar type,
         final Context context ) {
      final Map<String, String> valueDescriptions = jsonProperty( propertyNode, META_ENUM )
            .stream()
            .flatMap( node -> Streams.stream( node.fields() ) )
            .map( entry -> Map.entry( entry.getKey(), entry.getValue().asText() ) )
            .collect( asMap() );

      final List<ScalarValue> values = Streams.stream( enumNode.elements() )
            .map( node -> {
               final String enumValue = node.asText();
               return value()
                     .description( valueDescriptions.get( enumValue ) )
                     .lexicalValue( enumValue )
                     .type( type )
                     .build();
            } )
            .toList();
      return enumeration( context.characteristicUrn() )
            .dataType( type )
            .preferredName( determinePreferredName( propertyNode ).orElse( null ) )
            .description( todoComment().orElse( null ) )
            .values( values )
            .build();
   }

   protected Trait buildTrait( final JsonNode propertyNode, final Characteristic baseCharacteristic, final List<Constraint> constraints,
         final Context context ) {
      final AspectModelUrn traitUrn = Optional.ofNullable( context.characteristicUrn() )
            .map( urn -> urn.withName( urn.getName() + "Trait" ) )
            .orElse( null );
      return trait( traitUrn )
            .preferredName( determinePreferredName( propertyNode ).orElse( null ) )
            .description( todoComment().orElse( null ) )
            .baseCharacteristic( baseCharacteristic )
            .constraints( constraints )
            .build();
   }

   protected Entity buildEntity( final JsonNode item, final Context context ) {
      final String entityName = determineEntityName( context.propertyName() ) + "Entity";
      final AspectModelUrn entityUrn = config.aspectModelUrn().withName( entityName );
      final ModelElement previouslyGeneratedElement = context.generatedElementsByUrn().get( entityUrn );
      if ( previouslyGeneratedElement instanceof final Entity entity ) {
         return entity;
      }

      final Entity result = entity( config.aspectModelUrn().withName( entityName ) )
            .preferredName( determinePreferredName( item ).orElse( null ) )
            .description( determineDescription( item ).orElse( null ) )
            .properties( buildPropertiesList( item, context ) )
            .build();
      context.generatedElementsByUrn().put( entityUrn, result );
      return result;
   }

   protected JsonNode resolveRef( final String ref ) {
      // First check if a custom ref resolver is present. This is required for handling references to external files,
      // because only the caller knows how to open/resolve those: They could be local files, or remote resources
      // or something else.
      final Function<String, Optional<JsonNode>> customResolver = config.customRefResolver();
      if ( customResolver != null ) {
         final Optional<JsonNode> result = customResolver.apply( ref );
         if ( result.isPresent() ) {
            return result.get();
         }
      }

      if ( !ref.startsWith( "#" ) ) {
         throw new AspectImportException( "Can not resolve non-relative $refs ($ref must start with '#'): " + ref );
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

   protected AspectModelUrn buildCharacteristicUrn( final String characteristicName ) {
      return config.aspectModelUrn().withName( StringUtils.capitalize( escapeLocalName( characteristicName ) ) );
   }

   protected boolean characteristicsAreLogicallyEquivalent( final Characteristic characteristic1, final Characteristic characteristic2 ) {
      if ( characteristic1 == null || characteristic2 == null ) {
         return false;
      }
      if ( !characteristic1.isAnonymous() || !characteristic2.isAnonymous() && characteristic1.urn().equals( characteristic2.urn() ) ) {
         return true;
      }
      return characteristic1.getClass().isAssignableFrom( characteristic2.getClass() )
            && characteristic1.getDataType().equals( characteristic2.getDataType() )
            && characteristic1.getPreferredNames().equals( characteristic2.getPreferredNames() )
            && characteristic1.getDescriptions().equals( characteristic2.getDescriptions() )
            && characteristic1.getSee().equals( characteristic2.getSee() );
   }

   protected String determineElementNameForSchema( final String schema ) {
      final String[] parts = schema.split( "/" );
      return escapeLocalName( parts[parts.length - 1] );
   }

   protected Property buildProperty( final Map.Entry<String, JsonNode> field, final Set<String> requiredProperties,
         final Context context ) {
      final JsonNode propertyNode = field.getValue();
      final String preliminaryPropertyName = escapeLocalName( field.getKey() );

      final Characteristic characteristic;
      final Optional<JsonNode> refNode = jsonProperty( propertyNode, $REF );
      if ( refNode.isPresent() ) {
         final String reference = refNode.get().asText();
         final JsonNode referencedNode = resolveRef( reference );
         if ( context.schemaCharacteristicsByNode().containsKey( referencedNode ) ) {
            characteristic = context.schemaCharacteristicsByNode().get( referencedNode );
         } else {
            characteristic = buildCharacteristicFromReferencedSchema( refNode.get(), context.withPropertyName( preliminaryPropertyName ) );
            context.schemaCharacteristicsByNode().put( referencedNode, characteristic );
         }
      } else {
         characteristic = buildCharacteristic( propertyNode, context.withPropertyName( preliminaryPropertyName ) );
      }

      final String propertyName;
      final AspectModelUrn propertyUrn;

      final AspectModelUrn preliminaryPropertyUrn = buildPropertyUrn( preliminaryPropertyName );
      final ModelElement previouslyGeneratedElement = context.generatedElementsByUrn().get( preliminaryPropertyUrn );
      final String descriptionForThisElement = determineDescription( propertyNode ).orElse( null );
      final String preferredNameForThisElement = determinePreferredName( propertyNode, preliminaryPropertyName );
      final Optional<ScalarValue> exampleValueForThisElement = jsonProperty( propertyNode, EXAMPLES )
            .filter( JsonNode::isArray )
            .flatMap( node -> Streams.stream( node.elements() ).findFirst() )
            .flatMap( example -> characteristic.getDataType().isPresent()
                  && characteristic.getDataType().get() instanceof final Scalar scalar
                  ? Optional.of( value( example.asText(), scalar ) )
                  : Optional.empty() );

      if ( previouslyGeneratedElement instanceof final Property previouslyGeneratedProperty ) {
         final boolean descriptionsAreEqual = Optional.ofNullable( previouslyGeneratedProperty.getDescription( Locale.ENGLISH ) )
               .map( description -> description.equals( descriptionForThisElement ) ).orElse( false );
         final boolean preferredNamesAreEqual = Optional.ofNullable( previouslyGeneratedProperty.getPreferredName( Locale.ENGLISH ) )
               .map( preferredName -> preferredName.equals( preferredNameForThisElement ) ).orElse( false );
         final boolean exampleValuesAreEqual = previouslyGeneratedProperty.getExampleValue().flatMap( previousExampleValue ->
               exampleValueForThisElement.map( exampleValue -> previousExampleValue == exampleValue ) ).orElse( false );
         if ( descriptionsAreEqual && preferredNamesAreEqual && exampleValuesAreEqual && characteristicsAreLogicallyEquivalent(
               previouslyGeneratedProperty.getCharacteristic().orElse( null ), characteristic ) ) {
            return previouslyGeneratedProperty;
         }

         // Another Property with the same name but a different Characteristic was previously generated.
         // So we need another name.
         final String newPropertyName = StringUtils.uncapitalize( context.path().stream().map( StringUtils::capitalize )
               .collect( Collectors.joining( "", "", StringUtils.capitalize( preliminaryPropertyName ) ) ) );
         propertyName = newPropertyName.equals( preliminaryPropertyName )
               ? newPropertyName + "0"
               : newPropertyName;
         propertyUrn = config.aspectModelUrn().withName( propertyName );
      } else {
         propertyName = preliminaryPropertyName;
         propertyUrn = preliminaryPropertyUrn;
      }

      final Property result = property( propertyUrn )
            .preferredName( preferredNameForThisElement )
            .description( descriptionForThisElement )
            .characteristic( characteristic )
            .exampleValue( exampleValueForThisElement.orElse( null ) )
            .optional( !requiredProperties.contains( propertyName ) )
            .payloadName( propertyName.equals( preliminaryPropertyName ) ? null : preliminaryPropertyName )
            .build();
      context.generatedElementsByUrn().put( propertyUrn, result );
      return result;
   }
}
