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

package org.eclipse.esmf.aspectmodel.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.ImmutableList;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.BlankNodeId;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeVisitor;
import org.apache.jena.graph.Node_ANY;
import org.apache.jena.graph.Node_Blank;
import org.apache.jena.graph.Node_Graph;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_Triple;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.Util;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

/**
 * Serializes an {@link AspectModelFile} to RDF/Turtle while following the formatting rules for Aspect models.
 * The model is expected to contain exactly one Aspect.
 */
public class PrettyPrinter {
   private static final String INDENT = "   ";
   private static final String TRIPLE_QUOTE = "\"\"\"";
   private static final String LINE_BREAK = "\n";
   private final Comparator<Property> propertyOrder;
   private final Comparator<Map.Entry<String, String>> prefixOrder;
   private final Set<Resource> processedResources = new HashSet<>();
   private final Queue<Resource> resourceQueue = new ArrayDeque<>();

   private final List<ModelElement> elementsToWrite;
   private final List<String> headerComment;
   private final Model model;
   private final PrintWriter writer;

   private final PrintVisitor printVisitor;

   /**
    * Creates a new Pretty Printer for a given Aspect Model File.
    *
    * @param modelFile the model file to pretty print
    * @param writer the writer to write to
    */
   public PrettyPrinter( final AspectModelFile modelFile, final PrintWriter writer ) {
      elementsToWrite = modelFile.elements();
      headerComment = modelFile.headerComment();
      this.writer = writer;
      model = ModelFactory.createDefaultModel();
      model.add( modelFile.sourceModel() );
      RdfUtil.cleanPrefixes( model );

      propertyOrder = createPredefinedPropertyOrder();
      prefixOrder = createPredefinedPrefixOrder();
      printVisitor = new PrintVisitor( model );
   }

   private Comparator<Property> createPredefinedPropertyOrder() {
      final List<Property> predefinedPropertyOrder = new ArrayList<>();
      predefinedPropertyOrder.add( SammNs.SAMM._extends() );
      predefinedPropertyOrder.add( SammNs.SAMM.preferredName() );
      predefinedPropertyOrder.add( SammNs.SAMM.description() );
      predefinedPropertyOrder.add( SammNs.SAMM.see() );
      predefinedPropertyOrder.add( SammNs.SAMM.characteristic() );
      predefinedPropertyOrder.add( SammNs.SAMM.properties() );
      predefinedPropertyOrder.add( SammNs.SAMM.operations() );
      predefinedPropertyOrder.add( SammNs.SAMM.events() );
      predefinedPropertyOrder.add( SammNs.SAMM.input() );
      predefinedPropertyOrder.add( SammNs.SAMM.output() );
      predefinedPropertyOrder.add( SammNs.SAMM.dataType() );
      predefinedPropertyOrder.add( SammNs.SAMM.exampleValue() );
      predefinedPropertyOrder.add( SammNs.SAMM.value() );
      predefinedPropertyOrder.add( SammNs.SAMM.property() );
      predefinedPropertyOrder.add( SammNs.SAMM.optional() );

      predefinedPropertyOrder.add( SammNs.SAMMC.baseCharacteristic() );
      predefinedPropertyOrder.add( SammNs.SAMMC.languageCode() );
      predefinedPropertyOrder.add( SammNs.SAMMC.localeCode() );
      predefinedPropertyOrder.add( SammNs.SAMMC.left() );
      predefinedPropertyOrder.add( SammNs.SAMMC.right() );
      predefinedPropertyOrder.add( SammNs.SAMMC.minValue() );
      predefinedPropertyOrder.add( SammNs.SAMMC.maxValue() );
      predefinedPropertyOrder.add( SammNs.SAMMC.lowerBoundDefinition() );
      predefinedPropertyOrder.add( SammNs.SAMMC.upperBoundDefinition() );
      predefinedPropertyOrder.add( SammNs.SAMMC.defaultValue() );
      predefinedPropertyOrder.add( SammNs.SAMMC.unit() );
      predefinedPropertyOrder.add( SammNs.SAMMC.left() );
      predefinedPropertyOrder.add( SammNs.SAMMC.right() );
      predefinedPropertyOrder.add( SammNs.SAMMC.deconstructionRule() );
      predefinedPropertyOrder.add( SammNs.SAMMC.elements() );
      predefinedPropertyOrder.add( SammNs.SAMMC.values() );
      predefinedPropertyOrder.add( SammNs.SAMMC.integer() );
      predefinedPropertyOrder.add( SammNs.SAMMC.scale() );

      return Comparator.<Property> comparingInt( property ->
                  predefinedPropertyOrder.contains( property )
                        ? predefinedPropertyOrder.indexOf( property )
                        : Integer.MAX_VALUE )
            .thenComparing( Property::getLocalName );
   }

   private Comparator<Map.Entry<String, String>> createPredefinedPrefixOrder() {
      final List<String> predefinedPrefixOrder = new ArrayList<>();
      predefinedPrefixOrder.add( "samm" );
      predefinedPrefixOrder.add( "samm-c" );
      predefinedPrefixOrder.add( "samm-e" );
      predefinedPrefixOrder.add( "unit" );
      predefinedPrefixOrder.add( "rdf" );
      predefinedPrefixOrder.add( "rdfs" );
      predefinedPrefixOrder.add( "xsd" );
      predefinedPrefixOrder.add( "" );

      return Comparator.<Map.Entry<String, String>> comparingInt( entry ->
                  predefinedPrefixOrder.contains( entry.getKey() )
                        ? predefinedPrefixOrder.indexOf( entry.getKey() )
                        : Integer.MAX_VALUE )
            .thenComparing( Map.Entry::getKey );
   }

