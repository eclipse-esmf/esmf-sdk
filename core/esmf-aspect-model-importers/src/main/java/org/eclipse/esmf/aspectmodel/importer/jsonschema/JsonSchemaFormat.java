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

package org.eclipse.esmf.aspectmodel.importer.jsonschema;

import static org.eclipse.esmf.metamodel.builder.SammBuilder.regularExpressionConstraint;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;

/**
 * Represents JSON Schema <a href="https://www.learnjsonschema.com/2020-12/format-annotation/format/">format</a> fields
 */
public enum JsonSchemaFormat {
   DATE,

   DATE_TIME,

   DURATION,

   /**
    * {@link RegularExpressionConstraint} for emails, as defined in RFC 5321
    */
   EMAIL( urn -> regularExpressionConstraint( urn )
         .preferredName( "email address" )
         .description( "Matches email addresses as defined in RFC 5321 section 4.1.2" )
         .see( "https://www.rfc-editor.org/rfc/rfc5321.html#section-4.1.2" )
         .value( Pattern.compile( "^([a-zA-Z0-9+._/&!][-a-zA-Z0-9+._/&!]*)@("
               + "([a-zA-Z0-9][-a-zA-Z0-9]*\\.)([-a-zA-Z0-9]+\\.)*[a-zA-Z]{2,})$" ) )
         .build() ),

   /**
    * {@link RegularExpressionConstraint} for hostnames as defined in RFC 1123
    */
   HOSTNAME( urn -> regularExpressionConstraint( urn )
         .preferredName( "hostname" )
         .description( "Matches hostnames as defined in RFC 1123" )
         .see( "https://www.rfc-editor.org/rfc/rfc1123.html" )
         .value( Pattern.compile( "^[a-z0-9]([-a-z0-9]*[a-z0-9])?$" ) )
         .build() ),

   /**
    * {@link RegularExpressionConstraint} for internationalized email addresses, as defined in RFC 6531 and implemented in <a
    * href="https://gist.github.com/baker-ling/3b4b014ee809aa9732f9873fe060c098">gist</a>.
    * Adapted for use in Java: (1) removed redundant escapes, (2) added character ranges to match case-insensitive (instead of i flag);
    * replaced Unicode code point references from \\u to \\x.
    */
   IDN_EMAIL( urn -> regularExpressionConstraint( urn )
         .preferredName( "internationalized email address" )
         .description( "Matches international email addresses as defined in RFC 6531 section 3.3" )
         .see( "https://datatracker.ietf.org/doc/html/rfc6531#section-3.3" )
         .value( Pattern.compile(
               "^(([0-9a-zA-Z!#$%&'*+\\-/=?^_`{|}~\\x{80}-\\x{10FFFF}]+(\\"
                     + ".[0-9a-zA-Z!#$%&'*+\\-/=?^_`{|}~\\x{80}-\\x{10FFFF}]+)*)|(\""
                     + "([\\x20-\\x21\\x23-\\x5B\\x5D-\\x7E\\x{80}-\\x{10FFFF}]|\\\\[\\x20-\\x7E])*\"))(?<!.{64,})@"
                     + "((\\[((\\d{1,3}(\\.\\d{1,3}){3})|"
                     + "(IPv6:[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7})|"
                     + "(IPv6:([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){0,5})?::([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){0,5})?)|"
                     + "(IPv6:[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){5}:\\d{1,3}(\\.\\d{1,3}){3})|(IPv6:"
                     + "([0-9a-fA-F]{1,4}"
                     + "(:[0-9a-fA-F]{1,4}){0,3})?::([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){0,3}:)?\\d{1,3}(\\.\\d{1,3}){3})|"
                     + "([a-zA-Z0-9\\-]*\\[[a-zA-Z0-9]:[\\x21-\\x5A\\x5E-\\x7E]+))])|((?!.{256,})"
                     + "([0-9a-zA-Z\\x{80}-\\x{10FFFF}]([0-9a-zA-Z\\-\\x{80}-\\x{10FFFF}]*[0-9a-zA-Z\\x{80}-\\x{10FFFF}])?)(\\."
                     + "([0-9a-zA-Z\\x{80}-\\x{10FFFF}]([0-9a-zA-Z\\-\\x{80}-\\x{10FFFF}]*[0-9a-zA-Z\\x{80}-\\x{10FFFF}])?))*))$" ) )
         .build() ),

