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

package io.openmanufacturing.sds.aspectmodel.shacl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shacl.validation.Severity;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

import io.openmanufacturing.sds.aspectmodel.shacl.constraint.AllowedLanguagesConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.AllowedValuesConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.AndConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.ClassConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.ClosedConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.Constraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.DatatypeConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.DisjointConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.EqualsConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.HasValueConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.LessThanConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.LessThanOrEqualsConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MaxCountConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MaxExclusiveConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MaxInclusiveConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MaxLengthConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MinCountConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MinExclusiveConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MinInclusiveConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.MinLengthConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.NodeConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.NodeKindConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.NotConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.PatternConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.SparqlConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.UniqueLangConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.path.AlternativePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.InversePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.OneOrMorePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.Path;
import io.openmanufacturing.sds.aspectmodel.shacl.path.PredicatePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.SequencePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.ZeroOrMorePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.ZeroOrOnePath;

public class ShapeLoader implements Function<Model, List<Shape.Node>> {
   /**
    * Encodes the logic of how to build an instance of {@link Constraint} from given RDF predicates on the value node
    */
   private final Map<Property, Function<Statement, Constraint>> constraintLoaders = ImmutableMap.<Property, Function<Statement, Constraint>> builder()
         .put( SHACLM.class_, statement -> new ClassConstraint( statement.getResource() ) )
         .put( SHACLM.datatype, statement -> new DatatypeConstraint( statement.getResource().getURI() ) )
         .put( SHACLM.nodeKind, statement -> new NodeKindConstraint( Shape.NodeKind.valueOf( statement.getResource().getLocalName() ) ) )
         .put( SHACLM.minCount, statement -> new MinCountConstraint( statement.getLiteral().getInt() ) )
         .put( SHACLM.maxCount, statement -> new MaxCountConstraint( statement.getLiteral().getInt() ) )
         .put( SHACLM.minExclusive, statement -> new MinExclusiveConstraint( statement.getLiteral() ) )
         .put( SHACLM.minInclusive, statement -> new MinInclusiveConstraint( statement.getLiteral() ) )
         .put( SHACLM.maxExclusive, statement -> new MaxExclusiveConstraint( statement.getLiteral() ) )
         .put( SHACLM.maxInclusive, statement -> new MaxInclusiveConstraint( statement.getLiteral() ) )
         .put( SHACLM.minLength, statement -> new MinLengthConstraint( statement.getLiteral().getInt() ) )
         .put( SHACLM.maxLength, statement -> new MaxLengthConstraint( statement.getLiteral().getInt() ) )
         .put( SHACLM.pattern, statement -> {
            String flagsString = Optional.ofNullable( statement.getSubject().getProperty( SHACLM.flags ) ).map( Statement::getString ).orElse( "" );
            return new PatternConstraint( buildPattern( statement.getLiteral().getString(), flagsString ) );
         } )
         .put( SHACLM.languageIn, statement ->
               new AllowedLanguagesConstraint( statement.getResource().as( RDFList.class ).mapWith( rdfNode -> rdfNode.asLiteral().getString() ).toList() ) )
         .put( SHACLM.uniqueLang, statement -> new UniqueLangConstraint() )
         .put( SHACLM.equals, statement -> new EqualsConstraint( statement.getModel().createProperty( statement.getResource().getURI() ) ) )
         .put( SHACLM.disjoint, statement -> new DisjointConstraint( statement.getModel().createProperty( statement.getResource().getURI() ) ) )
         .put( SHACLM.lessThan, statement -> new LessThanConstraint( statement.getModel().createProperty( statement.getResource().getURI() ) ) )
         .put( SHACLM.lessThanOrEquals, statement -> new LessThanOrEqualsConstraint( statement.getModel().createProperty( statement.getResource().getURI() ) ) )
         .put( SHACLM.not, statement -> new NotConstraint( constraints( statement.getObject().asResource() ).get( 0 ) ) )
         .put( SHACLM.and, statement -> new AndConstraint( statement.getObject().as( RDFList.class ).asJavaList().stream()
               .filter( RDFNode::isResource )
               .map( RDFNode::asResource )
               .flatMap( resource -> resource.isURIResource()
                     ? nodeShape( resource ).attributes().constraints().stream()
                     : shapeAttributes( resource ).constraints().stream() )
               .collect( Collectors.toList() ) ) )
         .put( SHACLM.node, statement -> new NodeConstraint( nodeShape( statement.getObject().asResource() ) ) )
         .put( SHACLM.in, statement -> new AllowedValuesConstraint( statement.getResource().as( RDFList.class ).asJavaList() ) )
         .put( SHACLM.closed, statement -> {
            boolean closed = statement.getBoolean();
            if ( !closed ) {
               throw new RuntimeException();
            }
            Set<Property> ignoredProperties = statement.getSubject().getProperty( SHACLM.ignoredProperties ).getResource()
                  .as( RDFList.class )
                  .asJavaList()
                  .stream()
                  .map( RDFNode::asResource )
                  .map( Resource::getURI )
                  .map( uri -> statement.getModel().createProperty( uri ) )
                  .collect( Collectors.toSet() );
            return new ClosedConstraint( ignoredProperties );
         } )
         .put( SHACLM.hasValue, statement -> new HasValueConstraint( statement.getObject() ) )
         .put( SHACLM.sparql, statement -> {
            final Resource constraintNode = statement.getResource();
            final String message = Optional.ofNullable( constraintNode.getProperty( SHACLM.message ) ).map( Statement::getString ).orElse( "" );
            return new SparqlConstraint( message, sparqlQuery( constraintNode ) );
         } )
         .build();

