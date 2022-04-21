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

package io.openmanufacturing.sds.aspectmodel.serializer;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import com.google.common.collect.ImmutableList;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.UnsupportedVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMMC;
import io.openmanufacturing.sds.aspectmodel.vocabulary.Namespace;

/**
 * Allows to serialize a {@link Model} that contains an Aspect model to RDF/Turtle while following
 * the formatting rules for Aspect models.
 */
public class PrettyPrinter {
   private static final String INDENT = "   ";
   private static final String TRIPLE_QUOTE = "\"\"\"";
   private static final String LINE_BREAK = "\n";
   private final Comparator<Property> propertyOrder;
   private final Comparator<Map.Entry<String, String>> prefixOrder;
   private final Set<Resource> processedResources = new HashSet<>();
   private final Queue<Resource> resourceQueue = new ArrayDeque<>();

   private final Model model;
   private final AspectModelUrn rootElementUrn;
   private final PrintWriter writer;
   private final Map<String, String> prefixMap;
   private final BAMM bamm;

   /**
    * Constructor.
    *
    * @param versionedModel The model to write
    * @param rootElementUrn The URN of the root model element
    * @param writer The writer to write to.
    */
   public PrettyPrinter( final VersionedModel versionedModel, final AspectModelUrn rootElementUrn, final PrintWriter writer ) {
      model = versionedModel.getRawModel();
      final KnownVersion metaModelVersion = KnownVersion.fromVersionString( versionedModel.getVersion().toString() )
            .orElseThrow( () -> new UnsupportedVersionException( versionedModel.getVersion() ) );
      this.writer = writer;
      this.rootElementUrn = rootElementUrn;

      bamm = new BAMM( metaModelVersion );
      final BAMMC bammc = new BAMMC( metaModelVersion );

      prefixMap = new HashMap<>( Namespace.createPrefixMap( metaModelVersion ) );
      prefixMap.putAll( versionedModel.getModel().getNsPrefixMap() );
      prefixMap.put( "", rootElementUrn.getUrnPrefix() );

      PrintUtil.registerPrefixMap( prefixMap );
      model.setNsPrefixes( prefixMap );

      propertyOrder = createPredefinedPropertyOrder( bammc );
      prefixOrder = createPredefinedPrefixOrder();
   }

   private Comparator<Property> createPredefinedPropertyOrder( final BAMMC bammc ) {
      final List<Property> predefinedPropertyOrder = new ArrayList<>();
      predefinedPropertyOrder.add( bamm.name() );
      predefinedPropertyOrder.add( bamm._extends() );
      predefinedPropertyOrder.add( bamm.preferredName() );
      predefinedPropertyOrder.add( bamm.description() );
      predefinedPropertyOrder.add( bamm.see() );
      predefinedPropertyOrder.add( bamm.characteristic() );
      predefinedPropertyOrder.add( bamm.properties() );
      predefinedPropertyOrder.add( bamm.operations() );
      predefinedPropertyOrder.add( bamm.events() );
      predefinedPropertyOrder.add( bamm.input() );
      predefinedPropertyOrder.add( bamm.output() );
      predefinedPropertyOrder.add( bamm.baseCharacteristic() );
      predefinedPropertyOrder.add( bamm.dataType() );
      predefinedPropertyOrder.add( bamm.exampleValue() );
      predefinedPropertyOrder.add( bamm.value() );
      predefinedPropertyOrder.add( bamm.property() );
      predefinedPropertyOrder.add( bamm.optional() );

      predefinedPropertyOrder.add( bammc.languageCode() );
      predefinedPropertyOrder.add( bammc.localeCode() );
      predefinedPropertyOrder.add( bammc.left() );
      predefinedPropertyOrder.add( bammc.right() );
      predefinedPropertyOrder.add( bammc.minValue() );
      predefinedPropertyOrder.add( bammc.maxValue() );
      predefinedPropertyOrder.add( bammc.lowerBoundDefinition() );
      predefinedPropertyOrder.add( bammc.upperBoundDefinition() );
      predefinedPropertyOrder.add( bammc.defaultValue() );
      predefinedPropertyOrder.add( bammc.unit() );
      predefinedPropertyOrder.add( bammc.left() );
      predefinedPropertyOrder.add( bammc.right() );
      predefinedPropertyOrder.add( bammc.deconstructionRule() );
      predefinedPropertyOrder.add( bammc.elements() );
      predefinedPropertyOrder.add( bammc.values() );
      predefinedPropertyOrder.add( bammc.integer() );
      predefinedPropertyOrder.add( bammc.scale() );

      return Comparator.<Property> comparingInt( property ->
                  predefinedPropertyOrder.contains( property ) ?
                        predefinedPropertyOrder.indexOf( property ) :
                        Integer.MAX_VALUE )
            .thenComparing( Property::getLocalName );
   }

