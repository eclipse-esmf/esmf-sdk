/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.validation.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.RegularExpressionConstraintViolation;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SAMMC;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.parsing.dflt.RgxGenParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

public class RegularExpressionExampleValueValidator implements CustomValidation {

   private final SAMM samm = SammNs.SAMM;
   private final SAMMC sammc = SammNs.SAMMC;

   private final List<Violation> violations = new ArrayList<>();

   @Override
   public List<Violation> validateModel( final Model rawModel ) {
      violations.clear();

      // Traverse all Aspects in the model
      final StmtIterator aspects = rawModel.listStatements( null, RDF.type, samm.Aspect() );
      while ( aspects.hasNext() ) {
         final Statement aspect = aspects.nextStatement();
         final Statement properties = aspect.getSubject().getProperty( samm.properties() );
         if ( properties != null ) {
            final Iterator<RDFNode> aspectProperties = properties.getList().iterator();
            while ( aspectProperties.hasNext() ) {
               final RDFNode propRef = aspectProperties.next();
               traverseRecursively( propRef.asResource() );
            }
         }
      }

      return violations;
   }

   /**
    * Recursively traverses the model starting from the given resource.
    * Checks for RegularExpressionConstraint without exampleValue and descends into all relevant paths:
    * - characteristic
    * - dataType
    * - baseCharacteristic
    * - properties
    */
   private void traverseRecursively( final Resource resource ) {
      checkRegularExpressionConstraint( resource );

      // Traverse characteristic if present
      Statement characteristicStmt = resource.getProperty( samm.characteristic() );
      if ( characteristicStmt != null ) {
         Resource characteristic = characteristicStmt.getObject().asResource();
         traverseRecursively( characteristic );

         // Traverse dataType if present in characteristic
         Statement dataTypeStmt = characteristic.getProperty( samm.dataType() );
         if ( dataTypeStmt != null ) {
            Resource dataType = dataTypeStmt.getObject().asResource();
            traverseRecursively( dataType );
         }

         // Traverse baseCharacteristic if present in characteristic
         Statement baseCharacteristicStmt = characteristic.getProperty( sammc.baseCharacteristic() );
         if ( baseCharacteristicStmt != null ) {
            Resource baseCharacteristic = baseCharacteristicStmt.getObject().asResource();
            traverseRecursively( baseCharacteristic );
         }
      }

      // Traverse properties if present
      Statement propertiesStmt = resource.getProperty( samm.properties() );
      if ( propertiesStmt != null ) {
         Iterator<RDFNode> propertyNodes = propertiesStmt.getList().iterator();
         while ( propertyNodes.hasNext() ) {
            RDFNode node = propertyNodes.next();
            if ( node.isResource() ) {
               traverseRecursively( node.asResource() );
            }
         }
      }
   }

   /**
    * Checks if the resource has a RegularExpressionConstraint that we can't generate and no exampleValue exists.
    * If so, adds a violation.
    */
   private void checkRegularExpressionConstraint( final Resource resource ) {
      if ( resource.getProperty( samm.exampleValue() ) == null ) {
         // Check for presence of constraint
         Statement characteristicStmt = resource.getProperty( samm.characteristic() );
         if ( characteristicStmt != null ) {
            Resource characteristic = characteristicStmt.getObject().asResource();
            Statement constraintStmt = characteristic.getProperty( sammc.constraint() );
            if ( constraintStmt != null ) {
               Resource constraintResource = constraintStmt.getObject().asResource();
               Property typeProp = ResourceFactory.createProperty( "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" );
               boolean isRegExConstraint = constraintResource.hasProperty( typeProp, sammc.RegularExpressionConstraint() );
               if ( isRegExConstraint ) {
                  Property valueProp = samm.value();
                  String regexValue = constraintResource.getProperty( valueProp ).getString();
                  try {
                     RgxGen.parse( regexValue );
                  } catch ( final RgxGenParseException exception ) {
                     reportViolation( constraintResource, regexValue );
                  }
               }
            }
         }
      }
   }

   private void reportViolation( final Resource constraintResource, final String regexValue ) {
      violations.add( new RegularExpressionConstraintViolation( constraintResource, regexValue ) );
   }
}