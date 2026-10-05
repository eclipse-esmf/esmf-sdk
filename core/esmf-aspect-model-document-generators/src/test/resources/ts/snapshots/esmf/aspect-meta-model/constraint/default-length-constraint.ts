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

import {Constraint, DefaultConstraint} from './default-constraint';

export interface LengthConstraint extends Constraint {
    minValue?: number;
    maxValue?: number;
}

export class DefaultLengthConstraint extends DefaultConstraint implements LengthConstraint {
    constructor(metaModelVersion: string, aspectModelUrn: string, name: string, private _minValue?: number, private _maxValue?: number) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public set minValue(value: number | undefined) {
        this._minValue = value;
    }

    public get minValue(): number | undefined {
        return this._minValue;
    }

    public set maxValue(value: number | undefined) {
        this._maxValue = value;
    }

    public get maxValue(): number | undefined {
        return this._maxValue;
    }
}
