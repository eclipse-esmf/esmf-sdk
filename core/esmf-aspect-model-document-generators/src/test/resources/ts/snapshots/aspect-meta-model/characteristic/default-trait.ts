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
import {Constraint} from '../constraint/default-constraint';
import {ModelVisitor} from '../visitor/model-visitor';

export interface Trait extends Characteristic {
    baseCharacteristic?: Characteristic;
    constraints?: Array<Constraint>;
}

export class DefaultTrait extends DefaultCharacteristic implements Trait {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _baseCharacteristic?: Characteristic,
        private _constraints?: Array<Constraint>
    ) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public get baseCharacteristic(): Characteristic {
        return this._baseCharacteristic;
    }

    public set baseCharacteristic(baseCharacteristic: Characteristic) {
        this._baseCharacteristic = baseCharacteristic;
    }

    public get constraints(): Array<Constraint> {
        return this._constraints;
    }

    accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitCharacteristic(this, context);
    }
}
