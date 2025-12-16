/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver.modelfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectLoadingException;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import io.vavr.Tuple2;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

/**
 * Enumeration of the {@link AspectModelFile}s that contain the SAMM meta model definition.
 */
public enum MetaModelFile implements AspectModelFile {
   UNITS( "unit", "units.ttl", SammNs.UNIT, MetaModelFileType.ELEMENT_DEFINITION ),
   FILE_RESOURCE( "entity", "FileResource.ttl", SammNs.SAMME, MetaModelFileType.ELEMENT_DEFINITION ),
   POINT_3D( "entity", "Point3d.ttl", SammNs.SAMME, MetaModelFileType.ELEMENT_DEFINITION ),
   TIME_SERIES_ENTITY( "entity", "TimeSeriesEntity.ttl", SammNs.SAMME, MetaModelFileType.ELEMENT_DEFINITION ),
   QUANTITY( "entity", "Quantity.ttl", SammNs.SAMME, MetaModelFileType.ELEMENT_DEFINITION ),
   CHARACTERISTIC_INSTANCES( "characteristic", "characteristic-instances.ttl", SammNs.SAMMC,
         MetaModelFileType.ELEMENT_DEFINITION ),

   TYPE_CONVERSIONS( "meta-model", "type-conversions.ttl", SammNs.SAMM, MetaModelFileType.META_MODEL_DEFINITION ),
   ASPECT_META_MODEL_DEFINITIONS( "meta-model", "aspect-meta-model-definitions.ttl", SammNs.SAMM,
         MetaModelFileType.META_MODEL_DEFINITION ),
   CHARACTERISTIC_DEFINITIONS( "characteristic", "characteristic-definitions.ttl", SammNs.SAMMC,
         MetaModelFileType.META_MODEL_DEFINITION ),

   ASPECT_META_MODEL_SHAPES( "meta-model", "aspect-meta-model-shapes.ttl", SammNs.SAMM, MetaModelFileType.SHAPE_DEFINITION ),
   PREFIX_DECLARATIONS( "meta-model", "prefix-declarations.ttl", SammNs.SAMM, MetaModelFileType.SHAPE_DEFINITION ),
   CHARACTERISTIC_SHAPES( "characteristic", "characteristic-shapes.ttl", SammNs.SAMMC, MetaModelFileType.SHAPE_DEFINITION );

   public enum MetaModelFileType {
      ELEMENT_DEFINITION,
      META_MODEL_DEFINITION,
      SHAPE_DEFINITION
   }

   private final RdfNamespace rdfNamespace;
   private final MetaModelFileType metaModelFileType;
   private final Model sourceModel;
   private final String filename;
   private final URL sourceUrl;

   MetaModelFile( final String section, final String filename, final RdfNamespace rdfNamespace,
         final MetaModelFileType metaModelFileType ) {
      this.filename = filename;
      this.rdfNamespace = rdfNamespace;
      this.metaModelFileType = metaModelFileType;
      sourceUrl = url( section, filename );
      sourceModel = TurtleLoader.loadTurtle( sourceUrl )
            .map( model -> {
               final Set<Tuple2<Statement, Statement>> changeSet = determineSammUrlsToReplace( model );
               changeSet.forEach( urlReplacement -> {
                  model.remove( urlReplacement._1() );
                  model.add( urlReplacement._2() );
               } );
               return model;
            } ).getOrElseThrow( () -> new AspectLoadingException( "Could not load meta model file: " + filename ) );
   }

   /**
    * Determines all statements that refer to a samm:// URL and their replacements where the samm:// URL has
    * been replaced with a URL that is resolvable in the current context (e.g. to the class path or via HTTP).
    *
    * @param model the input model
    * @return the tuples of the original statement to replace and the replacement statement
    */
   private Set<Tuple2<Statement, Statement>> determineSammUrlsToReplace( final Model model ) {
      final Property shaclJsLibraryUrl = ResourceFactory.createProperty( "http://www.w3.org/ns/shacl#jsLibraryURL" );
      return Streams.stream( model.listStatements( null, shaclJsLibraryUrl, (RDFNode) null ) )
            .filter( statement -> statement.getObject().isLiteral() )
            .filter( statement -> statement.getObject().asLiteral().getString().startsWith( "samm://" ) )
            .flatMap( statement -> rewriteSammUrl( statement.getObject().asLiteral().getString() )
                  .stream()
                  .map( newUrl ->
                        ResourceFactory.createStatement( statement.getSubject(), statement.getPredicate(),
                              ResourceFactory.createTypedLiteral( newUrl, SammType.ANY_URI ) ) )
                  .map( newStatement -> new Tuple2<>( statement, newStatement ) ) )
            .collect( Collectors.toSet() );
   }

