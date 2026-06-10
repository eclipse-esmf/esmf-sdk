/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

class RopeTest {
   /**
    * Controls which input strings should be used for tests. Ropes should work for any string, so we
    * let Jqwik choose any kind of string.
    *
    * @return an arbitrary string
    */
   @Provide
   Arbitrary<String> inputString() {
      return Arbitraries.strings();
   }

   @Provide
   Arbitrary<String> basicString() {
      return Arbitraries.strings().alpha().numeric().excludeChars( '\n' );
   }

   @Property
   void ropesCanBeConcatenated( @ForAll( "inputString" ) final String string1, @ForAll( "inputString" ) final String string2 ) {
      final Rope rope1 = new Rope( string1 );
      final Rope rope2 = new Rope( string2 );
      final Rope result = rope1.concat( rope2 );
      final Rope concatted = new Rope( string1 + string2 );
      assertThat( result ).isEqualTo( concatted );
      assertThat( result.toString() ).isEqualTo( string1 + string2 );
      assertThat( concatted.toString() ).isEqualTo( string1 + string2 );
   }

   @Property
   void ropesCanBeSplit( @ForAll( "inputString" ) final String string ) {
      final Rope rope = new Rope( string );
      for ( int i = 0; i <= rope.length(); i++ ) {
         final Rope[] parts = rope.split( i );
         if ( parts[0] == null || parts[1] == null ) {
            continue;
         }
         assertThat( parts[0].length() + parts[1].length() ).isEqualTo( rope.length() );
         assertThat( parts[0].value + parts[1].value ).isEqualTo( rope.toString() );
      }
   }

   @Property
   void ropesCanReturnSubsequences( @ForAll( "inputString" ) final String string ) {
      final Rope rope = new Rope( string );
      for ( int i = 0; i <= rope.length(); i++ ) {
         for ( int j = i; j <= rope.length(); j++ ) {
            if ( i != j ) {
               assertThat( rope.subSequence( i, j ).toString() ).isEqualTo( string.substring( i, j ) );
            }
         }
      }
   }

   @Property
   void ropesCanInsertStrings( @ForAll( "inputString" ) final String string1, @ForAll( "inputString" ) final String string2 ) {
      final Rope rope1 = new Rope( string1 );
      final Rope rope2 = new Rope( string2 );
      for ( int i = 0; i <= rope1.length(); i++ ) {
         final Rope ropeResult = rope1.insert( rope2, i );
         final String stringResult = string1.substring( 0, i ) + string2 + string1.substring( i );
         assertThat( ropeResult.toString() ).isEqualTo( stringResult );
      }
   }

   @Property
   void ropesCanBeRebalanced( @ForAll( "inputString" ) final String string1, @ForAll( "inputString" ) final String string2 ) {
      final Rope rope1 = new Rope( string1 );
      final Rope rope2 = new Rope( string2 );

      // Insert lots of stuff. This will degenerate the tree
      Rope ropeResult = rope1;
      for ( int j = 0; j <= 10; j++ ) {
         if ( j > ropeResult.length() ) {
            break;
         }
         ropeResult = ropeResult.insert( rope2, j );
      }
      final Rope balanced = ropeResult.rebalance();
      assertThat( ropeResult ).isEqualTo( balanced );
      assertThat( ropeResult.toString() ).isEqualTo( balanced.toString() );
   }

   @Property
   void ropesCanDeleteStrings( @ForAll( "inputString" ) final String string ) {
      final Random random = ThreadLocalRandom.current();
      if ( string != null && string.isEmpty() ) {
         return;
      }
      assertThat( string ).isNotNull();
      final int index = random.nextInt( 0, string.length() );
      final int length = random.nextInt( 0, string.length() - index );
      final Rope rope = new Rope( string );

      final String stringResult = string.substring( 0, index ) + string.substring( index + length );
      final Rope result = rope.delete( index, length );
      assertThat( result.toString() ).isEqualTo( stringResult );
   }

