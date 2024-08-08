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

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.AspectModelBuilder;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.change.AddAspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.change.AddElementDefinition;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToExistingFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToNewFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToOtherNamespaceExistingFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveElementToOtherNamespaceNewFile;
import org.eclipse.esmf.aspectmodel.edit.change.MoveRenameAspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.change.RemoveAspectModelFile;
import org.eclipse.esmf.aspectmodel.edit.change.RenameElement;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

public class AspectChangeContextTest {
   @Test
   void testRenameElement() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Aspect aspect = aspectModel.aspect();

      final String originalName = aspect.getName();
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( originalName );

      final String newName = "RenamedAspect";
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final Change renameAspect = new RenameElement( aspect, newName );
      ctx.applyChange( renameAspect );
      assertThat( aspectModel.aspect().getName() ).isEqualTo( newName );
      assertThat( aspectModel.files().get( 0 ).sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() )
            .nextStatement().getSubject().getURI() ).endsWith( newName );

      ctx.undoChange();
      assertThat( aspectModel.aspect().getName() ).isEqualTo( originalName );
      ctx.redoChange();
      assertThat( aspectModel.aspect().getName() ).isEqualTo( newName );
   }

   @Test
   void testUndoRedo() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Aspect aspect = aspectModel.aspect();

      final AspectModelUrn aspectUrn = aspect.urn();
      final String originalName = aspect.getName();
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( originalName );

      final String newName = "RenamedAspect";
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final Change renameAspect = new RenameElement( aspectUrn, newName );
      ctx.applyChange( renameAspect );
      assertThat( aspectModel.aspect().getName() ).isEqualTo( newName );
      ctx.undoChange();
      assertThat( aspectModel.aspect().getName() ).isEqualTo( originalName );
      ctx.redoChange();
      assertThat( aspectModel.aspect().getName() ).isEqualTo( newName );
   }

   @Test
   void testChangeGroups() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      final Aspect aspect = aspectModel.aspect();

      final AspectModelUrn aspectUrn = aspect.urn();
      final String oldAspectName = aspect.getName();
      final Property property = aspect.getProperties().get( 0 );
      final String oldPropertyName = property.getName();

      final String newAspectName = "RenamedAspect";
      final String newPropertyName = "renamedProperty";
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final Change renameAspect = new RenameElement( aspectUrn, newAspectName );
      final Change renameProperty = new RenameElement( property.urn(), newPropertyName );

      final Change group = new ChangeGroup( renameAspect, renameProperty );
      ctx.applyChange( group );
      assertThat( aspectModel.aspect().urn().getName() ).isEqualTo( newAspectName );
      assertThat( aspectModel.aspect().getProperties().get( 0 ).getName() ).isEqualTo( newPropertyName );
   }

   @Test
   void testCreateFile() {
      final AspectModel aspectModel = AspectModelBuilder.buildEmptyModel();
      assertThat( aspectModel.files() ).isEmpty();
      assertThat( aspectModel.elements() ).isEmpty();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final AspectModelFile aspectModelFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( "file:///temp/test.ttl" ) ) )
            .build();
      final Change addFile = new AddAspectModelFile( aspectModelFile );
      ctx.applyChange( addFile );
      assertThat( aspectModel.files() ).hasSize( 1 );
      ctx.undoChange();
      assertThat( aspectModel.files() ).isEmpty();
      ctx.redoChange();
      assertThat( aspectModel.files() ).hasSize( 1 );
   }

   @Test
   void testRemoveFile() {
      final AspectModelFile aspectModelFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( "file:///temp/test.ttl" ) ) )
            .build();
      final AspectModel aspectModel = AspectModelBuilder.buildAspectModelFromFiles( List.of( aspectModelFile ) );
      assertThat( aspectModel.files() ).hasSize( 1 );
      assertThat( aspectModel.elements() ).isEmpty();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      ctx.applyChange( new RemoveAspectModelFile( aspectModel.files().get( 0 ) ) );
      assertThat( aspectModel.files() ).isEmpty();

      ctx.undoChange();
      assertThat( aspectModel.files() ).hasSize( 1 );
      ctx.redoChange();
      assertThat( aspectModel.files() ).isEmpty();
   }

   @Test
   void testCreateFileWithElementDefinition() {
      final AspectModel aspectModel = AspectModelBuilder.buildEmptyModel();
      assertThat( aspectModel.files() ).isEmpty();
      assertThat( aspectModel.elements() ).isEmpty();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final AspectModelFile aspectModelFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( "file:///temp/test.ttl" ) ) )
            .sourceModel( createModel( """
                  @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

                  :Aspect a samm:Aspect ;
                     samm:description "This is a test description"@en ;
                     samm:properties ( ) ;
                     samm:operations ( ) .
                  """
            ) )
            .build();
      final Change addFile = new AddAspectModelFile( aspectModelFile );
      ctx.applyChange( addFile );
      assertThat( aspectModel.files() ).hasSize( 1 );
      assertThat( aspectModel.aspects() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getName() ).isEqualTo( "Aspect" );
      ctx.undoChange();
      assertThat( aspectModel.files() ).isEmpty();
      ctx.redoChange();
      assertThat( aspectModel.files() ).hasSize( 1 );
   }

   @Test
   void testCreateFileThenAddElementDefinition() {
      final AspectModel aspectModel = AspectModelBuilder.buildEmptyModel();
      assertThat( aspectModel.files() ).isEmpty();
      assertThat( aspectModel.elements() ).isEmpty();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final AspectModelFile aspectModelFile = RawAspectModelFileBuilder.builder()
            .sourceLocation( Optional.of( URI.create( "file:///temp/test.ttl" ) ) )
            .build();

      final Change changes = new ChangeGroup(
            new AddAspectModelFile( aspectModelFile ),
            new AddElementDefinition( AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#Aspect" ),
                  createModel( """
                        @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
                        @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                        @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

                        :Aspect a samm:Aspect ;
                           samm:description "This is a test description"@en ;
                           samm:properties ( ) ;
                           samm:operations ( ) .
                        """ ), aspectModelFile )
      );

      ctx.applyChange( changes );
      assertThat( aspectModel.files() ).hasSize( 1 );
      assertThat( aspectModel.aspects() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getName() ).isEqualTo( "Aspect" );
      ctx.undoChange();
      assertThat( aspectModel.files() ).isEmpty();
      ctx.redoChange();
      assertThat( aspectModel.files() ).hasSize( 1 );
   }

   @Test
   void testMoveElementToNewFile() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      assertThat( aspectModel.files() ).hasSize( 1 );
      final URI originalSourceLocation = aspectModel.aspect().getSourceFile().sourceLocation().get();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final URI sourceLocation = URI.create( "file:///temp/test.ttl" );
      final Change move = new MoveElementToNewFile( aspectModel.aspect(), Optional.of( sourceLocation ) );

      final ChangeReport changeReport = ctx.applyChange( move );
      System.out.println( ChangeReportFormatter.INSTANCE.apply( changeReport, AspectChangeContextConfig.DEFAULT ) );
      assertThat( aspectModel.files() ).hasSize( 2 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( sourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );

      ctx.undoChange();
      assertThat( aspectModel.files() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( originalSourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );

      ctx.redoChange();
      assertThat( aspectModel.files() ).hasSize( 2 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( sourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
   }

   @Test
   void testMoveElementToExistingFile() {
      final Optional<URI> file1Location = Optional.of( URI.create( "file:///file1.ttl" ) );
      final Optional<URI> file2Location = Optional.of( URI.create( "file:///file2.ttl" ) );
      final AspectModel aspectModel = AspectModelBuilder.buildEmptyModel();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final AspectModelFile file1 = RawAspectModelFileBuilder.builder()
            .sourceLocation( file1Location )
            .sourceModel( createModel( """
                  @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

                  :Aspect a samm:Aspect ;
                     samm:description "This is a test description"@en ;
                     samm:properties ( ) ;
                     samm:operations ( ) .
                  """
            ) )
            .build();
      final AspectModelFile file2 = RawAspectModelFileBuilder.builder().sourceLocation( file2Location ).build();

      ctx.applyChange( new ChangeGroup(
            new AddAspectModelFile( file1 ),
            new AddAspectModelFile( file2 )
      ) );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file1Location );

      final Change move = new MoveElementToExistingFile( aspectModel.aspect(), file2 );

      final ChangeReport changeReport = ctx.applyChange( move );
      System.out.println( ChangeReportFormatter.INSTANCE.apply( changeReport, AspectChangeContextConfig.DEFAULT ) );

      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file2Location );
      ctx.undoChange();
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file1Location );
   }

   @Test
   void testMoveElementToOtherNamespaceNewFile() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      assertThat( aspectModel.files() ).hasSize( 1 );
      final URI originalSourceLocation = aspectModel.aspect().getSourceFile().sourceLocation().get();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final URI sourceLocation = URI.create( "file:///temp/test.ttl" );

      final AspectModelUrn targetUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.example.new:1.0.0#Aspect" );
      final Namespace targetNamespace = new DefaultNamespace( targetUrn, List.of(), Optional.empty() );
      final Change move = new MoveElementToOtherNamespaceNewFile( aspectModel.aspect(), targetNamespace, Optional.of( sourceLocation ) );

      final ChangeReport changeReport = ctx.applyChange( move );
      System.out.println( ChangeReportFormatter.INSTANCE.apply( changeReport, AspectChangeContextConfig.DEFAULT ) );

      assertThat( aspectModel.files() ).hasSize( 2 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( sourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
      assertThat( aspectModel.aspect().urn() ).isEqualTo( targetUrn );

      ctx.undoChange();
      assertThat( aspectModel.files() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( originalSourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );

      ctx.redoChange();
      assertThat( aspectModel.files() ).hasSize( 2 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( sourceLocation );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
   }

   @Test
   void testMoveElementToOtherNamespaceExistingFile() {
      final Optional<URI> file1Location = Optional.of( URI.create( "file:///file1.ttl" ) );
      final Optional<URI> file2Location = Optional.of( URI.create( "file:///file2.ttl" ) );
      final AspectModel aspectModel = AspectModelBuilder.buildEmptyModel();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final AspectModelFile file1 = RawAspectModelFileBuilder.builder()
            .sourceLocation( file1Location )
            .sourceModel( createModel( """
                  @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.1.0#> .
                  @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                  @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .

                  :Aspect a samm:Aspect ;
                     samm:description "This is a test description"@en ;
                     samm:properties ( ) ;
                     samm:operations ( ) .
                  """
            ) )
            .build();
      final AspectModelFile file2 = RawAspectModelFileBuilder.builder().sourceLocation( file2Location ).build();

      ctx.applyChange( new ChangeGroup(
            new AddAspectModelFile( file1 ),
            new AddAspectModelFile( file2 )
      ) );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file1Location );

      final AspectModelUrn targetUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.example.new:1.0.0#Aspect" );
      final Namespace targetNamespace = new DefaultNamespace( targetUrn, List.of(), Optional.empty() );
      final Change move = new MoveElementToOtherNamespaceExistingFile( aspectModel.aspect(), file2, targetNamespace );

      final ChangeReport changeReport = ctx.applyChange( move );
      System.out.println( ChangeReportFormatter.INSTANCE.apply( changeReport, AspectChangeContextConfig.DEFAULT ) );

      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file2Location );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
      assertThat( aspectModel.aspect().urn() ).isEqualTo( targetUrn );

      ctx.undoChange();
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).isEqualTo( file1Location );
      assertThat( aspectModel.aspect().getSourceFile().sourceModel().listStatements( null, RDF.type, SammNs.SAMM.Aspect() ).nextStatement()
            .getSubject().getURI() ).isEqualTo( aspectModel.aspect().urn().toString() );
   }

   @Test
   void testMoveRenameFile() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT );
      final Aspect aspect = aspectModel.aspect();

      assertThat( aspectModel.files() ).hasSize( 1 );

      final URI originalLocation = aspect.getSourceFile().sourceLocation().get();
      final AspectChangeContext ctx = new AspectChangeContext( aspectModel );
      final URI newLocation = URI.create( "file:///temp/test.ttl" );
      final Change renameFile = new MoveRenameAspectModelFile( aspect.getSourceFile(), newLocation );

      ctx.applyChange( renameFile );
      assertThat( aspectModel.files() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( newLocation );

      ctx.undoChange();
      assertThat( aspectModel.files() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( originalLocation );

      ctx.redoChange();
      assertThat( aspectModel.files() ).hasSize( 1 );
      assertThat( aspectModel.aspect().getSourceFile().sourceLocation() ).contains( newLocation );
   }
}