   private Comparator<Map.Entry<String, String>> createPredefinedPrefixOrder() {
      final List<String> predefinedPrefixOrder = new ArrayList<>();
      predefinedPrefixOrder.add( "bamm" );
      predefinedPrefixOrder.add( "bamm-c" );
      predefinedPrefixOrder.add( "bamm-e" );
      predefinedPrefixOrder.add( "unit" );
      predefinedPrefixOrder.add( "rdf" );
      predefinedPrefixOrder.add( "rdfs" );
      predefinedPrefixOrder.add( "xsd" );
      predefinedPrefixOrder.add( "" );

      return Comparator.<Map.Entry<String, String>> comparingInt( entry ->
                  predefinedPrefixOrder.contains( entry.getKey() ) ?
                        predefinedPrefixOrder.indexOf( entry.getKey() ) :
                        Integer.MAX_VALUE )
            .thenComparing( Map.Entry::getKey );
   }

   /**
    * Print to the PrintWriter given in the constructor. This method does not close the PrintWriter.
    */
   public void print() {
      prefixMap.entrySet().stream().sorted( prefixOrder ).forEach( entry -> writer.format( "@prefix %s: <%s> .%n", entry.getKey(), entry.getValue() ) );
      writer.println();

      final Resource rootElementResource = ResourceFactory.createResource( rootElementUrn.toString() );
      resourceQueue.add( rootElementResource );
      while ( !resourceQueue.isEmpty() ) {
         final Resource resource = resourceQueue.poll();
         writer.print( processElement( resource, 0 ) );
      }

      model.listSubjects().toSet().stream()
            .filter( RDFNode::isURIResource )
            .filter( resource -> !processedResources.contains( resource ) )
            .map( resource -> processElement( resource, 0 ) )
            .forEach( writer::print );
   }

   private List<Statement> statements( final Resource subject, final Property predicate, final RDFNode object ) {
      return ImmutableList.copyOf( model.listStatements( subject, predicate, object ) );
   }

   private Optional<Statement> statement( final Resource subject, final Property predicate, final RDFNode object ) {
      return statements( subject, predicate, object ).stream().findFirst();
   }

   private Optional<Statement> elementDefinition( final Resource element ) {
      return statement( element, RDF.type, null );
   }

   private String serializeList( final Resource list, final int indentationLevel ) {
      if ( list.isURIResource() && list.getURI().equals( RDF.nil.getURI() ) ) {
         return "( )";
      }

      return list.as( RDFList.class ).asJavaList().stream().map( listNode -> serialize( listNode, indentationLevel ) )
            .collect( Collectors.joining( " ", "( ", " )" ) );
   }

   private String serialize( final RDFNode rdfNode, final int indentationLevel ) {
      if ( rdfNode.isURIResource() && rdfNode.asResource().getURI().equals( RDF.type.getURI() ) ) {
         return "a";
      }

      if ( rdfNode.isResource() ) {
         return serializeResource( rdfNode, indentationLevel );
      }

      if ( rdfNode.isLiteral() ) {
         return serializeLiteral( rdfNode, indentationLevel );
      }

      return PrintUtil.print( rdfNode ).replace( '\'', '"' );
   }

   private String quoteValue( final String value ) {
      final int approximateSize = value.length() <= 50 ? 64 : 256;
      final StringBuilder buffer = new StringBuilder( approximateSize );
      if ( value.contains( System.lineSeparator() ) ) {
         buffer.append( TRIPLE_QUOTE );
         final String[] lines = value.split( LINE_BREAK );
         for ( int i = 0; i < lines.length; i++ ) {
            escapeStringAndAppendToBuilder( lines[i], buffer );
            if ( i != lines.length - 1 ) {
               buffer.append( "\n" );
            }
         }
         buffer.append( TRIPLE_QUOTE );
         return buffer.toString();
      }

      buffer.append( "\"" );
      escapeStringAndAppendToBuilder( value, buffer );
      buffer.append( "\"" );
      return buffer.toString();
   }

   /**
    * Escapes strings for serialization
    *
    * @param input the input string
    */
   private void escapeStringAndAppendToBuilder( final String input, final StringBuilder builder ) {
      final String escapedSpecialCharacters = StringEscapeUtils.escapeJava( input );
      // The following replaces unicode escape sequences such as \uABCD with the corresponding unicode character
      final char[] chars = escapedSpecialCharacters.toCharArray();
      int index = 0;
      while ( index < chars.length ) {
         final boolean indexAtUnicodeEscapeSequence = chars[index] == '\\' &&
               (index + 1) < chars.length &&
               chars[index + 1] == 'u' &&
               (index + 5) <= (chars.length - 1);
         if ( indexAtUnicodeEscapeSequence ) {
            final long codepoint = Long.parseLong( new String( chars, index + 2, 4 ), 16 );
            builder.append( (char) codepoint );
            index += 6;
         } else {
            builder.append( chars[index] );
            index++;
         }
      }
   }