   /**
    * Builds a {@link Pattern} from a pattern string and a flags string as specified in
    * <a href="https://www.w3.org/TR/xpath-functions/#func-matches">xpath functions</a>.
    * @param patternString the pattern string
    * @param flagsString the flags string
    * @return the pattern
    */
   private Pattern buildPattern( final String patternString, final String flagsString ) {
      int flags = 0;
      if ( flagsString.contains( "s" ) ) {
         flags |= Pattern.DOTALL;
      }
      if ( flagsString.contains( "m" ) ) {
         flags |= Pattern.MULTILINE;
      }
      if ( flagsString.contains( "i" ) ) {
         flags |= Pattern.CASE_INSENSITIVE;
      }
      if ( flagsString.contains( "q" ) ) {
         flags |= Pattern.LITERAL;
      }
      return Pattern.compile( patternString, flags );
   }

   @Override
   public List<Shape.Node> apply( final Model model ) {
      return Streams.stream( model.listStatements( null, RDF.type, SHACLM.NodeShape ) )
            .map( Statement::getSubject )
            .map( this::nodeShape )
            .toList();
   }

   private Shape.Attributes shapeAttributes( final Resource shapeNode ) {
      final Optional<String> uri = Optional.ofNullable( shapeNode.getURI() );
      final Optional<Resource> targetNode = Optional.ofNullable( shapeNode.getProperty( SHACLM.targetNode ) ).map( Statement::getResource );
      final Optional<Resource> targetClass = Optional.ofNullable( shapeNode.getProperty( SHACLM.targetClass ) ).map( Statement::getResource );
      final Optional<Property> targetObjectsOf = Optional.ofNullable( shapeNode.getProperty( SHACLM.targetObjectsOf ) )
            .map( Statement::getResource )
            .map( Resource::getURI )
            .map( propertyUri -> shapeNode.getModel().createProperty( propertyUri ) );
      final Optional<Property> targetSubjectsOf = Optional.ofNullable( shapeNode.getProperty( SHACLM.targetSubjectsOf ) )
            .map( Statement::getResource )
            .map( Resource::getURI )
            .map( propertyUri -> shapeNode.getModel().createProperty( propertyUri ) );
      final Optional<Query> targetSparql = Optional.ofNullable( shapeNode.getProperty( SHACLM.target ) )
            .map( Statement::getResource )
            .map( this::sparqlQuery );
      final Optional<String> name = Optional.ofNullable( shapeNode.getProperty( SHACLM.name ) ).map( Statement::getString );
      final Optional<String> description = Optional.ofNullable( shapeNode.getProperty( SHACLM.description ) ).map( Statement::getString );
      final Optional<Integer> order = Optional.ofNullable( shapeNode.getProperty( SHACLM.order ) ).map( Statement::getInt );
      final Optional<String> group = Optional.ofNullable( shapeNode.getProperty( SHACLM.group ) ).map( Statement::getString );
      final Optional<RDFNode> defaultValue = Optional.ofNullable( shapeNode.getProperty( SHACLM.defaultValue ) ).map( Statement::getObject );
      final boolean deactivated = Optional.ofNullable( shapeNode.getProperty( SHACLM.deactivated ) ).map( Statement::getBoolean ).orElse( false );
      final Optional<String> message = Optional.ofNullable( shapeNode.getProperty( SHACLM.message ) ).map( Statement::getString );
      final Severity severity = severity( shapeNode );
      final List<Constraint> constraints = constraints( shapeNode );
      return new Shape.Attributes( uri, targetNode, targetClass, targetObjectsOf, targetSubjectsOf, targetSparql, name, description, order, group,
            defaultValue, deactivated, message, severity, constraints );
   }

   private Shape.Node nodeShape( final Resource shapeNode ) {
      final Shape.Attributes attributes = shapeAttributes( shapeNode );
      final List<Shape.Property> properties = Streams.stream( shapeNode.listProperties( SHACLM.property ) )
            .map( Statement::getResource )
            .map( this::propertyShape ).toList();
      return new Shape.Node( attributes, properties );
   }