   @Property
   void testInputStreamAndToStringConsistency( @ForAll( "inputString" ) final String string ) throws IOException {
      final Rope rope = new Rope( string );
      for ( final Charset encoding : List.of( StandardCharsets.UTF_8, StandardCharsets.UTF_16 ) ) {
         final byte[] bytesFromToString = rope.toString().getBytes( encoding );
         try ( final InputStream inputStream = rope.inputStream( encoding ) ) {
            final byte[] bytesFromInputStream = inputStream.readAllBytes();
            assertThat( bytesFromToString )
                  .as( () -> "Compared string: " + string )
                  .hasSameSizeAs( bytesFromInputStream )
                  .isEqualTo( bytesFromInputStream );
         }
      }
   }

   @Test
   void testSubSequence() {
      final Rope rope = new Rope( "abc\ndef\n" );
      assertThat( rope.subSequence( 0, 3 ).toString() ).isEqualTo( "abc" );
      assertThat( rope.subSequence( 4, 7 ).toString() ).isEqualTo( "def" );
   }

   @Test
   void testGetIndex() {
      final Rope rope = new Rope( "abc\ndef\nghi\n" );
      assertThat( rope.getIndex( 0, 0 ) ).isEqualTo( 0 );
      assertThat( rope.getIndex( 0, 2 ) ).isEqualTo( 2 );
      assertThat( rope.getIndex( 1, 0 ) ).isEqualTo( 4 );
      assertThat( rope.getIndex( 1, 3 ) ).isEqualTo( 7 );
      assertThat( rope.getIndex( 2, 0 ) ).isEqualTo( 8 );
      assertThat( rope.getIndex( 2, 2 ) ).isEqualTo( 10 );

      final Rope rope2 = new Rope( "\n\n" );
      assertThat( rope2.getIndex( 0, 0 ) ).isEqualTo( 0 );
      assertThat( rope2.getIndex( 1, 0 ) ).isEqualTo( 1 );
      assertThat( rope2.getIndex( 2, 0 ) ).isEqualTo( 2 );
   }

   @Property
   void randomOperationsMatchStringBehavior( @ForAll( "inputString" ) final String initialString ) {
      if ( initialString.isEmpty() ) {
         return;
      }

      final Random random = ThreadLocalRandom.current();
      final int operationCount = random.nextInt( 5, 15 );

      Rope rope = new Rope( initialString );
      String string = initialString;

      enum Operation {
         DELETE, INSERT, UPDATE
      }

      for ( int i = 0; i < operationCount; i++ ) {
         final Operation operation = Operation.values()[random.nextInt( 3 )];

         try {
            if ( string.isEmpty() ) {
               // Can only insert if string is empty
               final String toInsert = "test" + i;
               rope = rope.insert( new Rope( toInsert ), 0 );
               assertThat( rope ).isNotNull();
               string = toInsert + string;
            } else if ( operation == Operation.DELETE ) {
               final int start = random.nextInt( string.length() );
               final int maxLength = string.length() - start;
               final int length = maxLength > 0 ? random.nextInt( 1, maxLength + 1 ) : 0;

               if ( length > 0 ) {
                  rope = rope.delete( start, length );
                  string = string.substring( 0, start ) + string.substring( start + length );
               }
            } else if ( operation == Operation.INSERT ) {
               final int index = random.nextInt( string.length() + 1 );
               final String toInsert = "ins" + i;

               rope = rope.insert( new Rope( toInsert ), index );
               string = string.substring( 0, index ) + toInsert + string.substring( index );
            } else {
               // Update (replace) operation - delete then insert
               final int start = random.nextInt( string.length() );
               final int maxLength = string.length() - start;
               final int length = maxLength > 0 ? random.nextInt( 1, maxLength + 1 ) : 0;
               final String replacement = "upd" + i;

               if ( length > 0 ) {
                  rope = rope.delete( start, length ).insert( new Rope( replacement ), start );
                  string = string.substring( 0, start ) + replacement + string.substring( start + length );
               }
            }

            assertThat( rope.toString() )
                  .as( "After operation %d (type=%d)", i, operation )
                  .isEqualTo( string );

         } catch ( final Exception exception ) {
            final String ropeLength = rope == null ? "?" : "" + rope.length();
            throw new AssertionError( "Failed at operation " + i + " with rope length " + ropeLength
                  + " and string length " + string.length() + ": " + exception.getMessage(), exception );
         }
      }

      assertThat( rope.toString() ).isEqualTo( string );
   }

