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

package org.eclipse.esmf.test;

import static org.eclipse.esmf.metamodel.DataTypes.*;
import static org.eclipse.esmf.metamodel.Elements.*;
import static org.eclipse.esmf.metamodel.builder.SammBuilder.*;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Test;

public class AssertTest {
   @Test
   void testPropertyAssert() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( "AspectWithProperty" ).hasVersion( "1.0.0" );
      assertThat( aspectModel ).hasSingleAspectThat().hasUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithProperty" );
      assertThat( aspectModel ).hasSingleAspectThat().hasUrn(
            AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithProperty" ) );

      final Aspect aspect = aspectModel.aspect();
      assertThat( aspect ).isNamedElement()
            .hasName( "AspectWithProperty" )
            .hasVersion( "1.0.0" )
            .hasVersion( new VersionNumber( 1, 0, 0 ) )
            .isNoCollectionAspect();
      assertThat( aspect ).urn()
            .hasName( "AspectWithProperty" )
            .hasNamespaceMainPart( "org.eclipse.esmf.test" )
            .hasVersion( "1.0.0" )
            .hasVersion( new VersionNumber( 1, 0, 0 ) );
      assertThat( aspect ).properties().hasSize( 1 );
      assertThat( aspect ).properties().anyMatch( property -> property.getName().equals( "testProperty" ) );
      assertThat( aspect ).propertyByName( "testProperty" ).isPresent();
      assertThat( aspect ).operations().isEmpty();
      assertThat( aspect ).events().isEmpty();
      assertThat( aspect ).see()
            .hasSize( 1 )
            .anyMatch( see -> see.contains( "example.com" ) );

      assertThat( aspect ).sourceFile().hasLocationMatching( uri -> uri.toString().endsWith( "AspectWithProperty.ttl" ) );

      final AspectModelFile aspectModelFile = aspect.getSourceFile();
      assertThat( aspectModelFile ).hasLocationMatching( uri -> uri.toString().endsWith( "AspectWithProperty.ttl" ) );

      assertThat( aspect ).hasSinglePropertyThat()
            .isNamedElement()
            .hasName( "testProperty" )
            .hasSameNamespaceAs( aspect )
            .isMandatory()
            .see().hasSize( 2 ).allMatch( see -> see.contains( "example" ) );
      final Property property = aspect.getProperties().get( 0 );
      assertThat( property ).characteristic().hasDataType( XSDDatatype.XSDstring ); // Apache Jena RDF type
      assertThat( property ).characteristic().hasDataType( xsd.string );            // SAMM Java API type

      assertThat( property ).characteristic().dataType().isScalarThat().canBeCastTo( xsd.string );

      final Characteristic characteristic = property.getCharacteristic().orElseThrow();
      assertThat( characteristic ).isNamedElement()
            .hasName( "Text" )
            .hasUrn( SammNs.SAMMC.urn( "Text" ) );
      assertThat( characteristic ).hasDataType( xsd.string );
      assertThat( characteristic ).hasSourceFile( MetaModelFile.CHARACTERISTIC_INSTANCES );
      assertThat( characteristic ).sourceFile()
            .isEqualTo( MetaModelFile.CHARACTERISTIC_INSTANCES )
            .hasLocationMatching( uri -> uri.toString().startsWith( AspectModelUrn.PROTOCOL_AND_NAMESPACE_PREFIX ) );

      final Namespace namespace = aspect.getSourceFile().namespace();
      assertThat( namespace ).contains( aspect );
      assertThat( namespace ).contains( property );
   }

   @Test
   void testAssertionsWithBuilder() {
      final AspectModelUrn namespace = AspectModelUrn.fromUrn( "urn:samm:org.example:1.0.0" );
      final Aspect aspect = aspect( namespace.withName( "Aspect" ) )
            .preferredName( "My Aspect" )
            .description( "This is my Aspect" )
            .property( property( namespace.withName( "property" ) )
                  .preferredName( "My Property" )
                  .preferredName( "Meine Property", Locale.GERMAN )
                  .characteristic( singleEntity( namespace.withName( "MySingleEntity" ) )
                        .dataType( entity( namespace.withName( "MyEntity" ) )
                              .property( property( namespace.withName( "myEntityProperty" ) )
                                    .characteristic( samm_c.Text )
                                    .build() )
                              .build() )
                        .build() )
                  .build() )
            .build();

      assertThat( aspect )
            .hasPreferredName( "My Aspect", Locale.ENGLISH )
            .hasDescriptionMatching( description -> description.getValue().contains( "is my" ) )
            .hasSinglePropertyThat()
            .hasSameNamespaceAs( aspect )
            .hasName( "property" )
            .hasPreferredName( "Meine Property", Locale.GERMAN )
            .characteristic()
            .hasSameNamespaceAs( aspect )
            .hasName( "MySingleEntity" )
            .isSingleEntityThat()
            .dataType()
            .isEntityThat()
            .hasName( "MyEntity" )
            .hasSameNamespaceAs( aspect )
            .hasSinglePropertyThat()
            .hasSameNamespaceAs( aspect )
            .hasName( "myEntityProperty" )
            .hasCharacteristic( samm_c.Text )
            .characteristic()
            .hasDataType( xsd.string )
            .sourceFile()
            .hasLocation( "urn:samm:org.eclipse.esmf.samm:characteristic:" + KnownVersion.getLatest().toVersionString() );

      final RangeConstraint rangeConstraint = rangeConstraint()
            .minValue( value( 5, xsd.integer ) )
            .lowerBound( BoundDefinition.AT_LEAST )
            .build();

      assertThat( rangeConstraint )
            .hasNoMaxValue()
            .hasLowerBound( BoundDefinition.AT_LEAST )
            .hasUpperBound( BoundDefinition.OPEN )
            .minValue()
            .hasValue( 5 )
            .hasType( xsd.integer )
            .canBeCastTo( xsd.decimal );
   }
}
