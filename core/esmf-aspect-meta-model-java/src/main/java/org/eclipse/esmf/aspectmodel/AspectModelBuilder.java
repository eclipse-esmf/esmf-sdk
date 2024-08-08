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

package org.eclipse.esmf.aspectmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.loader.ModelElementFactory;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.DefaultAspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.impl.DefaultAspectModel;
import org.eclipse.esmf.metamodel.impl.DefaultNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

public class AspectModelBuilder {
   public static AspectModel buildEmptyModel() {
      return new DefaultAspectModel( new ArrayList<>(), ModelFactory.createDefaultModel(), new ArrayList<>() );
   }

   public static AspectModel buildAspectModelFromFiles( final Collection<AspectModelFile> inputFiles ) {
      final Model mergedModel = ModelFactory.createDefaultModel();
      mergedModel.add( MetaModelFile.metaModelDefinitions() );
      for ( final AspectModelFile file : inputFiles ) {
         mergedModel.add( file.sourceModel() );
      }

      final List<ModelElement> elements = new ArrayList<>();
      final List<AspectModelFile> files = new ArrayList<>();
      final Map<AspectModelFile, MetaModelBaseAttributes> namespaceDefinitions = new HashMap<>();
      for ( final AspectModelFile file : inputFiles ) {
         final DefaultAspectModelFile aspectModelFile = new DefaultAspectModelFile( file.sourceModel(), file.headerComment(),
               file.sourceLocation() );
         files.add( aspectModelFile );
         final Model model = file.sourceModel();
         final ModelElementFactory modelElementFactory = new ModelElementFactory( mergedModel, Map.of(), element -> aspectModelFile );
         final List<ModelElement> fileElements = model.listStatements( null, RDF.type, (RDFNode) null ).toList().stream()
               .filter( statement -> !statement.getObject().isURIResource() || !statement.getResource().equals( SammNs.SAMM.Namespace() ) )
               .map( Statement::getSubject )
               .filter( RDFNode::isURIResource )
               .map( resource -> mergedModel.createResource( resource.getURI() ) )
               .map( resource -> modelElementFactory.create( ModelElement.class, resource ) )
               .toList();
         aspectModelFile.setElements( fileElements );
         elements.addAll( fileElements );
      }

      setNamespaces( files, elements );
      return new DefaultAspectModel( files, mergedModel, elements );
   }

   private static void setNamespaces( final Collection<AspectModelFile> files, final Collection<ModelElement> elements ) {
      final Map<String, List<ModelElement>> elementsGroupedByNamespaceUrn = elements.stream()
            .filter( element -> !element.isAnonymous() )
            .collect( Collectors.groupingBy( element -> element.urn().getNamespaceIdentifier() ) );
      for ( final AspectModelFile file : files ) {
         final Optional<String> optionalNamespaceUrn =
               Optional.ofNullable( file.sourceModel().getNsPrefixURI( "" ) )
                     .map( urnPrefix -> urnPrefix.split( "#" )[0] )
                     .or( () -> file.elements().stream()
                           .filter( element -> !element.isAnonymous() )
                           .map( element -> element.urn().getNamespaceIdentifier() )
                           .findAny() );
         if ( optionalNamespaceUrn.isEmpty() ) {
            return;
         }

         final String namespaceUrn = optionalNamespaceUrn.get();
         MetaModelBaseAttributes namespaceDefinition = null;
         AspectModelFile fileContainingNamespaceDefinition = null;
         final List<ModelElement> elementsForUrn = elementsGroupedByNamespaceUrn.get( namespaceUrn );
         if ( elementsForUrn != null ) {
            for ( final ModelElement element : elementsForUrn ) {
               final AspectModelFile elementFile = element.getSourceFile();
               if ( elementFile.sourceModel().contains( null, RDF.type, SammNs.SAMM.Namespace() ) ) {
                  final Model model = elementFile.sourceModel();
                  final ModelElementFactory modelElementFactory = new ModelElementFactory( model, Map.of(), __ -> null );
                  final Resource namespaceResource = model.listStatements( null, RDF.type, SammNs.SAMM.Namespace() )
                        .mapWith( Statement::getSubject )
                        .toList().iterator().next();
                  namespaceDefinition = modelElementFactory.createBaseAttributes( namespaceResource );
                  fileContainingNamespaceDefinition = elementFile;
                  break;
               }
            }
         }

         final Namespace namespace = new DefaultNamespace( namespaceUrn, elementsGroupedByNamespaceUrn.get( namespaceUrn ),
               Optional.ofNullable( fileContainingNamespaceDefinition ), Optional.ofNullable( namespaceDefinition ) );
         ( (DefaultAspectModelFile) file ).setNamespace( namespace );
      }
   }
}
