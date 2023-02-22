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

package org.eclipse.esmf.aspectmodel.resolver.services;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.esmf.aspectmodel.UnsupportedVersionException;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.AspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.urn.ElementType;
import org.eclipse.esmf.aspectmodel.vocabulary.BAMM;
import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

import org.eclipse.esmf.samm.KnownVersion;
import io.vavr.Tuple2;
import io.vavr.control.Try;

/**
 * Provides functionality to resolve Aspect Meta Model resources which reside in the classpath.
 */
public class SdsAspectMetaModelResourceResolver implements AspectMetaModelResourceResolver {

   /**
    * Extends the given {@link Model} with elements contained in the given {@link InputStream}s.
    *
    * @param model the {@link Model} to be extended
    * @param streams the {@link InputStream}s containing the additional model elements
    * @return the extended {@link Model}
    */
   private Try<Model> addToModel( final Model model, final Stream<InputStream> streams ) {
      return streams.map( TurtleLoader::loadTurtle ).map( loadedModel -> loadedModel.flatMap( otherModel -> {
         model.add( otherModel );
         return Try.success( model );
      } ) ).reduce( Try.success( model ), Try::orElse );
   }

   /**
    * Loads an RDF model using a function that knows how to turn a target meta model version into a set of URLs
    *
    * @param version The meta model version
    * @param resolver The resolver function
    * @return The model
    */
   private Try<Model> loadUrlSet( final KnownVersion version, final Function<KnownVersion, Set<URL>> resolver ) {
      final Model model = ModelFactory.createDefaultModel();
      return addToModel( model, resolver.apply( version ).stream().map( TurtleLoader::openUrl ) );
   }

   /**
    * Loads the Meta Model according to a given {@link KnownVersion}
    *
    * @param bammVersion The Meta Model
    * @return The meta model
    */
   public Try<Model> loadMetaModel( final KnownVersion bammVersion ) {
      return loadUrlSet( bammVersion, new ClassPathMetaModelUrnResolver() ).map( model -> {
         model.clearNsPrefixMap();
         model.setNsPrefixes( Namespace.createPrefixMap( bammVersion ) );
         return model;
      } );
   }

   private Model deepCopy( final Model model ) {
      final Model copy = ModelFactory.createDefaultModel();
      Streams.stream( model.listStatements() ).forEach( copy::add );
      copy.setNsPrefixes( ImmutableMap.copyOf( model.getNsPrefixMap() ) );
      return copy;
   }

   /**
    * Returns the {@link VersionedModel} for a loaded raw Aspect model that includes the given <i>rawModel</i>and
    * the <i>model</i> which is the rawModel merged with the corresponding meta model
    *
    * @param rawModel The given raw Aspect model
    * @param bammVersion The meta model version the model corresponds to
    * @return the VersionedModel containing the model, meta model version and raw model
    */
   @Override
   public Try<VersionedModel> mergeMetaModelIntoRawModel( final Model rawModel, final VersionNumber bammVersion ) {
      final Try<KnownVersion> bammKnownVersion = KnownVersion.fromVersionString( bammVersion.toString() )
            .map( Try::success )
            .orElse( Try.failure( new UnsupportedVersionException( bammVersion ) ) );

      return bammKnownVersion.flatMap( this::loadMetaModel ).map( metaModel -> {
         final Model model = deepCopy( rawModel );
         model.add( metaModel );
         return new VersionedModel( model, bammVersion, rawModel );
      } );
   }

   @Override
   public Stream<Statement> listAspectStatements( final Model modelToAdd, final Model target ) {
      return getBammVersion( modelToAdd )
            .map( versionNumber -> KnownVersion.fromVersionString( versionNumber.toString() ).orElseThrow(
                  () -> new UnsupportedVersionException( versionNumber ) ) )
            .toJavaStream().flatMap( metaModelVersion -> {
               final BAMM bamm = new BAMM( metaModelVersion );
               if ( !target.contains( null, RDF.type, bamm.Aspect() ) ) {
                  return Streams.stream( modelToAdd.listStatements() );
               }
               return getModelStatementsWithoutAspectAssertion( modelToAdd, bamm );
            } );
   }

   private Stream<Statement> getModelStatementsWithoutAspectAssertion( final Model model, final BAMM bamm ) {
      return Streams.stream( model.listStatements() ).filter( statement ->
            !(statement.getPredicate().equals( RDF.type )
                  && statement.getObject().isURIResource()
                  && statement.getObject().asResource().equals( bamm.Aspect() )) );
   }

