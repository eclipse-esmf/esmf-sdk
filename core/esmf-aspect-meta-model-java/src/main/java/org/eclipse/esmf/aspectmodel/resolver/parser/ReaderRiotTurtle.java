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
import java.io.Reader;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.ReaderRIOTFactory;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.riot.tokens.TokenizerText;
import org.apache.jena.sparql.util.Context;

import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

public class ReaderRiotTurtle implements ReaderRIOT {
   public static ReaderRIOTFactory factory = ReaderRiotTurtle::new;
   private final ParserProfile parserProfile;

   ReaderRiotTurtle( final Lang lang, final ParserProfile parserProfile ) {
      this.parserProfile = parserProfile;
   }

   @Override
   public void read( final InputStream in, final String baseUri, final ContentType ct, final StreamRDF output, final Context context ) {
      final TurtleSyntaxTree syntaxTree = context != null && context.get( TurtleLoader.TREE_SITTER_SYNTAX_TREE ) != null
            ? context.get( TurtleLoader.TREE_SITTER_SYNTAX_TREE )
            : null;
      final ParserProfile wrappedParserProfile = new TurtleParserProfile( parserProfile, syntaxTree );
      final TurtleTokenizer tokenizer = new TurtleTokenizer( in, wrappedParserProfile.getErrorHandler() );
      final TurtleParser parser = TurtleParser.create( tokenizer, wrappedParserProfile, output );
      parser.parse();
   }

   @Override
   public void read( final Reader in, final String baseUri, final ContentType ct, final StreamRDF output, final Context context ) {
      final TurtleSyntaxTree syntaxTree = context != null && context.get( TurtleLoader.TREE_SITTER_SYNTAX_TREE ) != null
            ? context.get( TurtleLoader.TREE_SITTER_SYNTAX_TREE )
            : null;
      final ParserProfile wrappedParserProfile = new TurtleParserProfile( parserProfile, syntaxTree );
      final Tokenizer tokenizer = TokenizerText.create().source( in ).errorHandler( wrappedParserProfile.getErrorHandler() ).build();
      final TurtleParser parser = TurtleParser.create( tokenizer, wrappedParserProfile, output );
      parser.parse();
   }
}
