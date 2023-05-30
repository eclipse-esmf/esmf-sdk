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

package org.eclipse.esmf.aspectmodel.shacl;

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
import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedLanguagesConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedValuesConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.AndConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.ClassConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.ClosedConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.Constraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DatatypeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.DisjointConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.EqualsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.HasValueConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.JsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.LessThanConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.LessThanOrEqualsConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxExclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxInclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxLengthConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinCountConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinExclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinInclusiveConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.MinLengthConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeKindConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NotConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.OrConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.PatternConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.SparqlConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.UniqueLangConstraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.XoneConstraint;
import org.eclipse.esmf.aspectmodel.shacl.path.AlternativePath;
import org.eclipse.esmf.aspectmodel.shacl.path.InversePath;
import org.eclipse.esmf.aspectmodel.shacl.path.OneOrMorePath;
import org.eclipse.esmf.aspectmodel.shacl.path.Path;
import org.eclipse.esmf.aspectmodel.shacl.path.PredicatePath;
import org.eclipse.esmf.aspectmodel.shacl.path.SequencePath;
import org.eclipse.esmf.aspectmodel.shacl.path.ZeroOrMorePath;
import org.eclipse.esmf.aspectmodel.shacl.path.ZeroOrOnePath;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

/**
 * Takes an RDF model describing one or more SHACL shapes as input and parses them into {@link Shape}s
 */
public class ShapeLoader implements Function<Model, List<Shape.Node>> {
   private static final SHACL SHACL = new SHACL();

   /**
    * When constraints are instantiated for a shape, the context is passed as input
    * @param path If the parent shape of the constraint is a property shape, the path determines what the property shape refers to
    */
   private record ShapeContext(Statement statement, Optional<Path> path) {
   }

   /**
    * Encodes the logic of how to build an instance of {@link Constraint} from given RDF predicates on the value node
    */
   private final Map<Property, Function<ShapeContext, Constraint>> constraintLoaders = ImmutableMap.<Property, Function<ShapeContext, Constraint>> builder()
         .put( SHACL.class_(), context -> new ClassConstraint( context.statement().getResource() ) )
         .put( SHACL.datatype(), context -> new DatatypeConstraint( context.statement().getResource().getURI() ) )
         .put( SHACL.nodeKind(), context -> new NodeKindConstraint( Shape.NodeKind.valueOf( context.statement().getResource().getLocalName() ) ) )
         .put( SHACL.minCount(), context -> new MinCountConstraint( context.statement().getLiteral().getInt() ) )
         .put( SHACL.maxCount(), context -> new MaxCountConstraint( context.statement().getLiteral().getInt() ) )
         .put( SHACL.minExclusive(), context -> new MinExclusiveConstraint( context.statement().getLiteral() ) )
         .put( SHACL.minInclusive(), context -> new MinInclusiveConstraint( context.statement().getLiteral() ) )
         .put( SHACL.maxExclusive(), context -> new MaxExclusiveConstraint( context.statement().getLiteral() ) )
         .put( SHACL.maxInclusive(), context -> new MaxInclusiveConstraint( context.statement().getLiteral() ) )
         .put( SHACL.minLength(), context -> new MinLengthConstraint( context.statement().getLiteral().getInt() ) )
         .put( SHACL.maxLength(), context -> new MaxLengthConstraint( context.statement().getLiteral().getInt() ) )
         .put( SHACL.pattern(), context -> {
            String flagsString = Optional.ofNullable( context.statement().getSubject().getProperty( SHACL.flags() ) ).map( Statement::getString ).orElse( "" );
            return new PatternConstraint( buildPattern( context.statement().getLiteral().getString(), flagsString ) );
         } )
         .put( SHACL.languageIn(), context ->
               new AllowedLanguagesConstraint(
                     context.statement().getResource().as( RDFList.class ).mapWith( rdfNode -> rdfNode.asLiteral().getString() ).toList() ) )
         .put( SHACL.uniqueLang(), context -> new UniqueLangConstraint() )
         .put( SHACL.equals(), context -> new EqualsConstraint( context.statement().getModel().createProperty( context.statement().getResource().getURI() ) ) )
         .put( SHACL.disjoint(),
               context -> new DisjointConstraint( context.statement().getModel().createProperty( context.statement().getResource().getURI() ) ) )
         .put( SHACL.lessThan(),
               context -> new LessThanConstraint( context.statement().getModel().createProperty( context.statement().getResource().getURI() ) ) )
         .put( SHACL.lessThanOrEquals(),
               context -> new LessThanOrEqualsConstraint( context.statement().getModel().createProperty( context.statement().getResource().getURI() ) ) )
         .put( SHACL.not(), context -> new NotConstraint( constraints( context.statement().getObject().asResource(), context.path() ).get( 0 ) ) )
         .put( SHACL.and(), context -> new AndConstraint( nestedConstraintList( context.statement(), context.path() ) ) )
         .put( SHACL.or(), context -> new OrConstraint( nestedConstraintList( context.statement(), context.path() ) ) )
         .put( SHACL.xone(), context -> new XoneConstraint( nestedConstraintList( context.statement(), context.path() ) ) )
         .put( SHACL.node(), context -> new NodeConstraint( nodeShape( context.statement().getObject().asResource() ), context.path() ) )
         .put( SHACL.in(), context -> new AllowedValuesConstraint( context.statement().getResource().as( RDFList.class ).asJavaList() ) )
         .put( SHACL.closed(), context -> {
            boolean closed = context.statement().getBoolean();
            if ( !closed ) {
               throw new RuntimeException();
            }
            Set<Property> ignoredProperties = context.statement().getSubject().getProperty( SHACL.ignoredProperties() ).getResource()
                  .as( RDFList.class )
                  .asJavaList()
                  .stream()
                  .map( RDFNode::asResource )
                  .map( Resource::getURI )
                  .map( uri -> context.statement().getModel().createProperty( uri ) )
                  .collect( Collectors.toSet() );
            return new ClosedConstraint( ignoredProperties );
         } )
         .put( SHACL.hasValue(), context -> new HasValueConstraint( context.statement().getObject() ) )
         .put( SHACL.sparql(), context -> {
            final Resource constraintNode = context.statement().getResource();
            final String message = Optional.ofNullable( constraintNode.getProperty( SHACL.message() ) ).map( Statement::getString ).orElse( "" );
            return new SparqlConstraint( message, sparqlQuery( constraintNode ) );
         } )
         .put( SHACL.js(), context -> {
            Resource constraintNode = context.statement().getResource();
            JsLibrary library = jsLibrary( constraintNode.getProperty( SHACL.jsLibrary() ).getResource() );
            String functionName = constraintNode.getProperty( SHACL.jsFunctionName() ).getString();
            final String message = Optional.ofNullable( constraintNode.getProperty( SHACL.message() ) ).map( Statement::getString ).orElse( "" );
            return new JsConstraint( message, library, functionName );
         } )
         .build();