   private String serializeLiteral( final RDFNode rdfNode, final int indentationLevel ) {
      final Literal literal = rdfNode.asLiteral();
      final String value = literal.getLexicalForm();

      final RDFDatatype type = rdfNode.asNode().getLiteralDatatype();
      if ( type == null || type.getURI().equals( XSD.xstring.getURI() ) ) {
         return quoteValue( value );
      }

      if ( type.getURI().equals( XSD.xboolean.getURI() ) || type.getURI().equals( XSD.integer.getURI() ) ) {
         return value;
      }

      if ( type.getURI().equals( RDF.langString.getURI() ) ) {
         return quoteValue( value ) + "@" + literal.getLanguage();
      }

      return quoteValue( value ) + "^^" + serialize( model.getResource( rdfNode.asNode().getLiteralDatatypeURI() ), indentationLevel );
   }

   private String serializeAnonymousNodeWithoutRdfType( final Resource resource, final int indentationLevel ) {
      final List<Statement> statements = statements( resource, null, null );
      if ( statements.isEmpty() ) {
         return "";
      }
      return statements.stream()
            .map( Statement::getPredicate )
            .sorted( propertyOrder )
            .flatMap( property -> statements( resource, property, null ).stream() )
            .distinct()
            .map( statement ->
                  String.format( "%s %s", serialize( statement.getPredicate(), indentationLevel ),
                        serialize( statement.getObject(), indentationLevel ) ) )
            .collect( Collectors.joining( "; ", "[ ", " ]" ) );
   }

   private String serializeResource( final RDFNode rdfNode, final int indentationLevel ) {
      final Resource resource = rdfNode.asResource();

      if ( processedResources.contains( resource ) ) {
         return PrintUtil.print( resource );
      }

      if ( (resource.isURIResource() && resource.getURI().equals( RDF.nil.getURI() ))
            || statements( resource, RDF.first, null ).iterator().hasNext() ) {
         return serializeList( resource, indentationLevel );
      }

      if ( resource.isURIResource() ) {
         if ( elementDefinition( resource ).isPresent() ) {
            resourceQueue.add( resource );
         }

         return PrintUtil.print( resource );
      }

      return processElement( resource, indentationLevel + 1 );
   }

   private String processElement( final Resource element, final int indentationLevel ) {
      if ( processedResources.contains( element ) ) {
         return "";
      }

      final Optional<Statement> elementDefinition = elementDefinition( element );
      if ( elementDefinition.isEmpty() ) {
         return serializeAnonymousNodeWithoutRdfType( element, indentationLevel );
      }

      final String serializedProperty = serialize( elementDefinition.get().getPredicate(), indentationLevel );
      final String serializedObject = serialize( elementDefinition.get().getObject().asResource(), indentationLevel );

      final String firstLine = element.isAnon() ?
            String.format( "[%n%s%s %s", INDENT.repeat( indentationLevel + 1 ), serializedProperty, serializedObject ) :
            String.format( "%s %s %s", serialize( element, indentationLevel ), serializedProperty, serializedObject );

      processedResources.add( element );
      final String body = statements( element, null, null )
            .stream()
            .map( Statement::getPredicate )
            .filter( property -> !property.equals( RDF.type ) )
            .sorted( propertyOrder )
            .flatMap( property -> statements( element, property, null ).stream() )
            .distinct()
            .map( statement -> String.format( "%s%s %s", INDENT.repeat( indentationLevel + 1 ),
                  serialize( statement.getPredicate(), indentationLevel ),
                  serialize( statement.getObject(), indentationLevel ) ) )
            .collect( Collectors.joining( String.format( " ;%n" ), "", (element.isAnon() ?
                  String.format( " %n%s]", INDENT.repeat( indentationLevel ) ) : "") ) );
      if ( body.isEmpty() ) {
         return String.format( "%s .%n%n", firstLine );
      } else {
         final String firstPart = String.format( "%s ;%n%s", firstLine, body );
         if ( body.trim().endsWith( "." ) ) {
            return firstPart;
         }
         if ( body.endsWith( "]" ) && indentationLevel >= 1 ) {
            return firstPart;
         }
         return String.format( (indentationLevel >= 1 ? "%s ;%n" : "%s .%n%n"), firstPart );
      }
   }
}
