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
import {BoundDefinition} from '../bound-definition';

export interface RangeConstraint extends Constraint {
    minValue?: number;
    maxValue?: number;
    upperBoundDefinition?: BoundDefinition;
    lowerBoundDefinition?: BoundDefinition;
}

export class DefaultRangeConstraint extends DefaultConstraint implements RangeConstraint {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _upperBoundDefinition: BoundDefinition,
        private _lowerBoundDefinition: BoundDefinition,
        private _minValue?: any,
        private _maxValue?: any
    ) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public set upperBoundDefinition(value: BoundDefinition) {
        this._upperBoundDefinition = value;
    }

    public get upperBoundDefinition(): BoundDefinition {
        return this._upperBoundDefinition;
    }

    public set lowerBoundDefinition(value: BoundDefinition) {
        this._lowerBoundDefinition = value;
    }

    public get lowerBoundDefinition(): BoundDefinition {
        return this._lowerBoundDefinition;
    }

    public set minValue(value: any) {
        this._minValue = value;
    }

    public get minValue(): any {
        return this._minValue;
    }

    public set maxValue(value: any) {
        this._maxValue = value;
    }

    public get maxValue(): any {
        return this._maxValue;
    }
}
