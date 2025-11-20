/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for
 * additional information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

import {DefaultCollection} from './default-collection';
import {Characteristic} from './default-characteristic';
import {Type} from '../type';

export class DefaultList extends DefaultCollection {
    constructor(metaModelVersion: string, aspectModelUrn: string, name: string, elementCharacteristic?: Characteristic, dataType?: Type) {
        super(metaModelVersion, aspectModelUrn, name, true, true, elementCharacteristic, dataType);
    }
}