   /**
    * Loads the Meta Model shapes according to a given {@link KnownVersion}
    *
    * @param bammVersion The target Meta Model version
    * @return a {@link Model} containing the Shapes
    */
   public Try<Model> loadShapesModel( final KnownVersion bammVersion ) {
      return loadUrlSet( bammVersion, new ClassPathMetaModelShapesUrnResolver() ).map( model -> {
         final Set<Tuple2<Statement, Statement>> changeSet = determineBammUrlsToReplace( model );
         changeSet.forEach( urlReplacement -> {
            model.remove( urlReplacement._1() );
            model.add( urlReplacement._2() );
         } );
         return model;
      } );
   }

   /**
    * Determines all statements that refer to a bamm:// URL and their replacements where the bamm:// URL has
    * been replaced with a URL that is resolvable in the current context (e.g. to the class path or via HTTP).
    *
    * @param model the input model
    * @return the tuples of the original statement to replace and the replacement statement
    */
   private Set<Tuple2<Statement, Statement>> determineBammUrlsToReplace( final Model model ) {
      final Property shaclJsLibraryUrl = ResourceFactory.createProperty( "http://www.w3.org/ns/shacl#jsLibraryURL" );
      return Streams.stream( model.listStatements( null, shaclJsLibraryUrl, (RDFNode) null ) )
            .filter( statement -> statement.getObject().isLiteral() )
            .filter( statement -> statement.getObject().asLiteral().getString().startsWith( "bamm://" ) )
            .flatMap( statement -> rewriteBammUrl( statement.getObject().asLiteral().getString() )
                  .stream()
                  .map( newUrl ->
                        ResourceFactory.createStatement( statement.getSubject(), statement.getPredicate(),
                              ResourceFactory.createTypedLiteral( newUrl, ExtendedXsdDataType.ANY_URI ) ) )
                  .map( newStatement -> new Tuple2<>( statement, newStatement ) ) )
            .collect( Collectors.toSet() );
   }

   /**
    * URLs inside meta model shapes, in particular those used with sh:jsLibraryURL, are given as bamm:// URLs
    * in order to decouple them from the way they are resolved (i.e. currently to a file in the class path, but
    * in the future this could be resolved using the URL of a suitable service). This method takes a bamm:// URL
    * and rewrites it to the respective URL of the object on the class path.
    *
    * @param bammUrl the bamm URL in the format bamm://PART/VERSION/FILENAME
    * @return The corresponding class path URL to resolve the meta model resource
    */
   private Optional<String> rewriteBammUrl( final String bammUrl ) {
      final Matcher matcher = Pattern.compile( "^bamm://([\\p{Alpha}-]*)/(\\d+\\.\\d+\\.\\d+)/(.*)$" )
            .matcher( bammUrl );
      if ( matcher.find() ) {
         return KnownVersion.fromVersionString( matcher.group( 2 ) ).flatMap( metaModelVersion ->
                     MetaModelUrls.url( matcher.group( 1 ), metaModelVersion, matcher.group( 3 ) ) )
               .map( URL::toString );
      }
      if ( bammUrl.startsWith( "bamm://scripts/" ) ) {
         final String resourcePath = bammUrl.replace( "bamm://", "bamm/" );
         final URL resource = SdsAspectMetaModelResourceResolver.class.getClassLoader().getResource( resourcePath );
         return Optional.ofNullable( resource ).map( URL::toString );
      }
      return Optional.empty();
   }

   /**
    * Returns the set of meta model versions referenced in the model
    *
    * @param model an Aspect model
    * @return the set of meta model versions
    */
   @Override
   public Set<VersionNumber> getUsedMetaModelVersions( final Model model ) {
      final String bammUrnStart = String.format( "%s:%s",
            AspectModelUrn.VALID_PROTOCOL, AspectModelUrn.VALID_NAMESPACE_IDENTIFIER );
      return model.listObjects()
            .toList()
            .stream()
            .filter( RDFNode::isURIResource )
            .map( RDFNode::asResource )
            .map( Resource::getURI )
            .filter( uri -> uri.startsWith( bammUrnStart ) )
            .flatMap( uri -> AspectModelUrn.from( uri ).toJavaStream() )
            .filter( urn -> (urn.getElementType().equals( ElementType.META_MODEL ) || urn.getElementType().equals( ElementType.CHARACTERISTIC )) )
            .map( AspectModelUrn::getVersion )
            .map( VersionNumber::parse )
            .collect( Collectors.toSet() );
   }
}
