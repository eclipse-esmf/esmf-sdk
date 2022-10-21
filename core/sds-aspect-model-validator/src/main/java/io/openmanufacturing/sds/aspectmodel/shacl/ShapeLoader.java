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
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.OrConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.PatternConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.SparqlConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.UniqueLangConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.XoneConstraint;
import io.openmanufacturing.sds.aspectmodel.shacl.path.AlternativePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.InversePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.OneOrMorePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.Path;
import io.openmanufacturing.sds.aspectmodel.shacl.path.PredicatePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.SequencePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.ZeroOrMorePath;
import io.openmanufacturing.sds.aspectmodel.shacl.path.ZeroOrOnePath;

public class ShapeLoader implements Function<Model, List<Shape.Node>> {
   private static final SHACL SHACL = new SHACL();

   /**
    * Encodes the logic of how to build an instance of {@link Constraint} from given RDF predicates on the value node
    */
   private final Map<Property, Function<Statement, Constraint>> constraintLoaders = ImmutableMap.<Property, Function<Statement, Constraint>> builder()
         .put( SHACL.class_(), statement -> new ClassConstraint( statement.getResource() ) )
         .put( SHACL.datatype(), statement -> new DatatypeConstraint( statement.getResource().getURI() ) )
         .put( SHACL.nodeKind(), statement -> new NodeKindConstraint( Shape.NodeKind.valueOf( statement.getResource().getLocalName() ) ) )
         .put( SHACL.minCount(), statement -> new MinCountConstraint( statement.getLiteral().getInt() ) )
         .put( SHACL.maxCount(), statement -> new MaxCountConstraint( statement.getLiteral().getInt() ) )
         .put( SHACL.minExclusive(), statement -> new MinExclusiveConstraint( statement.getLiteral() ) )
         .put( SHACL.minInclusive(), statement -> new MinInclusiveConstraint( statement.getLiteral() ) )
         .put( SHACL.maxExclusive(), statement -> new MaxExclusiveConstraint( statement.getLiteral() ) )
         .put( SHACL.maxInclusive(), statement -> new MaxInclusiveConstraint( statement.getLiteral() ) )
         .put( SHACL.minLength(), statement -> new MinLengthConstraint( statement.getLiteral().getInt() ) )
         .put( SHACL.maxLength(), statement -> new MaxLengthConstraint( statement.getLiteral().getInt() ) )
         .put( SHACL.pattern(), statement -> {
            String flagsString = Optional.ofNullable( statement.getSubject().getProperty( SHACL.flags() ) ).map( Statement::getString ).orElse( "" );
            return new PatternConstraint( buildPattern( statement.getLiteral().getString(), flagsString ) );
         } )
         .put( SHACL.languageIn(), statement ->
               new AllowedLanguagesConstraint( statement.getResource().as( RDFList.class ).mapWith( rdfNode -> rdfNode.asLiteral().getString() ).toList() ) )
         .put( SHACL.uniqueLang(), statement -> new UniqueLangConstraint() )
         .put( SHACL.equals(), statement -> new EqualsConstraint( statement.getModel().createProperty( statement.getResource().getURI() ) ) )
         .put( SHACL.disjoint(), statement -> new DisjointConstraint( statement.getModel().createProperty( statement.getResource().getURI() ) ) )
         .put( SHACL.lessThan(), statement -> new LessThanConstraint( statement.getModel().createProperty( statement.getResource().getURI() ) ) )
         .put( SHACL.lessThanOrEquals(),
               statement -> new LessThanOrEqualsConstraint( statement.getModel().createProperty( statement.getResource().getURI() ) ) )
         .put( SHACL.not(), statement -> new NotConstraint( constraints( statement.getObject().asResource() ).get( 0 ) ) )
         .put( SHACL.and(), statement -> new AndConstraint( nestedConstraintList( statement ) ) )
         .put( SHACL.or(), statement -> new OrConstraint( nestedConstraintList( statement ) ) )
         .put( SHACL.xone(), statement -> new XoneConstraint( nestedConstraintList( statement ) ) )
         .put( SHACL.node(), statement -> new NodeConstraint( nodeShape( statement.getObject().asResource() ) ) )
         .put( SHACL.in(), statement -> new AllowedValuesConstraint( statement.getResource().as( RDFList.class ).asJavaList() ) )
         .put( SHACL.closed(), statement -> {
            boolean closed = statement.getBoolean();
            if ( !closed ) {
               throw new RuntimeException();
            }
            Set<Property> ignoredProperties = statement.getSubject().getProperty( SHACL.ignoredProperties() ).getResource()
                  .as( RDFList.class )
                  .asJavaList()
                  .stream()
                  .map( RDFNode::asResource )
                  .map( Resource::getURI )
                  .map( uri -> statement.getModel().createProperty( uri ) )
                  .collect( Collectors.toSet() );
            return new ClosedConstraint( ignoredProperties );
         } )
         .put( SHACL.hasValue(), statement -> new HasValueConstraint( statement.getObject() ) )
         .put( SHACL.sparql(), statement -> {
            final Resource constraintNode = statement.getResource();
            final String message = Optional.ofNullable( constraintNode.getProperty( SHACL.message() ) ).map( Statement::getString ).orElse( "" );
            return new SparqlConstraint( message, sparqlQuery( constraintNode ) );
         } )
         .build();

