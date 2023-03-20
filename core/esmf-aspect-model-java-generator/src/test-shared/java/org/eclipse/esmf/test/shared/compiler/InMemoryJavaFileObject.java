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

package org.eclipse.esmf.test.shared.compiler;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Base class for {@link javax.tools.JavaFileObject}s that are backed by a buffer.
 */
public abstract class InMemoryJavaFileObject extends SimpleJavaFileObject {
   protected InMemoryJavaFileObject( final String name, final Kind kind ) {
      super( URI.create( String.format( "string:///%s%s", name.replace( '.', '/' ), kind.extension ) ),
            kind );
   }
}
