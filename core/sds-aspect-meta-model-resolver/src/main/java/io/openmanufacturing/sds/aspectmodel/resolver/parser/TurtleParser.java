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

package io.openmanufacturing.sds.aspectmodel.resolver.parser;

import org.apache.jena.riot.lang.LangTurtle;
import org.apache.jena.riot.system.ParserProfile;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.tokens.Tokenizer;

public class TurtleParser extends LangTurtle {

   private TurtleParser( final Tokenizer tokenizer, final ParserProfile parserProfile, final StreamRDF output ) {
      super( tokenizer, parserProfile, output );
   }

   public static TurtleParser create( final Tokenizer tokenizer, final ParserProfile parserProfile, final StreamRDF output ) {
      return new TurtleParser( tokenizer, parserProfile, output );
   }
}
