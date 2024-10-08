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

package org.eclipse.esmf.aspectmodel.resolver.parser;

import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;

import com.google.common.collect.MapMaker;
import org.apache.jena.graph.Node;

/**
 * This map keeps track of location information for nodes, i.e., when an RDF document is parsed using {@link TurtleParserProfile}
 * (i.e., also when using {@link TurtleLoader}), the TokenRegistry will know about line/column/token information for each RDF node
 * in the document.
 */
public class TokenRegistry {
   /**
    * The map that holds the node->token relations. It's important that this map shares the properties of both an
    * IdentityHashMap (key identity must be determined using == instead of equals(), because Jena's Node_URI will be equal
    * to another if the URI matches, but here we need to distinguish between their actual occurences) and a
    * WeakHashMap (because we'd cause a memory leak if we keep token information around once a node is GC'ed).
    * For this reason, Guava MapMaker with weakKeys() is used for the map implementation, is it defaults to object
    * identity for comparison.
    */
   private final static Map<Node, SmartToken> tokens = new MapMaker().weakKeys().makeMap();

   public static void put( final Node node, final SmartToken token ) {
      tokens.put( node, token );
   }

   public static Optional<SmartToken> getToken( final Node node ) {
      return Optional.ofNullable( tokens.get( node ) );
   }
}
