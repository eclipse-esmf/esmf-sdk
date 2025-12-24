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

package org.eclipse.esmf.aspectmodel.edit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.aspectmodel.RdfUtil.createModel;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.edit.change.AddAspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.change.AddElementDefinition;
import org.eclipse.esmf.aspectmodel.edit.change.CopyFileWithIncreasedNamespaceVersion;
import org.eclipse.esmf.aspectmodel.edit.change.CopyNamespaceWithIncreasedVersion;
import org.eclipse.esmf.aspectmodel.edit.change.IncreaseVersion;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToExistingFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToNewFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToOtherNamespaceExistingFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToOtherNamespaceNewFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveRenameAspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.change.RemoveAspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.change.RenameElement;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

class AspectChangeManagerTest {
   @Test
   void testRenameElement() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Aspect aspect = aspectModel.aspect();

      final String originalName = aspect.getName();
      assertThat( aspectModel ).hasSingleAspectThat().urn().hasName( originalName );

      final String newName = "RenamedAspect";
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final Change renameAspect = new RenameElement( aspect, newName );
      changeManager.applyChange( renameAspect );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( newName );
      assertThat( aspectModel.files().get( 0 ).sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() )
            .nextStatement().getSubject().getURI() ).endsWith( newName );
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );

      changeManager.undoChange();
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( originalName );
      changeManager.redoChange();
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( newName );
   }

   @Test
   void testUndoRedo() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Aspect aspect = aspectModel.aspect();

      final AspectModelUrn aspectUrn = aspect.urn();
      final String originalName = aspect.getName();
      assertThat( aspectModel ).hasSingleAspectThat().hasName( originalName );

      final String newName = "RenamedAspect";
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final Change renameAspect = new RenameElement( aspectUrn, newName );
      changeManager.applyChange( renameAspect );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( newName );
      changeManager.undoChange();
      assertThat( aspectModel ).hasSingleAspectThat().hasName( originalName );
      changeManager.redoChange();
      assertThat( aspectModel ).hasSingleAspectThat().hasName( newName );
   }

   @Test
   void testChangeGroups() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      final Aspect aspect = aspectModel.aspect();

      final AspectModelUrn aspectUrn = aspect.urn();
      final Property property = aspect.getProperties().get( 0 );

      final String newAspectName = "RenamedAspect";
      final String newPropertyName = "renamedProperty";
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final Change renameAspect = new RenameElement( aspectUrn, newAspectName );
      final Change renameProperty = new RenameElement( property.urn(), newPropertyName );

      final Change group = new ChangeGroup( renameAspect, renameProperty );
      changeManager.applyChange( group );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( newAspectName );
      assertThat( aspectModel ).hasSingleAspectThat().hasSinglePropertyThat().hasName( newPropertyName );
   }

   @Test
   void testCreateFile() {
      final AspectModel aspectModel = new AspectModelLoader().emptyModel();
      assertThat( aspectModel ).files().isEmpty();
      assertThat( aspectModel ).elements().isEmpty();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final AspectModelFile aspectModelFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( "file:///temp/test.ttl" ) ) )
            .build();
      final Change addFile = new AddAspectModelFile( aspectModelFile );
      changeManager.applyChange( addFile );
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
      changeManager.undoChange();
      assertThat( changeManager.createdFiles() ).isEmpty();
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().isEmpty();
      changeManager.redoChange();
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
   }

   @Test
   void testRemoveFile() {
      final AspectModelFile aspectModelFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( "file:///temp/test.ttl" ) ) )
            .build();
      final AspectModel aspectModel = new AspectModelLoader().loadAspectModelFiles( List.of( aspectModelFile ) );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel ).elements().isEmpty();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      changeManager.applyChange( new RemoveAspectModelFile( aspectModel.files().get( 0 ) ) );
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().isEmpty();
      changeManager.undoChange();
      assertThat( changeManager.removedFiles() ).isEmpty();
      assertThat( aspectModel ).files().hasSize( 1 );
      changeManager.redoChange();
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().isEmpty();
   }

   @Test
   void testCreateFileWithElementDefinition() {
      final AspectModel aspectModel = new AspectModelLoader().emptyModel();
      assertThat( aspectModel ).files().isEmpty();
      assertThat( aspectModel ).elements().isEmpty();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final AspectModelFile aspectModelFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( "file:///temp/test.ttl" ) ) )
            .sourceModel( createModel( """
                  @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:%s#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
                  
                  :Aspect a samm:Aspect ;
                     samm:description "This is a test description"@en ;
                     samm:properties ( ) ;
                     samm:operations ( ) .
                  """.formatted( KnownVersion.getLatest().toVersionString() )
            ) )
            .build();
      final Change addFile = new AddAspectModelFile( aspectModelFile );
      changeManager.applyChange( addFile );
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.aspects() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getName() ).isEqualTo( "Aspect" );
      changeManager.undoChange();
      assertThat( changeManager.createdFiles() ).isEmpty();
      assertThat( aspectModel ).files().isEmpty();
      changeManager.redoChange();
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
   }

   @Test
   void testCreateFileThenAddElementDefinition() {
      final AspectModel aspectModel = new AspectModelLoader().emptyModel();
      assertThat( aspectModel ).files().isEmpty();
      assertThat( aspectModel ).elements().isEmpty();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final AspectModelFile aspectModelFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( "file:///temp/test.ttl" ) ) )
            .build();

      final Change changes = new ChangeGroup(
            new AddAspectModelFile( aspectModelFile ),
            new AddElementDefinition( AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#Aspect" ),
                  createModel( """
                        @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:%s#> .
                        @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                        @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
                        
                        :Aspect a samm:Aspect ;
                           samm:description "This is a test description"@en ;
                           samm:properties ( ) ;
                           samm:operations ( ) .
                        """.formatted( KnownVersion.getLatest().toVersionString() ) ), aspectModelFile )
      );

      changeManager.applyChange( changes );
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.aspects() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getName() ).isEqualTo( "Aspect" );
      changeManager.undoChange();
      assertThat( changeManager.createdFiles() ).isEmpty();
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().isEmpty();
      changeManager.redoChange();
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
   }

   @Test
   void testMoveElementToNewFile() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      assertThat( aspectModel ).files().hasSize( 1 );
      final URI originalSourceLocation = aspectModel.aspect().getSourceFile().sourceLocation().get();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final URI sourceLocation = URI.create( "file:///temp/test.ttl" );
      final Change move = new MoveElementToNewFile( aspectModel.aspect(), sourceLocation );

      changeManager.applyChange( move );
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 2 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( sourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );

      changeManager.undoChange();
      assertThat( changeManager.createdFiles() ).isEmpty();
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( originalSourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );

      changeManager.redoChange();
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 2 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( sourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
   }

   @Test
   void testMoveElementToExistingFile() {
      final Optional<URI> file1Location = Optional.of( URI.create( "file:///file1.ttl" ) );
      final Optional<URI> file2Location = Optional.of( URI.create( "file:///file2.ttl" ) );
      final AspectModel aspectModel = new AspectModelLoader().emptyModel();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final AspectModelFile file1 = RawAspectModelFileBuilder.builder()
            .sourceLocation( file1Location )
            .sourceModel( createModel( """
                  @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:%s#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
                  
                  :Aspect a samm:Aspect ;
                     samm:description "This is a test description"@en ;
                     samm:properties ( ) ;
                     samm:operations ( ) .
                  """.formatted( KnownVersion.getLatest().toVersionString() )
            ) )
            .build();
      final AspectModelFile file2 = RawAspectModelFileBuilder.builder().sourceLocation( file2Location ).build();

      changeManager.applyChange( new ChangeGroup(
            new AddAspectModelFile( file1 ),
            new AddAspectModelFile( file2 )
      ) );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file1Location );

      final Change move = new MoveElementToExistingFile( aspectModel.aspect(), file2 );
      changeManager.applyChange( move );
      assertThat( changeManager.modifiedFiles() ).hasSize( 2 );
      assertThat( changeManager.createdFiles() ).isEmpty();
      assertThat( changeManager.removedFiles() ).isEmpty();

      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file2Location );
      changeManager.undoChange();
      assertThat( changeManager.modifiedFiles() ).hasSize( 2 );
      assertThat( changeManager.createdFiles() ).isEmpty();
      assertThat( changeManager.removedFiles() ).isEmpty();
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file1Location );
   }

   @Test
   void testMoveElementToOtherNamespaceNewFile() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      assertThat( aspectModel ).files().hasSize( 1 );
      final URI originalSourceLocation = aspectModel.aspect().getSourceFile().sourceLocation().get();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final URI sourceLocation = URI.create( "file:///temp/test.ttl" );

      final AspectModelUrn targetUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.example.new:1.0.0#Aspect" );
      final Namespace targetNamespace = new DefaultNamespace( targetUrn, List.of(), Optional.empty() );
      final Change move = new MoveElementToOtherNamespaceNewFile( aspectModel.aspect(), targetNamespace, sourceLocation );

      changeManager.applyChange( move );
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 2 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( sourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
      assertThat( aspectModel.aspect().urn() ).isEqualTo( targetUrn );

      changeManager.undoChange();
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( changeManager.createdFiles() ).isEmpty();
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( originalSourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );

      changeManager.redoChange();
      assertThat( changeManager.modifiedFiles() ).hasSize( 1 );
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 2 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( sourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
   }

   @Test
   void testMoveElementToOtherNamespaceExistingFile() {
      final Optional<URI> file1Location = Optional.of( URI.create( "file:///file1.ttl" ) );
      final Optional<URI> file2Location = Optional.of( URI.create( "file:///file2.ttl" ) );
      final AspectModel aspectModel = new AspectModelLoader().emptyModel();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final AspectModelFile file1 = RawAspectModelFileBuilder.builder()
            .sourceLocation( file1Location )
            .sourceModel( createModel( """
                  @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:%s#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
                  
                  :Aspect a samm:Aspect ;
                     samm:description "This is a test description"@en ;
                     samm:properties ( ) ;
                     samm:operations ( ) .
                  """.formatted( KnownVersion.getLatest().toVersionString() )
            ) )
            .build();
      final AspectModelFile file2 = RawAspectModelFileBuilder.builder().sourceLocation( file2Location ).build();

      changeManager.applyChange( new ChangeGroup(
            new AddAspectModelFile( file1 ),
            new AddAspectModelFile( file2 )
      ) );
      changeManager.resetFileStates();
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file1Location );

      final AspectModelUrn targetUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.example.new:1.0.0#Aspect" );
      final Namespace targetNamespace = new DefaultNamespace( targetUrn, List.of(), Optional.empty() );
      final Change move = new MoveElementToOtherNamespaceExistingFile( aspectModel.aspect(), file2, targetNamespace );

      final ChangeReport changeReport = changeManager.applyChange( move );
      assertThat( changeManager.modifiedFiles() ).hasSize( 2 );
      assertThat( changeManager.createdFiles() ).isEmpty();

      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file2Location );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
      assertThat( aspectModel.aspect().urn() ).isEqualTo( targetUrn );

      changeManager.undoChange();
      assertThat( changeManager.modifiedFiles() ).hasSize( 2 );
      assertThat( changeManager.createdFiles() ).isEmpty();
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file1Location );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
   }

   @Test
   void testMoveRenameFile() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Aspect aspect = aspectModel.aspect();

      assertThat( aspectModel ).files().hasSize( 1 );

      final URI originalLocation = aspect.getSourceFile().sourceLocation().get();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );
      final URI newLocation = URI.create( "file:///temp/test.ttl" );
      final Change renameFile = new MoveRenameAspectModelFile( aspect.getSourceFile(), newLocation );

      changeManager.applyChange( renameFile );
      assertThat( changeManager.modifiedFiles() ).isEmpty();
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( newLocation );

      changeManager.undoChange();
      assertThat( changeManager.modifiedFiles() ).isEmpty();
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( originalLocation );

      changeManager.redoChange();
      assertThat( changeManager.modifiedFiles() ).isEmpty();
      assertThat( changeManager.createdFiles() ).hasSize( 1 );
      assertThat( changeManager.removedFiles() ).hasSize( 1 );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( newLocation );
   }

   @Test
   void testCopyNamespaceWithIncreasedVersion() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Namespace namespace = aspectModel.files().get( 0 ).namespace();
      final Change copyNamespaceWithIncreasedVersion = new CopyNamespaceWithIncreasedVersion( namespace,
            IncreaseVersion.MAJOR );

      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( true )
            .build();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );

      assertThat( aspectModel.namespaces() ).hasSize( 1 );
      assertThat( aspectModel.namespaces().get( 0 ).version().getMajor() ).isEqualTo( 1 );
      assertThat( aspectModel.aspects() ).hasSize( 1 );
      assertThat( aspectModel.aspect().urn().getVersion() ).isEqualTo( "1.0.0" );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.files().get( 0 ).sourceLocation().get().toString() ).contains( "1.0.0" );

      final ChangeReport changeReport = changeManager.applyChange( copyNamespaceWithIncreasedVersion );
      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces() ).map( ns -> ns.version().getMajor() ).containsExactlyInAnyOrder( 1, 2 );
      assertThat( aspectModel.aspects() ).hasSize( 2 );
      assertThat( aspectModel.aspects() ).map( aspect -> aspect.urn().getVersion() ).containsExactlyInAnyOrder( "1.0.0", "2.0.0" );
      assertThat( aspectModel ).files().hasSize( 2 );
      assertThat( aspectModel ).files().map( file -> file.sourceLocation().get().toString() )
            .anyMatch( location -> location.contains( "1.0.0" ) )
            .anyMatch( location -> location.contains( "2.0.0" ) );
   }

   @Test
   void testCopyFileWithIncreasedVersion() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Change copyFileWithIncreasedNamespaceVersion = new CopyFileWithIncreasedNamespaceVersion( aspectModel.files().get( 0 ),
            IncreaseVersion.MAJOR );

      final AspectChangeManagerConfig config = AspectChangeManagerConfigBuilder.builder()
            .detailedChangeReport( true )
            .build();
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );

      assertThat( aspectModel.namespaces() ).hasSize( 1 );
      assertThat( aspectModel.namespaces().get( 0 ).version().getMajor() ).isEqualTo( 1 );
      assertThat( aspectModel.aspects() ).hasSize( 1 );
      assertThat( aspectModel.aspect().urn().getVersion() ).isEqualTo( "1.0.0" );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel.files().get( 0 ).sourceLocation().get().toString() ).contains( "1.0.0" );

      final ChangeReport changeReport = changeManager.applyChange( copyFileWithIncreasedNamespaceVersion );
      assertThat( aspectModel.namespaces() ).hasSize( 2 );
      assertThat( aspectModel.namespaces() ).map( ns -> ns.version().getMajor() ).containsExactlyInAnyOrder( 1, 2 );
      assertThat( aspectModel.aspects() ).hasSize( 2 );
      assertThat( aspectModel.aspects() ).map( aspect -> aspect.urn().getVersion() ).containsExactlyInAnyOrder( "1.0.0", "2.0.0" );
      assertThat( aspectModel ).files().hasSize( 2 );
      assertThat( aspectModel ).files().map( file -> file.sourceLocation().get().toString() )
            .anyMatch( location -> location.contains( "1.0.0" ) )
            .anyMatch( location -> location.contains( "2.0.0" ) );
   }

   @Test
   void testCopyFileWithIncreasedVersion_doesNotKeepOldNamespaceIrisInNewFile() {
      // Arrange
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );

      final AspectModelFile originalFile = aspectModel.files().get( 0 );
      final String oldNsPrefix = originalFile.namespace().urn().toString() + "#";
      final String propertyLocalName = "testProperty";

      // sanity: ensure the fixture really contains a reference to the property in old namespace (e.g., via rdf:first)
      assertThat( originalFile.sourceModel()
            .listStatements( null, null, originalFile.sourceModel().createResource( oldNsPrefix + propertyLocalName ) )
            .hasNext()
      ).as( "Test fixture must contain old namespace property IRI" ).isTrue();

      final VersionNumber currentVersion = originalFile.namespace().version();
      final VersionNumber nextVersion = IncreaseVersion.MAJOR.apply( currentVersion );
      final String nextVersionStr = nextVersion.toString();

      final Change change = new CopyFileWithIncreasedNamespaceVersion( originalFile, IncreaseVersion.MAJOR );
      final AspectChangeManager changeManager = new AspectChangeManager( aspectModel );

      // Act
      changeManager.applyChange( change );

      // Assert: find the newly created file by computed next version
      final AspectModelFile v2File = aspectModel.files().stream()
            .filter( f -> f.sourceLocation().map( URI::toString ).orElse( "" ).contains( nextVersionStr ) )
            .findFirst()
            .orElseThrow( () -> new AssertionError( "Expected a copied file with version " + nextVersionStr + " in sourceLocation" ) );

      final Model m = v2File.sourceModel();

      // 1) No subject/predicate/object IRI in the NEW file must still point to the OLD namespace prefix
      final StmtIterator it = m.listStatements();
      while ( it.hasNext() ) {
         final Statement st = it.nextStatement();

         if ( st.getSubject().isURIResource() ) {
            assertThat( st.getSubject().getURI() )
                  .as( "Old namespace leaked in SUBJECT: %s", st )
                  .doesNotStartWith( oldNsPrefix );
         }

         assertThat( st.getPredicate().getURI() )
               .as( "Old namespace leaked in PREDICATE: %s", st )
               .doesNotStartWith( oldNsPrefix );

         if ( st.getObject().isURIResource() ) {
            assertThat( st.getObject().asResource().getURI() )
                  .as( "Old namespace leaked in OBJECT (catches rdf:first in lists): %s", st )
                  .doesNotStartWith( oldNsPrefix );
         }
      }

      // 2) Targeted check: old property IRI must not appear anywhere as object
      assertThat( m.listStatements( null, null, m.createResource( oldNsPrefix + propertyLocalName ) ).hasNext() )
            .as( "New file must not reference old property IRI (catches samm:properties list/rdf:first issue)" )
            .isFalse();
   }
}