   private final Map<Resource, JsLibrary> jsLibraries = new HashMap<>();

   private List<Constraint> nestedConstraintList( final Statement statement, final Optional<Path> path ) {
      return statement.getObject().as( RDFList.class ).asJavaList().stream()
            .filter( RDFNode::isResource )
            .map( RDFNode::asResource )
            .flatMap( resource -> resource.isURIResource()
                  ? nodeShape( resource ).attributes().constraints().stream()
                  : shapeAttributes( resource, path ).constraints().stream() )
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

   private Shape.Attributes shapeAttributes( final Resource shapeNode, final Optional<Path> path ) {
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
      final List<Constraint> constraints = constraints( shapeNode, path );
      return new Shape.Attributes( uri, targetNode, targetClass, targetObjectsOf, targetSubjectsOf, targetSparql, name, description, order, group,
            defaultValue, deactivated, message, severity, constraints );
   }

   private Shape.Node nodeShape( final Resource shapeNode ) {
      final Shape.Attributes attributes = shapeAttributes( shapeNode, Optional.empty() );
      final List<Shape.Property> properties = Streams.stream( shapeNode.listProperties( SHACL.property() ) )
            .map( Statement::getResource )
            .map( this::propertyShape ).toList();
      return new Shape.Node( attributes, properties );
   }

   private Shape.Property propertyShape( final Resource shapeNode ) {
      final Path path = path( shapeNode.getProperty( SHACL.path() ).getResource() );
      final Shape.Attributes attributes = shapeAttributes( shapeNode, Optional.of( path ) );
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

   private List<Constraint> constraints( final Resource valueNode, final Optional<Path> path ) {
      return constraintLoaders.entrySet().stream()
            .flatMap( entry -> {
               try {
                  return Streams.stream( valueNode.listProperties( entry.getKey() ) )
                        .map( statement -> entry.getValue().apply( new ShapeContext( statement, path ) ) );
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

   private JsLibrary jsLibrary( final Resource target ) {
      final JsLibrary jsLibrary = jsLibraries.get( target );
      if ( jsLibrary != null ) {
         return jsLibrary;
      }

      final List<String> jsLibraryUrls = target.listProperties( SHACL.jsLibraryURL() ).mapWith( Statement::getString ).toList();
      final List<JsLibrary> includedLibraries = target.listProperties( SHACL.jsLibrary() ).mapWith( Statement::getResource ).mapWith( this::jsLibrary )
            .toList();
      final JsLibrary result = new JsLibrary( Optional.ofNullable( target.getURI() ), jsLibraryUrls, includedLibraries );
      jsLibraries.put( target, result );
      return result;
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
