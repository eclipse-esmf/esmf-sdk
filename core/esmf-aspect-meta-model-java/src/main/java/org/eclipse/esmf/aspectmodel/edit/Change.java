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

package org.eclipse.esmf.aspectmodel.edit;

/**
 * This interface represents a single modification to an Aspect Model. Instances of Change should be applied to an Aspect Model
 * using {@link AspectChangeManager}; see the description about the change mechanism there. Instances of Change <b>must not</b>
 * perform file system operations; synching "adding", "removing" and "modifying" files with the file system is a separate step.
 */
public interface Change {
   /**
    * "Run" this change. This method should not be directly called on a Change object, but will be executed by the AspectChangeManager.
    * The passed {@link ChangeContext} provides information to the Change implementation about the current state of the AspectModel.
    *
    * @param changeContext the change context
    * @return the report describing what has been changed
    */
   ChangeReport fire( ChangeContext changeContext );

   /**
    * Returns a Change that is the reverse operation of this one, i.e., running this change and the reverse change after another
    * effectively cancels out any changes.
    *
    * @return the Change representing the reverse operation
    */
   Change reverse();
}