   private Shape.Property propertyShape( final Resource shapeNode ) {
      final Shape.Attributes attributes = shapeAttributes( shapeNode );
      final Path path = path( shapeNode.getProperty( SHACLM.path ).getResource() );
      return new Shape.Property( attributes, path );
   }

   private List<Path> pathList( final Resource listNode ) {
      return listNode.as( RDFList.class ).asJavaList().stream()
            .filter( RDFNode::isResource )
            .map( RDFNode::asResource )
            .map( this::path )
            .collect( Collectors.toList() );
   }

   private Path path( final Resource pathNode ) {
      if ( isRdfList( pathNode ) ) {
         final List<Path> subPaths = pathList( pathNode );
         return new SequencePath( subPaths );
      }

      if ( pathNode.isURIResource() ) {
         return new PredicatePath( pathNode.getModel().createProperty( pathNode.getURI() ) );
      }

      // pathNode is a blank node but not RDF list
      final Statement alternativeStatement = pathNode.getProperty( SHACLM.alternativePath );
      if ( alternativeStatement != null ) {
         final List<Path> alternatives = pathList( alternativeStatement.getResource() );
         return new AlternativePath( alternatives.get( 0 ), alternatives.get( 1 ) );
      }

      final Statement inverseStatement = pathNode.getProperty( SHACLM.inversePath );
      if ( inverseStatement != null ) {
         return new InversePath( path( inverseStatement.getResource() ) );
      }

      final Statement zeroOrMoreStatement = pathNode.getProperty( SHACLM.zeroOrMorePath );
      if ( zeroOrMoreStatement != null ) {
         return new ZeroOrMorePath( path( zeroOrMoreStatement.getResource() ) );
      }

      final Statement oneOrMoreStatement = pathNode.getProperty( SHACLM.oneOrMorePath );
      if ( oneOrMoreStatement != null ) {
         return new OneOrMorePath( path( oneOrMoreStatement.getResource() ) );
      }

      final Statement zeroOrOneStatement = pathNode.getProperty( SHACLM.zeroOrOnePath );
      if ( zeroOrOneStatement != null ) {
         return new ZeroOrOnePath( path( zeroOrOneStatement.getResource() ) );
      }
      throw new RuntimeException( "Invalid path: " + PrintUtil.print( pathNode ) );
   }

   private boolean isRdfList( final Resource resource ) {
      return (resource.isURIResource() && resource.getURI().equals( RDF.nil.getURI() ))
            || resource.hasProperty( RDF.rest ) || resource.hasProperty( RDF.first );
   }

   private List<Constraint> constraints( final Resource valueNode ) {
      return constraintLoaders.entrySet().stream()
            .flatMap( entry -> {
               try {
                  return Streams.stream( valueNode.listProperties( entry.getKey() ) )
                        .map( statement -> entry.getValue().apply( statement ) );
               } catch ( final Exception exception ) {
                  throw new RuntimeException( "Could not load SHACL shape: Invalid use of " + entry.getKey() + " on " + valueNode );
               }
            } )
            .collect( Collectors.toList() );
   }

   private Severity severity( final Resource shapeNode ) {
      return Optional.ofNullable( shapeNode.getProperty( SHACLM.severity ) )
            .map( Statement::getResource )
            .map( Resource::asNode )
            .map( Severity::create )
            .orElse( Severity.Violation );
   }

   private Query sparqlQuery( final Resource target ) {
      final Map<String, String> prefixMap = Optional.ofNullable( target.getProperty( SHACLM.prefixes ) )
            .map( Statement::getResource )
            .map( this::prefixDeclarations )
            .orElse( Map.of() );
      final String queryString = target.getProperty( SHACLM.select ).getString();
      final ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString();
      parameterizedSparqlString.setCommandText( queryString );
      parameterizedSparqlString.setNsPrefixes( prefixMap );
      // Although the SPARQLTarget's query returns a variable "$this" do NOT parameterizedSparqlString.setIRI() for "$this" here.
      // See https://www.w3.org/TR/shacl-af/#SPARQLTarget
      return parameterizedSparqlString.asQuery();
   }

   private Map<String, String> prefixDeclarations( final Resource prefixDeclarations ) {
      final Map<String, String> result = new HashMap<>();
      for ( final StmtIterator it = prefixDeclarations.getModel().listStatements( prefixDeclarations, SHACLM.declare, (RDFNode) null ); it.hasNext(); ) {
         final Statement statement = it.next();
         final Resource declaration = statement.getResource();
         final String prefix = declaration.getProperty( SHACLM.prefix ).getString();
         final String namespace = declaration.getProperty( SHACLM.namespace ).getLiteral().getLexicalForm();
         result.put( prefix, namespace );
      }
      return result;
   }
}
