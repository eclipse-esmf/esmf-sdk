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
import {Property} from '../default-property';

export interface StructuredValue extends Characteristic {
    deconstructionRule: string;
    elements: Array<string | Property>;
}

export class DefaultStructuredValue extends DefaultCharacteristic implements StructuredValue {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _deconstructionRule: string,
        private _elements: Array<string | Property>,
        dataType?: Type
    ) {
        super(metaModelVersion, aspectModelUrn, name, dataType);
    }

    public set deconstructionRule(value: string) {
        this._deconstructionRule = value;
    }

    public get deconstructionRule(): string {
        return this._deconstructionRule;
    }

    public set elements(value: Array<string | Property>) {
        this._elements = value;
    }

    public get elements(): Array<string | Property> {
        return this._elements;
    }
}
