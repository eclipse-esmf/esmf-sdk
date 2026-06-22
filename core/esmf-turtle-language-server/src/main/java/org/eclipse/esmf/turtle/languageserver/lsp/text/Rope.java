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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rope implements CharSequence {
   private static final Logger LOG = LoggerFactory.getLogger( Rope.class );
   public static final Rope EMPTY = new Rope( "" );

   String value;
   Rope left;
   Rope right;
   int weight;
   int linebreaks;

   /**
    * Constructs a new rope from a given string value
    *
    * @param value the value
    */
   public Rope( final String value ) {
      this.value = value;
      weight = value.length();
      linebreaks = linebreaks();
   }

   /**
    * Constructs a new rope from left and right children
    *
    * @param left left children
    * @param right right children
    */
   private Rope( final Rope left, final Rope right ) {
      this.left = left;
      this.right = right;
      weight = length( left );
      linebreaks = linebreaks();
   }

   public int linebreaks() {
      return linebreaks( this );
   }

   private int linebreaks( final Rope rope ) {
      if ( rope.left == null ) {
         return (int) rope.value.chars().filter( ch -> ch == '\n' ).count();
      }
      return linebreaks( rope.left ) + linebreaks( rope.right );
   }

   public int weight() {
      return weight;
   }

   /**
    * Returns the length of the rope in characters
    *
    * @return the length
    */
   @Override
   public int length() {
      return length( this );
   }

   private int length( final Rope r ) {
      int len = 0;
      for ( Rope rope = r; rope != null; rope = rope.right ) {
         len += rope.weight;
      }
      return len;
   }

   /**
    * Concatenates another rope to this one
    *
    * @param rope the other rope
    * @return the new rope representing the concatenated result
    */
   public Rope concat( final Rope rope ) {
      return rope == null ? this : concat( this, rope );
   }

   private @Nullable Rope concat( final @Nullable Rope rope1, final @Nullable Rope rope2 ) {
      if ( rope1 == null && rope2 == null ) {
         return EMPTY;
      } else if ( rope1 == null ) {
         return rope2;
      } else if ( rope2 == null ) {
         return rope1;
      }
      return new Rope( rope1, rope2 );
   }

   private char charAt( final Rope node, final int index ) {
      if ( node.left == null ) {
         return node.value.charAt( index );
      }
      return node.weight > index ? charAt( node.left, index ) : charAt( node.right, index - node.weight );
   }

   /**
    * Returns the character at given index
    *
    * @param index the index of the {@code char} value to be returned
    *
    * @return the character at the index
    */
   @Override
   public char charAt( final int index ) {
      return charAt( this, index );
   }

   /**
    * Splits this rope into two parts at the given index
    *
    * @param index the index
    * @return an array with two elements, representing the left and right parts of the index. Both
    *         elements could be null,
    *         depending on the current rope value and the index
    */
   public Rope[] split( final int index ) {
      return split( this, index );
   }

   private @Nullable Rope @NonNull [] split( final Rope node, final int index ) {
      final Rope node0;
      final Rope node1;
      if ( node.left == null ) {
         if ( index == 0 ) {
            node0 = null;
            node1 = node;
         } else if ( index == node.weight ) {
            node0 = node;
            node1 = null;
         } else {
            node0 = new Rope( node.value.substring( 0, index ) );
            node1 = new Rope( node.value.substring( index, node.weight ) );
         }
      } else if ( index == node.weight ) {
         node0 = node.left;
         node1 = node.right;
      } else if ( index < node.weight ) {
         final Rope[] parts = split( node.left, index );
         node0 = parts[0];
         node1 = concat( parts[1], node.right );
      } else {
         final Rope[] parts = split( node.right, index - node.weight );
         node0 = concat( node.left, parts[0] );
         node1 = parts[1];
      }
      return new Rope[] { node0, node1 };
   }

   /**
    * Returns the subsequence of characters between to indices
    *
    * @param start the start index, inclusive
    * @param end the end index, exclusive
    * @return the subsequence of characters
    */
   @Override
   public @NonNull Rope subSequence( final int start, final int end ) {
      final Rope result = split( start )[1].split( end - start )[0];
      return result == null ? EMPTY : result;
   }

   /**
    * Inserts another rope at the given index. The index must be inside the rope.
    *
    * @param rope the other rope
    * @param index the index. This must be less or equal than this rope's length
    * @return the resulting new rope
    */
   public Rope insert( final Rope rope, final int index ) {
      final Rope[] parts = split( index );
      return concat( concat( parts[0], rope ), parts[1] );
   }

   /**
    * Deletes a section at the given index. The index and index+length must be inside the rope.
    *
    * @param index the index
    * @param length the length to delete
    * @return the resulting new rope
    */
   public Rope delete( final int index, final int length ) {
      final Rope[] parts = split( index );
      final Rope result;
      if ( parts[1] == null ) {
         result = concat( parts[0], null );
      } else {
         result = concat( parts[0], parts[1].split( length )[1] );
      }
      return result == null ? EMPTY : result;
   }

   /**
    * Returns the index of the nth linebreak in a string, or -1 if no nth linebreak exists
    *
    * @param string the text to search in
    * @param n the number of the linebreak
    * @return the index or -1
    */
   private static int indexOfNthLinebreak( final String string, final int n ) {
      int counter = n;
      int pos = string.indexOf( '\n' );
      while ( --counter > 0 && pos != -1 ) {
         pos = string.indexOf( '\n', pos + 1 );
      }
      return pos;
   }

   public Rope update( final int startLine, final int startColumn, final int endLine, final int endColumn, final String newContent ) {
      final int startIndex = getIndex( startLine, startColumn );
      final int endIndex = getIndex( endLine, endColumn );
      // final int offset = endIndex == length() ? 0 : 1;
      final int offset = 1;
      final Rope resultAfterDeletion = startIndex == endIndex
            ? this
            : delete( startIndex, endIndex - startIndex + offset );
      return newContent.isEmpty() ? resultAfterDeletion : resultAfterDeletion.insert( new Rope( newContent ), startIndex );
   }

   public int getIndex( final int targetLine, final int targetColumn ) {
      if ( targetLine == 0 ) {
         return targetColumn;
      }
      if ( targetLine < 0 || targetColumn < 0 ) {
         return -1;
      }

      int currentIndex = 0;
      int currentLine = 0;
      final int length = length();

      // Traverse the rope character by character until we reach the target line
      while ( currentIndex < length && currentLine < targetLine ) {
         if ( charAt( currentIndex ) == '\n' ) {
            currentLine++;
         }
         currentIndex++;
      }

      // Add the column offset
      return currentIndex + targetColumn;
   }

   /**
    * Prints the rope as a tree structure
    *
    * @return the tree structure as a visual string
    */
   public String print() {
      return print( this, 0 );
   }

   private String print( final Rope rope, final int indentation ) {
      if ( rope == null ) {
         return "[]";
      }
      final String indentString = new String( new char[indentation] ).replace( "\0", " " );
      return "[%s]\n%s-L:%s\n%s-R:%s".formatted( rope.value,
            indentString, print( rope.left, indentation + 2 ),
            indentString, print( rope.right, indentation + 2 ) );
   }

   public Rope rebalance() {
      final Rope[] leaves = leaves();
      return merge( leaves, 0, leaves.length );
   }

   private Rope[] leaves() {
      if ( left == null && right == null ) {
         return new Rope[] { this };
      }
      final Rope[] leftLeaves = left == null ? new Rope[0] : left.leaves();
      final Rope[] rightLeaves = right == null ? new Rope[0] : right.leaves();
      final Rope[] result = Arrays.copyOf( leftLeaves, leftLeaves.length + rightLeaves.length );
      System.arraycopy( rightLeaves, 0, result, leftLeaves.length, rightLeaves.length );
      return result;
   }

   private Rope merge( final Rope[] leaves, final int start, final int end ) {
      final int range = end - start;
      if ( range == 1 ) {
         return leaves[start];
      }
      if ( range == 2 ) {
         return new Rope( leaves[start], leaves[start + 1] );
      }
      final int mid = start + ( range / 2 );
      return new Rope( merge( leaves, start, mid ), merge( leaves, mid, end ) );
   }

   @Override
   public @NonNull String toString() {
      try ( final InputStream inputStream = inputStream( StandardCharsets.UTF_8 ) ) {
         return new String( inputStream.readAllBytes(), StandardCharsets.UTF_8 );
      } catch ( final IOException exception ) {
         throw new RuntimeException( exception );
      }
   }

   /**
    * Equality of two ropes is given when the strings they encode are equal, regardless how their
    * internal tree structure looks like
    *
    * @param object the other object
    * @return true when the other object is a rope with the same encoded string
    */
   @Override
   public boolean equals( final Object object ) {
      if ( this == object ) {
         return true;
      }
      if ( object == null || getClass() != object.getClass() ) {
         return false;
      }
      final Rope rope = (Rope) object;
      return rope.toString().equals( toString() );
   }

   @Override
   public int hashCode() {
      return toString().hashCode();
   }

   public InputStream inputStream( final Charset encoding ) {
      return new RopeInputStream( encoding );
   }

   public InputStream inputStream() {
      return inputStream( StandardCharsets.UTF_8 );
   }

   /**
    * Reads at most many bytes from the given offset into the buffer array, as the array provides, or
    * fewer, if
    * not as many are left at the offset. Returns 0 if the end of the source code was reached,
    * otherwise the number of bytes read.
    *
    * @param buffer the buffer to write to
    * @param offset offset to read from
    * @return the number of bytes read
    */
   public int read( final byte[] buffer, final int offset ) {
      if ( buffer == null || offset < 0 || offset >= buffer.length ) {
         return 0;
      }

      try ( final InputStream stream = inputStream( StandardCharsets.UTF_8 ) ) {
         final int bytesRead = stream.read( buffer, offset, buffer.length );
         return bytesRead == -1 ? 0 : bytesRead;
      } catch ( final Exception exception ) {
         LOG.debug( "Exception while reading document content", exception );
         return 0;
      }
   }

   public class RopeInputStream extends InputStream {
      private final Deque<Rope> stack;
      private byte @Nullable [] currentBytes;
      private int currentPosition;
      private final Charset encoding;

      public RopeInputStream( final Charset encoding ) {
         this.encoding = encoding;
         stack = new ArrayDeque<>();
         currentBytes = null;
         currentPosition = 0;
         pushLeftmostPath( Rope.this );
      }

      private void pushLeftmostPath( final Rope node ) {
         Rope current = node;
         while ( current != null ) {
            if ( current.left == null ) {
               currentBytes = current.value.getBytes( encoding );
               currentPosition = 0;
               break;
            } else {
               stack.push( current );
               current = current.left;
            }
         }
      }

      private boolean moveToNextLeaf() {
         while ( !stack.isEmpty() ) {
            final Rope parent = stack.pop();
            if ( parent.right != null ) {
               pushLeftmostPath( parent.right );
               return true;
            }
         }
         currentBytes = null;
         return false;
      }

      @Override
      public int read() {
         while ( currentBytes == null || currentPosition >= currentBytes.length ) {
            if ( !moveToNextLeaf() ) {
               return -1;
            }
         }

         // Return byte as unsigned int (0-255)
         final int result = currentBytes[currentPosition] & 0xFF;
         currentPosition++;
         return result;
      }
   }
}
