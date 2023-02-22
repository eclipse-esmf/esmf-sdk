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
package org.eclipse.esmf.aspectmodel.validation.report;

import java.util.Map;

import org.apache.jena.riot.RiotException;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import com.google.common.base.Splitter;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Provides the different types of validation errors that can occur. Uses the visitor pattern to dispatch to one
 * of the  implementations.
 *
 * @deprecated Replaced by {@link Violation}
 */
@SuppressWarnings( { "squid:S1610", "This should not be converted into an interface, because it is a sealed class." } )
@Deprecated( forRemoval = true )
public abstract class ValidationError {
   public static final String MESSAGE_COULD_NOT_RETRIEVE_BAMM_VERSION = "Could not retrieve the version of the BAMM the Aspect is referring to";
   public static final String MESSAGE_BAMM_VERSION_NOT_SUPPORTED = "The used meta model version is not supported";
   public static final String MESSAGE_SYNTAX_ERROR = "A syntax error was encountered";
   public static final String MESSAGE_MODEL_RESOLUTION_ERROR = "Model could not be resolved entirely";

   private ValidationError() {
   }

   public interface Visitor<T> {
      T visit( final Semantic error );

      T visit( final Syntactic error );

      T visit( final Processing error );
   }

   /**
    * Represents a validation result as described by the
    * <a href="https://www.w3.org/TR/shacl/#results-validation-result">SHACL specification</a>.
    */
   @Value
   @EqualsAndHashCode( callSuper = false )
   public static class Semantic extends ValidationError {
      String resultMessage;
      String focusNode;
      String resultPath;
      String resultSeverity;
      String value;

      @Override
      public String toString() {
         return String.format( "resultMessage:  %s\n"
                     + "focusNode:      %s\n"
                     + "resultPath:     %s\n"
                     + "resultSeverity: %s\n"
                     + "value:          %s\n",
               resultMessage, focusNode, resultPath, resultSeverity, value );
      }

      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visit( this );
      }
   }

   /**
    * A Syntactic is created from a {@link RiotException} which is thrown by Jena when the given Aspect
    * Model contains invalid syntax. This includes trying to create a {@link org.apache.jena.rdf.model.Model} from a
    * format other than Turtle.
    *
    * The message given in the {@link RiotException} can be misleading which is why the line and column number are
    * extracted from this message and used to create a message which is helpful to the users of this library.
    */
   @Value
   @EqualsAndHashCode( callSuper = false )
   public static class Syntactic extends ValidationError {
      int lineNumber;
      int columnNumber;
      String originalExceptionMessage;

      public Syntactic( final RiotException riotException ) {
         originalExceptionMessage = riotException.getMessage();

         final String locationInfoPart = originalExceptionMessage
               .substring( originalExceptionMessage.indexOf( '[' ) + 1, originalExceptionMessage.indexOf( ']' ) );
         final Map<String, String> locationInfo = Splitter.on( "," ).trimResults()
               .withKeyValueSeparator( Splitter.on( ":" ).trimResults() )
               .split( locationInfoPart );

         lineNumber = Integer.parseInt( locationInfo.get( "line" ) );
         columnNumber = Integer.parseInt( locationInfo.get( "col" ) );
      }

      @Override
      public String toString() {
         return String.format(
               "The Aspect Model contains invalid syntax at line number %s and column number %s.", lineNumber,
               columnNumber );
      }

      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visit( this );
      }
   }

   /**
    * The validation failed for a reason other than syntax or semantic errors, e.g., the Aspect Model refers to
    * an unknown version of the Meta Model.
    */
   @Value
   @EqualsAndHashCode( callSuper = false )
   public static class Processing extends ValidationError {
      String message;

      @Override
      public String toString() {
         return "An error occured while trying to validate the Aspect Model: " + message;
      }

      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visit( this );
      }
   }

   /**
    * Visitor pattern dispatch
    *
    * @param visitor The visitor
    * @param <T> The intended return type
    * @return The result that the visitor returns
    */
   public abstract <T> T accept( final Visitor<T> visitor );
}
