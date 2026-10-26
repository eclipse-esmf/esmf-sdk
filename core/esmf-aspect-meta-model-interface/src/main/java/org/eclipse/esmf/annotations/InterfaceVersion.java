/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the version of an extension point interface. Implementations must be annotated with
 * {@link RequiredInterfaceVersion} to indicate which version they were built against.
 * The two versions are compared to detect incompatible implementations at load time.
 */
@Target( { ElementType.TYPE } )
@Retention( RetentionPolicy.RUNTIME )
public @interface InterfaceVersion {
   int version();
}