   @Test
   void testUpdate() {
      final Rope rope = new Rope( "\n" );
      final Rope result = rope.update( 0, 1, 0, 1, "X" );
      assertThat( result.toString() ).isEqualTo( "\nX" );

      final Rope rope2 = new Rope( "abcd\nefgh" );
      final Rope result2 = rope2.update( 1, 1, 1, 3, "X" );
      assertThat( result2.toString() ).isEqualTo( "abcd\neX" );

      final Rope rope3 = new Rope( "\nabcd" );
      final Rope result3 = rope3.update( 0, 0, 1, 3, "X" );
      assertThat( result3.toString() ).isEqualTo( "X" );

      final Rope rope4 = new Rope( "\na" );
      final Rope result4 = rope4.update( 0, 0, 1, 0, "X" );
      assertThat( result4.toString() ).isEqualTo( "X" );
   }

   @Test
   void testMixedOperationsWithKnownInput() {
      // Test with a known input to ensure predictable behavior
      String string = "line1\nline2\nline3\n";
      Rope rope = new Rope( string );

      // Operation 1: Delete "line2\n"
      rope = rope.delete( 6, 6 );
      string = string.substring( 0, 6 ) + string.substring( 12 );
      assertThat( rope.toString() ).isEqualTo( string ).isEqualTo( "line1\nline3\n" );

      // Operation 2: Insert "inserted\n" at position 6
      rope = rope.insert( new Rope( "inserted\n" ), 6 );
      string = string.substring( 0, 6 ) + "inserted\n" + string.substring( 6 );
      assertThat( rope.toString() ).isEqualTo( string ).isEqualTo( "line1\ninserted\nline3\n" );

      // Operation 3: Update using line/column (replace "inserted" with "modified")
      rope = rope.update( 1, 0, 1, 7, "modified" );
      string = "line1\nmodified\nline3\n";
      assertThat( rope.toString() ).isEqualTo( string );

      // Operation 4: Delete from middle of one line to middle of another
      rope = rope.update( 0, 3, 2, 3, "X" );
      string = "linX3\n";
      assertThat( rope.toString() ).isEqualTo( string );
   }

   @Property
   void randomDeletionsAndInsertionsPreserveLength( @ForAll( "inputString" ) final String initialString ) {
      if ( initialString.length() < 5 ) {
         return;
      }

      final Random random = ThreadLocalRandom.current();
      Rope rope = new Rope( initialString );
      String string = initialString;

      // Perform 10 delete-insert pairs that should preserve total content
      for ( int i = 0; i < 10; i++ ) {
         final int index = random.nextInt( string.length() );
         final int deleteLength = Math.min( random.nextInt( 1, 4 ), string.length() - index );

         // Delete
         final String deleted = string.substring( index, index + deleteLength );
         rope = rope.delete( index, deleteLength );
         string = string.substring( 0, index ) + string.substring( index + deleteLength );

         assertThat( rope.toString() ).isEqualTo( string );

         // Re-insert at a different position
         final int insertIndex = string.isEmpty() ? 0 : random.nextInt( string.length() + 1 );
         rope = rope.insert( new Rope( deleted ), insertIndex );
         string = string.substring( 0, insertIndex ) + deleted + string.substring( insertIndex );

         assertThat( rope.toString() ).isEqualTo( string );
      }

      // Verify the characters are still all present (may be reordered)
      assertThat( rope.length() ).isEqualTo( initialString.length() );
   }
}