   /**
    * URLs inside meta model shapes, in particular those used with sh:jsLibraryURL, are given as samm:// URLs
    * in order to decouple them from the way they are resolved (i.e. currently to a file in the class path, but
    * in the future this could be resolved using the URL of a suitable service). This method takes a samm:// URL
    * and rewrites it to the respective URL of the object on the class path.
    *
    * @param sammUrl the samm URL in the format samm://PART/VERSION/FILENAME
    * @return The corresponding class path URL to resolve the meta model resource
    */
   private Optional<String> rewriteSammUrl( final String sammUrl ) {
      final Matcher matcher = Pattern.compile( "^samm://([\\p{Alpha}-]*)/(\\d+\\.\\d+\\.\\d+)/(.*)$" )
            .matcher( sammUrl );
      if ( matcher.find() ) {
         return KnownVersion.fromVersionString( matcher.group( 2 ) )
               .map( metaModelVersion -> url( matcher.group( 1 ), matcher.group( 3 ) ) )
               .map( URL::toString );
      }
      if ( sammUrl.startsWith( "samm://scripts/" ) ) {
         final String resourcePath = sammUrl.replace( "samm://", "samm/" );
         final URL resource = MetaModelFile.class.getClassLoader().getResource( resourcePath );
         return Optional.ofNullable( resource ).map( URL::toString );
      }
      return Optional.empty();
   }

   @Override
   public Model sourceModel() {
      return sourceModel;
   }

   @Override
   public String sourceRepresentation() {
      try {
         final ByteArrayOutputStream out = new ByteArrayOutputStream();
         IOUtils.copy( sourceUrl.openStream(), out );
         return out.toString();
      } catch ( final IOException exception ) {
         throw new AspectLoadingException( "Unable to load meta model file " + sourceUrl );
      }
   }

   @Override
   public Optional<URI> sourceLocation() {
      return Optional.of( URI.create( rdfNamespace.getUri() ) );
   }

   @Override
   public Optional<String> filename() {
      return Optional.of( filename );
   }

   public static List<MetaModelFile> getElementDefinitionsFiles() {
      return Arrays.stream( values() ).filter( file -> file.metaModelFileType == MetaModelFileType.ELEMENT_DEFINITION ).toList();
   }

   public static List<MetaModelFile> getMetaModelDefinitionsFiles() {
      return Arrays.stream( values() ).filter( file -> file.metaModelFileType == MetaModelFileType.META_MODEL_DEFINITION ).toList();
   }

   public static List<MetaModelFile> getShapeDefinitionsFiles() {
      return Arrays.stream( values() ).filter( file -> file.metaModelFileType == MetaModelFileType.SHAPE_DEFINITION ).toList();
   }

   /**
    * The SAMM meta model definitions as a single RDF model.
    *
    * @return the meta model definitions
    */
   public static Model metaModelDefinitions() {
      final Model model = ModelFactory.createDefaultModel();
      getMetaModelDefinitionsFiles().stream().map( MetaModelFile::sourceModel ).forEach( model::add );
      getElementDefinitionsFiles().stream().map( MetaModelFile::sourceModel ).forEach( model::add );
      return model;
   }

   /**
    * The SAMM meta model shapes as a single RDF model.
    *
    * @return the meta model shapes
    */
   public static Model metaModelShapes() {
      final Model model = ModelFactory.createDefaultModel();
      getShapeDefinitionsFiles().stream().map( MetaModelFile::sourceModel ).forEach( model::add );
      return model;
   }

   /**
    * Create a URL referring to a meta model resource
    *
    * @param section The meta model section
    * @param filename the file name
    * @return The resource URL
    */
   private URL url( final String section, final String filename ) {
      final String spec = String.format( "samm/%s/%s/%s", section, KnownVersion.getLatest().toVersionString(), filename );
      try {
         final List<URL> urls = ImmutableList.copyOf( MetaModelFile.class.getClassLoader().getResources( spec ).asIterator() );
         if ( urls.size() == 1 ) {
            return urls.get( 0 );
         }
         if ( urls.isEmpty() ) {
            throw new AspectLoadingException( "Could not resolve meta model file: " + filename );
         }

         // If multiple resources with the given spec are found:
         // - return the one from the file system, if it exists
         // - otherwise, the one from jar
         // - otherwise, any of the found resources
         URL jarUrl = null;
         for ( final URL url : urls ) {
            if ( url.getProtocol().equals( "file" ) ) {
               return url;
            }
            if ( url.getProtocol().equals( "jar" ) ) {
               jarUrl = url;
            }
         }
         return jarUrl == null ? urls.get( 0 ) : jarUrl;
      } catch ( final IOException exception ) {
         throw new AspectLoadingException( "Could not resolve meta model file: " + filename );
      }
   }

   public RdfNamespace getRdfNamespace() {
      return rdfNamespace;
   }

   public MetaModelFileType getMetaModelFileType() {
      return metaModelFileType;
   }
}
