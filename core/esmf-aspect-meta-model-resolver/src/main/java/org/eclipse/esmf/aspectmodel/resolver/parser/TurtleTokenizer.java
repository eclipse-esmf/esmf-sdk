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

package org.eclipse.esmf.aspectmodel.resolver.parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;
import org.apache.jena.atlas.iterator.PeekIterator;
import org.apache.jena.riot.RiotParseException;
import org.apache.jena.riot.system.ErrorHandler;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.riot.tokens.TokenizerText;

/**
 * This tokenizer implementation wraps a {@link TokenizerText} and does things on top: (1) It swallows the wrapped tokenizer's exceptions,
 * (2) it provides diagnostics about errors using a custom error handler, (3) it tokenizes the input document on construction and caches
 * the results. There are two ways to retrieve the tokens: Via the {@link #tokens()} method which returns a regular immutable list of the
 * tokens, and via the iterator interface ({@link #hasNext()}, {@link #next()}. In order to reset the iterator to the start to re-read the
 * tokens, call {@link #close()}. The second method is implemented mainly to make the TurtleTokenizer usable with Apache Jena's
 * {@link org.apache.jena.riot.lang.LangEngine}, the base class for RDF parsers, and its derived classes such as
 * {@link org.apache.jena.riot.lang.LangTurtle}.
 */
public class TurtleTokenizer implements Tokenizer {
   private PeekIterator<Token> iterator;
   private List<SmartToken> tokens;
   private Token lastToken;

   public TurtleTokenizer( final InputStream stream, final ErrorHandler errorHandler ) {
      tokens = Collections.emptyList();
      try {
         final Iterator<Token> tokenizer = TokenizerText.create()
               .source( stream )
               .errorHandler( errorHandler ).build();
         tokens = Streams.stream( tokenizer ).map( SmartToken::new ).collect( Collectors.toList() );
         // TODO: Extract comments from source document and put them into tokens list. These are thrown away by TokenizerText, but
         // nobody stops us from extracting them ourselves and adding them; there is even a corresponding TokenType COMMENT.
      } catch ( final RiotParseException parseException ) {
         // If Jena deems the input unparsable and the error handling encounters a "fatal" state, it will throw.
         // At this point we will already have created the corresponding Diagnostic object.
      }
      iterator = PeekIterator.create( tokens.stream().map( SmartToken::token ).iterator() );
   }

   @Override
   public boolean hasNext() {
      return iterator.hasNext();
   }

   @Override
   public Token next() {
      lastToken = iterator.next();
      return lastToken;
   }

   @Override
   public Token peek() {
      return iterator.peek();
   }

   @Override
   public boolean eof() {
      return !iterator.hasNext();
   }

   @Override
   public long getLine() {
      return lastToken == null ? 0 : lastToken.getLine();
   }

   @Override
   public long getColumn() {
      return lastToken == null ? 0 : lastToken.getColumn();
   }

   /**
    * The close operation resets the stream, so it can be reiterated
    */
   @Override
   public void close() {
      lastToken = null;
      iterator = PeekIterator.create( tokens.stream().map( SmartToken::token ).iterator() );
   }

   public List<SmartToken> tokens() {
      return tokens;
   }
}
