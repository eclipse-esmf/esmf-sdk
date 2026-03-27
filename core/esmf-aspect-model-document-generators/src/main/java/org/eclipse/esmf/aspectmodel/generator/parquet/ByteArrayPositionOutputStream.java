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

package org.eclipse.esmf.aspectmodel.generator.parquet;

import java.io.ByteArrayOutputStream;

import org.apache.parquet.io.PositionOutputStream;

/**
 * A {@link org.apache.parquet.io.PositionOutputStream} implementation that delegates all write
 * operations to an underlying {@link ByteArrayOutputStream}, while tracking the current stream
 * position as required by the Parquet API.
 *
 * <p>
 * Parquet's internal write path requires a {@code PositionOutputStream} — an output stream
 * that can report its current byte offset via {@link #getPos()}. This class satisfies that
 * contract by maintaining a {@code position} counter that is incremented on every write,
 * mirroring the number of bytes written to the underlying {@link ByteArrayOutputStream}.
 *
 * <p>
 * Instances of this class are created and returned by {@link ByteArrayOutputFile} and are
 * not intended to be instantiated directly by callers.
 */
public class ByteArrayPositionOutputStream extends PositionOutputStream {

   private final ByteArrayOutputStream byteArrayOutputStream;

   public ByteArrayPositionOutputStream( final ByteArrayOutputStream baos ) {
      byteArrayOutputStream = baos;
   }

   @Override
   public long getPos() {
      return byteArrayOutputStream.size();
   }

   @Override
   public void write( final int b ) {
      byteArrayOutputStream.write( b );
   }

   @Override
   public void write( final byte[] b, final int off, final int len ) {
      byteArrayOutputStream.write( b, off, len );
   }
}
