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

package org.eclipse.esmf;

import java.net.URI;
import java.util.NoSuchElementException;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

/**
 * An InputHandler knows how to load Aspect Models, Aspects etc. depending on a certain type of given input.
 */
public interface InputHandler {
   AspectModel loadAspectModel();

   /**
    * Loads the single Aspect given in the input if there is one, otherwise will return a {@link NoSuchElementException}.
    *
    * @return the Aspect
    */
   Aspect loadAspect();

   /**
    * Loads the specific file given by the input
    *
    * @return the AspectModelFile
    */
   AspectModelFile loadAspectModelFile();

   /**
    * Returns the AspectModelLoader initialized with the {@link ResolutionStrategy}s according to the input
    *
    * @return the AspectModelLoader
    */
   AspectModelLoader aspectModelLoader();

   /**
    * Returns the canonical URI representation for the input source
    *
    * @return the URI representing the input location
    */
   URI inputUri();
}
