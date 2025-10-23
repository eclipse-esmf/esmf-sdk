package org.eclipse.esmf.aspectmodel;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base class for all Mojos that generate Java code.
 */
public abstract class JavaCodeGenerationMojo extends CodeGenerationMojo {
   @Parameter( defaultValue = "false" )
   protected boolean enableSetters;

   @Parameter( defaultValue = "standard" )
   protected String setterStyle;
}
