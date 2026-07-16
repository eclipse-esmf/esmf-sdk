/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.lsp;

import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.collections4.IteratorUtils;

public class LspUtil {
   private LspUtil() {}

   public static <T> List<T> loadServicesForInterface( final Class<T> serviceInterface ) {
      return IteratorUtils.toList( ServiceLoader.load( serviceInterface ).iterator() );
   }
}
