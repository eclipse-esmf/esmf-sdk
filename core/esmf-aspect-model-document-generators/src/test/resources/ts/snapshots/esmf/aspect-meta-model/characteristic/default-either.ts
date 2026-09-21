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

import {Characteristic, DefaultCharacteristic} from './default-characteristic';
import {Type} from '../type';

export interface Either extends Characteristic {
    left: Characteristic;
    right: Characteristic;
    /**
     * Get the effective type for the left characteristic.
     */
    effectiveLeftDataType: Type | undefined;
    /**
     * Get the effective type for the right characteristic.
     */
    effectiveRightDataType: Type | undefined;
}

export class DefaultEither extends DefaultCharacteristic implements Either {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _left: Characteristic,
        private _right: Characteristic
    ) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public set left(value: Characteristic) {
        this._left = value;
    }

    public get left(): Characteristic {
        return this._left;
    }

    public set right(value: Characteristic) {
        this._right = value;
    }

    public get right(): Characteristic {
        return this._right;
    }

    public get effectiveLeftDataType(): Type | undefined {
        return DefaultCharacteristic.getEffectiveDataType(this._left);
    }

    public get effectiveRightDataType(): Type | undefined {
        return DefaultCharacteristic.getEffectiveDataType(this._right);
    }
}
