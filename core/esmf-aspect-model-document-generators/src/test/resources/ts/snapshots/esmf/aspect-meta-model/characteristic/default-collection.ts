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

import {Type} from '../type';
import {Characteristic, DefaultCharacteristic} from './default-characteristic';

export interface Collection extends Characteristic {
    isAllowDuplicates: boolean;
    isOrdered: boolean;
    elementCharacteristic?: Characteristic;
}

export class DefaultCollection extends DefaultCharacteristic implements Collection {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _isAllowDuplicates: boolean,
        private _isOrdered: boolean,
        private _elementCharacteristic?: Characteristic,
        dataType?: Type
    ) {
        super(metaModelVersion, aspectModelUrn, name, dataType);
    }

    public set isAllowDuplicates(value: boolean) {
        this._isAllowDuplicates = value;
    }

    public get isAllowDuplicates(): boolean {
        return this._isAllowDuplicates;
    }

    public set isOrdered(value: boolean) {
        this._isOrdered = value;
    }

    public get isOrdered(): boolean {
        return this._isOrdered;
    }

    public set elementCharacteristic(value: Characteristic | undefined) {
        this._elementCharacteristic = value;
    }

    public get elementCharacteristic(): Characteristic | undefined {
        return this._elementCharacteristic;
    }
}
