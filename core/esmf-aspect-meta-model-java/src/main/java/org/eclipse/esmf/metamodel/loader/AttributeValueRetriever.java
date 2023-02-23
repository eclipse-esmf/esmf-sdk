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

package org.eclipse.esmf.metamodel.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;

/**
 * This class provides utilities to retrieve attribute values from model elements, e.g., a bamm:Property's bamm:characteristic.
 * It knows how to handle:
 * <ul>
 *   <li>optionality (e.g., bamm-c:upperBoundDefinition on a bamm-c:RangeConstraint is not mandatory)</li>
 *   <li>n-ary attributes (e.g., bamm:preferredName and bamm:description can appear multiple times)</li>
 *   <li>abstract Properties (i.e., a bnode with bamm:extends used as a Property)</li>
 *   <li>Property references (i.e., a bnode with bamm:property used as a Property)</li>
 * </ul>
 */
public class AttributeValueRetriever {
   protected final SAMM SAMM;

   public AttributeValueRetriever( final SAMM SAMM ) {
      this.SAMM = SAMM;
   }

   /**
    * Returns the value of an attribute on a model element (or its super elements)
    *
    * @param modelElement the model element
    * @param attribute the given attribute
    * @return the statement asserting the value
    * @throws AspectLoadingException when the attribute is not present or is present multiple times, or if bamm:extends is used wrong
    */
   protected Statement attributeValue( final Resource modelElement, final Property attribute ) {
      return optionalAttributeValue( modelElement, attribute ).orElseThrow(
            () -> new AspectLoadingException( "Missing attribute " + attribute + " on " + modelElement ) );
   }

   /**
    * Returns the optional value of an attribute on a model element (or its super elements)
    *
    * @param modelElement the model element
    * @param attribute the given attribute
    * @return the optional statement asserting the value
    */
   protected Optional<Statement> optionalAttributeValue( final Resource modelElement, final Property attribute ) {
      return attributeValues( modelElement, attribute ).stream().sequential().findFirst();
   }

   private boolean isRdfList( final Resource resource ) {
      return resource.isAnon() && (resource.hasProperty( RDF.rest ) || resource.equals( RDF.nil ));
   }

   /**
    * Returns the values of n-ary attributes on a model element (or its super elements), or if a given attribute is an rdf:List, the list elements.
    * The list will be ordered by precedence, e.g., if a Property is present on both the current element and it's superelement, the assertion on the
    * current element will be on a lower list index.
    * Duplicate attribute assertions are removed and only the assertion with the highest precedence will be returned (bottom-most in the inheritance tree),
    * this includes multiple assertions for the same attribute with rdf:langString values with the same language tag. For example:
    *
    * :SuperEntity a bamm:AbstractEntity ;
    *   bamm:description "I'm abstract"@en ;
    *   bamm:description "Ich bin abstrakt"@de ;
    *   bamm:properties () .
    *
    * :MyEntity a bamm:Entity ;
    *   bamm:extends :SuperEntity ;
    *   bamm:description "I'm concrete"@en ;
    *   bamm:properties () .
    *
    * Here, attributeValues( :MyEntity, bamm:description ) will return:
    *   List( Statement( :MyEntity bamm:description "I'm contrete"@en ),
    *         Statement( :SuperEntity bamm:description "Ich bin abstrakt"@de ) )
    *
    * The attribute that is overridden with a new value takes precedence, the one that is not overridden is inherited.
    *
    * @param modelElement the model element
    * @param attribute the given attribute
    * @return the list of statements asserting values for the attribute
    */
   protected List<Statement> attributeValues( final Resource modelElement, final Property attribute ) {
      // Attribute values defined directly on the resource go into the result
      final List<Statement> result = new ArrayList<>();
      for ( final StmtIterator iterator = modelElement.listProperties( attribute ); iterator.hasNext(); ) {
         final Statement statement = iterator.next();
         // If the value happens to be an rdf:List, unroll the list
         if ( statement.getObject().isResource() && isRdfList( statement.getObject().asResource() ) ) {
            statement.getObject().as( RDFList.class )
                  .mapWith( listValue -> modelElement.getModel().createStatement( modelElement, attribute, listValue ) )
                  .forEach( result::add );
         } else {
            result.add( statement );
         }
      }

      // If the model element is a bnode with bamm:property given, it's a Property reference. Follow it to retrieve the sought-for attribute assertions.
      final StmtIterator referenceIterator = modelElement.listProperties( SAMM.property() );
      if ( referenceIterator.hasNext() ) {
         final RDFNode referencedElement = referenceIterator.next().getObject();
         if ( !referencedElement.isResource() ) {
            throw new AspectLoadingException( "bamm:property on " + modelElement + " must point to a Property" );
         }
         result.addAll( attributeValues( referencedElement.asResource(), attribute ) );
         return result;
      }

      // If the model element is bamm:extends another element, retrieve attribute assertions from this supertype as well.
      final StmtIterator extendsIterator = modelElement.listProperties( SAMM._extends() );
      if ( extendsIterator.hasNext() ) {
         final RDFNode superElementNode = extendsIterator.next().getObject();
         if ( !superElementNode.isResource() ) {
            throw new AspectLoadingException( "bamm:extends on " + modelElement + " must point to a valid model element" );
         }
         result.addAll( attributeValues( superElementNode.asResource(), attribute ) );
      }

      // Filter duplicate assertions by precedence
      final List<Statement> filteredResult = new ArrayList<>();
      final Set<String> assertedLanguageTags = new HashSet<>();
      for ( final Statement statement : result ) {
         if ( statement.getObject().isLiteral() ) {
            final String languageTag = statement.getObject().asLiteral().getLanguage();
            if ( !languageTag.isEmpty() ) {
               if ( assertedLanguageTags.contains( languageTag ) ) {
                  continue;
               }
               assertedLanguageTags.add( languageTag );
            }
         }
         filteredResult.add( statement );
      }

      return filteredResult;
   }
}
