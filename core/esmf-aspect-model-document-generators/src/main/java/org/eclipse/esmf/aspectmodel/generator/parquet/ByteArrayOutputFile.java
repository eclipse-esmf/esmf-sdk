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

import org.apache.parquet.io.OutputFile;
import org.apache.parquet.io.PositionOutputStream;

/**
 * An implementation of Parquet's {@link org.apache.parquet.io.OutputFile} that writes entirely
 * in-memory using a {@link ByteArrayOutputStream} .
 *
 * <p>
 * This class serves as an adapter between Parquet's {@code OutputFile} API — which is normally
 * backed by a filesystem path (e.g. via {@code LocalOutputFile}) — and an in-memory byte buffer.
 * It is intended to be passed directly to {@code ExampleParquetWriter.builder(OutputFile)} in
 * place of a file-based {@code OutputFile}.
 *
 */
public class ByteArrayOutputFile implements OutputFile {

   private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

   @Override
   public PositionOutputStream create( final long blockSizeHint ) {
      return new ByteArrayPositionOutputStream( byteArrayOutputStream );
   }

   @Override
   public PositionOutputStream createOrOverwrite( final long blockSizeHint ) {
      return new ByteArrayPositionOutputStream( byteArrayOutputStream );
   }

   @Override
   public boolean supportsBlockSize() {
      return false;
   }

   @Override
   public long defaultBlockSize() {
      return 0;
   }

   public byte[] toByteArray() {
      return byteArrayOutputStream.toByteArray();
   }
}
