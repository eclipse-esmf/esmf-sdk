package io.openmanufacturing.sds;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * We want to be flexible and be able to output our results both to console and to a file. Some code parts we depend on to produce the results
 * use the Java try-with-resources statement to handle the output, which results in indiscriminate closing of the output stream passed as an argument.
 * This is normally not a problem with file output (actually a desired behavior), but one should not close system streams which were not opened
 * by us, as is for example the case with System.out.
 * This facade class serves the purpose of wrapping these "special" system streams to prevent them from being closed by this kind of stream handling.
 */
public class ProtectedOutputStream extends FilterOutputStream {

   public ProtectedOutputStream( final OutputStream out ) {
      super( out );
   }

   @Override
   public void close() throws IOException {
      // intentionally not closing the stream
      out.flush();
   }
}