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

export interface FixedPointConstraint extends Constraint {
    scale: number;
    integer: number;
}

export class DefaultFixedPointConstraint extends DefaultConstraint implements FixedPointConstraint {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _scale: number,
        private _integer: number,
    ) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public set scale(value: number) {
        this._scale = value;
    }

    public get scale(): number {
        return this._scale;
    }

    public set integer(value: number) {
        this._integer = value;
    }

    public get integer(): number {
        return this._integer;
    }
}