   private void showMilestoneBanner() {
      final Properties properties = new Properties();
      final InputStream propertiesResource = PrettyPrinter.class.getClassLoader().getResourceAsStream( "pom.properties" );
      if ( propertiesResource == null ) {
         return;
      }
      try {
         properties.load( propertiesResource );
      } catch ( final IOException exception ) {
         return;
      }

      final String version = properties.get( "aspect-meta-model-version" ).toString();
      if ( version.contains( "-M" ) ) {
         writer.println( "# This model was created using SAMM version " + version + " and is not intended for productive usage." );
         writer.println();
      }
   }

   /**
    * Print to the PrintWriter given in the constructor. This method does not close the PrintWriter.
    */
   public void print() {
      for ( final String line : headerComment ) {
         writer.println( "# " + line );
      }
      if ( headerComment.size() > 1 ) {
         writer.println();
      }

      showMilestoneBanner();

      model.getNsPrefixMap().entrySet().stream().sorted( prefixOrder )
            .forEach( entry -> writer.format( "@prefix %s: <%s> .%n", entry.getKey(), entry.getValue() ) );
      writer.println();

      // Write Aspects first, all other elements afterwards
      elementsToWrite.stream()
            .filter( element -> element.is( Aspect.class ) )
            .filter( element -> !element.isAnonymous() )
            .map( ModelElement::urn )
            .map( urn -> ResourceFactory.createResource( urn.toString() ) )
            .forEach( resourceQueue::add );
      elementsToWrite.stream()
            .filter( element -> !element.isAnonymous() )
            .filter( element -> !element.is( Aspect.class ) )
            .map( ModelElement::urn )
            .map( urn -> ResourceFactory.createResource( urn.toString() ) )
            .forEach( resourceQueue::add );

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

      final List<RDFNode> listContent = list.as( RDFList.class ).asJavaList();
      final long anonymousNodesInList = listContent.stream().filter( RDFNode::isAnon ).count();
      if ( listContent.size() <= 3 && anonymousNodesInList <= 1 ) {
         return listContent.stream()
               .map( listNode -> serialize( listNode, indentationLevel ) )
               .collect( Collectors.joining( " ", "( ", " )" ) );
      }
      final StringBuilder builder = new StringBuilder();
      builder.append( "(\n" );
      int i = 0;
      for ( final RDFNode listNode : listContent ) {
         builder.append( INDENT.repeat( indentationLevel + 2 ) );
         builder.append( serialize( listNode, indentationLevel + 2 ) );
         i++;
         if ( i < listContent.size() ) {
            builder.append( "\n" );
         }
      }
      builder.append( "\n" );
      builder.append( INDENT.repeat( indentationLevel + 1 ) );
      builder.append( ")" );
      return builder.toString();
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

      return print( rdfNode ).replace( '\'', '"' );
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
         final boolean indexAtUnicodeEscapeSequence = chars[index] == '\\'
               && ( index + 1 ) < chars.length
               && chars[index + 1] == 'u'
               && ( index + 5 ) <= ( chars.length - 1 );
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
         return print( resource );
      }

      if ( ( resource.isURIResource() && resource.getURI().equals( RDF.nil.getURI() ) )
            || statements( resource, RDF.first, null ).iterator().hasNext() ) {
         return serializeList( resource, indentationLevel );
      }

      if ( resource.isURIResource() ) {
         if ( elementDefinition( resource ).isPresent() ) {
            resourceQueue.add( resource );
         }

         return print( resource );
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

      final String firstLine = element.isAnon()
            ? String.format( "[%n%s%s %s", INDENT.repeat( indentationLevel + 1 ), serializedProperty, serializedObject )
            : String.format( "%s %s %s", serialize( element, indentationLevel ), serializedProperty, serializedObject );

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
            .collect( Collectors.joining( String.format( " ;%n" ), "", ( element.isAnon()
                  ? String.format( " %n%s]", INDENT.repeat( indentationLevel ) )
                  : "" ) ) );
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
         return String.format( ( indentationLevel >= 1 ? "%s ;%n" : "%s .%n%n" ), firstPart );
      }
   }

   private String print( final RDFNode node ) {
      if ( node == null ) {
         return "null";
      }
      return (String) node.asNode().visitWith( printVisitor );
   }

   record PrintVisitor( Model model ) implements NodeVisitor {
      @Override
      public Object visitAny( final Node_ANY it ) {
         return "*";
      }

      @Override
      public Object visitBlank( final Node_Blank it, final BlankNodeId id ) {
         return it.toString();
      }

      @Override
      public Object visitLiteral( final Node_Literal it, final LiteralLabel lit ) {
         String lf = it.getLiteralLexicalForm();
         final String singleQuote = "'";
         if ( lf.contains( singleQuote ) ) {
            lf = lf.replace( singleQuote, "\\'" );
         }
         // RDF 1.1 : Print xsd:string without ^^xsd:string
         return singleQuote + lf + singleQuote + ( Util.isSimpleString( it ) ? "" : "^^" + it.getLiteralDatatypeURI() );
      }

      @Override
      public Object visitURI( final Node_URI it, final String uri ) {
         final String suri = model.shortForm( uri );
         if ( uri.equals( suri ) ) {
            return "<" + uri + ">";
         } else {
            return suri;
         }
      }

      @Override
      public Object visitVariable( final Node_Variable it, final String name ) {
         return it.toString();
      }

      @Override
      public Object visitTriple( final Node_Triple it, final Triple triple ) {
         return it.toString();
      }

      @Override
      public Object visitGraph( final Node_Graph it, final Graph graph ) {
         return it.toString();
      }
   }
}