   private List<Constraint> nestedConstraintList( final Statement statement ) {
      return statement.getObject().as( RDFList.class ).asJavaList().stream()
            .filter( RDFNode::isResource )
            .map( RDFNode::asResource )
            .flatMap( resource -> resource.isURIResource()
                  ? nodeShape( resource ).attributes().constraints().stream()
                  : shapeAttributes( resource ).constraints().stream() )
            .toList();
   }

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
      return Streams.stream( model.listStatements( null, RDF.type, SHACL.NodeShape() ) )
            .map( Statement::getSubject )
            .map( this::nodeShape )
            .toList();
   }

   private Shape.Attributes shapeAttributes( final Resource shapeNode ) {
      final Optional<String> uri = Optional.ofNullable( shapeNode.getURI() );
      final Optional<Resource> targetNode = Optional.ofNullable( shapeNode.getProperty( SHACL.targetNode() ) ).map( Statement::getResource );
      final Optional<Resource> targetClass = Optional.ofNullable( shapeNode.getProperty( SHACL.targetClass() ) ).map( Statement::getResource );
      final Optional<Property> targetObjectsOf = Optional.ofNullable( shapeNode.getProperty( SHACL.targetObjectsOf() ) )
            .map( Statement::getResource )
            .map( Resource::getURI )
            .map( propertyUri -> shapeNode.getModel().createProperty( propertyUri ) );
      final Optional<Property> targetSubjectsOf = Optional.ofNullable( shapeNode.getProperty( SHACL.targetSubjectsOf() ) )
            .map( Statement::getResource )
            .map( Resource::getURI )
            .map( propertyUri -> shapeNode.getModel().createProperty( propertyUri ) );
      final Optional<Query> targetSparql = Optional.ofNullable( shapeNode.getProperty( SHACL.target() ) )
            .map( Statement::getResource )
            .map( this::sparqlQuery );
      final Optional<String> name = Optional.ofNullable( shapeNode.getProperty( SHACL.name() ) ).map( Statement::getString );
      final Optional<String> description = Optional.ofNullable( shapeNode.getProperty( SHACL.description() ) ).map( Statement::getString );
      final Optional<Integer> order = Optional.ofNullable( shapeNode.getProperty( SHACL.order() ) ).map( Statement::getInt );
      final Optional<String> group = Optional.ofNullable( shapeNode.getProperty( SHACL.group() ) ).map( Statement::getString );
      final Optional<RDFNode> defaultValue = Optional.ofNullable( shapeNode.getProperty( SHACL.defaultValue() ) ).map( Statement::getObject );
      final boolean deactivated = Optional.ofNullable( shapeNode.getProperty( SHACL.deactivated() ) ).map( Statement::getBoolean ).orElse( false );
      final Optional<String> message = Optional.ofNullable( shapeNode.getProperty( SHACL.message() ) ).map( Statement::getString );
      final Shape.Severity severity = severity( shapeNode );
      final List<Constraint> constraints = constraints( shapeNode );
      return new Shape.Attributes( uri, targetNode, targetClass, targetObjectsOf, targetSubjectsOf, targetSparql, name, description, order, group,
            defaultValue, deactivated, message, severity, constraints );
   }

   private Shape.Node nodeShape( final Resource shapeNode ) {
      final Shape.Attributes attributes = shapeAttributes( shapeNode );
      final List<Shape.Property> properties = Streams.stream( shapeNode.listProperties( SHACL.property() ) )
            .map( Statement::getResource )
            .map( this::propertyShape ).toList();
      return new Shape.Node( attributes, properties );
   }

   private Shape.Property propertyShape( final Resource shapeNode ) {
      final Shape.Attributes attributes = shapeAttributes( shapeNode );
      final Path path = path( shapeNode.getProperty( SHACL.path() ).getResource() );
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
      final Statement alternativeStatement = pathNode.getProperty( SHACL.alternativePath() );
      if ( alternativeStatement != null ) {
         final List<Path> alternatives = pathList( alternativeStatement.getResource() );
         return new AlternativePath( alternatives.get( 0 ), alternatives.get( 1 ) );
      }

      final Statement inverseStatement = pathNode.getProperty( SHACL.inversePath() );
      if ( inverseStatement != null ) {
         return new InversePath( path( inverseStatement.getResource() ) );
      }

      final Statement zeroOrMoreStatement = pathNode.getProperty( SHACL.zeroOrMorePath() );
      if ( zeroOrMoreStatement != null ) {
         return new ZeroOrMorePath( path( zeroOrMoreStatement.getResource() ) );
      }

      final Statement oneOrMoreStatement = pathNode.getProperty( SHACL.oneOrMorePath() );
      if ( oneOrMoreStatement != null ) {
         return new OneOrMorePath( path( oneOrMoreStatement.getResource() ) );
      }

      final Statement zeroOrOneStatement = pathNode.getProperty( SHACL.zeroOrOnePath() );
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

   private Shape.Severity severity( final Resource shapeNode ) {
      return Optional.ofNullable( shapeNode.getProperty( SHACL.severity() ) )
            .map( Statement::getResource )
            .map( severity -> {
               if ( severity.equals( SHACL.Info() ) ) {
                  return Shape.Severity.INFO;
               }
               if ( severity.equals( SHACL.Warning() ) ) {
                  return Shape.Severity.WARNING;
               }
               return Shape.Severity.VIOLATION;
            } )
            .orElse( Shape.Severity.VIOLATION );
   }

   private Query sparqlQuery( final Resource target ) {
      final Map<String, String> prefixMap = Optional.ofNullable( target.getProperty( SHACL.prefixes() ) )
            .map( Statement::getResource )
            .map( this::prefixDeclarations )
            .orElse( Map.of() );
      final String queryString = target.getProperty( SHACL.select() ).getString();
      final ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString();
      parameterizedSparqlString.setCommandText( queryString );
      parameterizedSparqlString.setNsPrefixes( prefixMap );
      // Although the SPARQLTarget's query returns a variable "$this" do NOT parameterizedSparqlString.setIRI() for "$this" here.
      // See https://www.w3.org/TR/shacl-af/#SPARQLTarget
      return parameterizedSparqlString.asQuery();
   }

   private Map<String, String> prefixDeclarations( final Resource prefixDeclarations ) {
      final Map<String, String> result = new HashMap<>();
      for ( final StmtIterator it = prefixDeclarations.getModel().listStatements( prefixDeclarations, SHACL.declare(), (RDFNode) null ); it.hasNext(); ) {
         final Statement statement = it.next();
         final Resource declaration = statement.getResource();
         final String prefix = declaration.getProperty( SHACL.prefix() ).getString();
         final String namespace = declaration.getProperty( SHACL.namespace() ).getLiteral().getLexicalForm();
         result.put( prefix, namespace );
      }
      return result;
   }
}