   /**
    * {@link RegularExpressionConstraint} for internationalized hostnames as defined in RFC 1123 and RFC 5890
    */
   IDN_HOSTNAME( urn -> regularExpressionConstraint( urn )
         .preferredName( "internationalized host name" )
         .description(
               "Matches hostnames as defined in RFC 1123 or internationalized hostnames as defined by RFC 5890 section 2.3"
                     + ".2.3" )
         .see( "https://www.rfc-editor.org/rfc/rfc1123.html" )
         .see( "https://www.rfc-editor.org/rfc/rfc5890.html#section-2.3.2.3" )
         .value( Pattern.compile( "^[a-z0-9]([-a-z0-9]*[a-z0-9])?$" ) )
         .build() ),

   /**
    * {@link RegularExpressionConstraint} for IPv4 addresses as defined in RFC 2673
    */
   IPV4( urn -> regularExpressionConstraint( urn )
         .preferredName( "IPv4 address" )
         .description( "Matches IPv4 addresses as defined in RFC 2763 section 3.2" )
         .see( "https://www.rfc-editor.org/rfc/rfc2673.html#section-3.2" )
         .value( Pattern.compile( "^(((?!25?[6-9])[12]\\d|[1-9])?\\d\\.?\\b){4}$" ) )
         .build() ),

   /**
    * {@link RegularExpressionConstraint} for IPv6 addresses as defined in RFC 4291 section 2.2 and implemented in <a
    * href="https://stackoverflow.com/a/17871737/12105820">this post</a>.
    * Adapted: Simplified groupings (e.g., {7,7} -> {7})
    */
   IPV6( urn -> regularExpressionConstraint( urn )
         .preferredName( "IPv6 address" )
         .description( "Matches IPv6 addresses as defined in RFC 4291 section 2.2" )
         .see( "https://www.rfc-editor.org/rfc/rfc4291.html#section-2.2" )
         .value( Pattern.compile(
               "^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,"
                     + "6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}"
                     + "(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}"
                     + "(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:"
                     + "(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})"
                     + "?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,"
                     + "4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))$" ) )
         .build() ),

   IRI,

   IRI_REFERENCE,

   JSON_POINTER,

   REGEX,

   RELATIVE_JSON_POINTER,

   TIME,

   URI,

   URI_REFERENCE,

   /**
    * {@link RegularExpressionConstraint} for URI templates as defined in RFC 6570 and implemented in <a
    * href="https://stackoverflow.com/a/61645285/12105820">this post</a>
    */
   URI_TEMPLATE( urn -> regularExpressionConstraint( urn )
         .preferredName( "URI template" )
         .description( "Matches URI templates as defined by RFC 6570" )
         .see( "https://www.rfc-editor.org/rfc/rfc6570.html" )
         .value( Pattern.compile(
               "^([^\\x00-\\x20\\x7f\"'%<>\\\\^`{|}]|%[0-9A-Fa-f]{2}|\\{[+#./;?&=,!@|]?("
                     + "(\\w|%[0-9A-Fa-f]{2})(\\.?(\\w|%[0-9A-Fa-f]{2}))*(:[1-9]\\d{0,3}|\\*)?)(,((\\w|%[0-9A-Fa-f]{2})(\\.?"
                     + "(\\w|%[0-9A-Fa-f]{2}))*(:[1-9]\\d{0,3}|\\*)?))*})*$" ) )
         .build() ),

   /**
    * {@link RegularExpressionConstraint} for UUIDs as defined in RFC 4122
    */
   UUID( urn -> regularExpressionConstraint( urn )
         .preferredName( "universally unique identifier (UUID)" )
         .description( "Matches UUIDs as defined by RFC 4122" )
         .see( "https://www.rfc-editor.org/rfc/rfc4122.html" )
         .value( Pattern.compile( "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$" ) )
         .build() );

   private final Optional<Function<AspectModelUrn, RegularExpressionConstraint>> constraint;

   JsonSchemaFormat() {
      constraint = Optional.empty();
   }

   JsonSchemaFormat( final Function<AspectModelUrn, RegularExpressionConstraint> constraint ) {
      this.constraint = Optional.of( constraint );
   }

   /**
    * Returns an anonymous regular expression constraint for the format, if one exists
    *
    * @return the regular expression constraint
    */
   public Optional<RegularExpressionConstraint> getRegularExpressionConstraint() {
      return getRegularExpressionConstraint( null );
   }

   /**
    * Returns a named regular expression constraint for the format, if one exists
    *
    * @param urn the URN for the regular expression constraint
    * @return the constraint
    */
   public Optional<RegularExpressionConstraint> getRegularExpressionConstraint( final AspectModelUrn urn ) {
      return constraint.map( constraint -> constraint.apply( urn ) );
   }

   public String key() {
      return toString().toLowerCase().replace( "_", "-" );
   }
}
