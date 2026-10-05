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

export interface RegularExpressionConstraint extends Constraint {
    value: string;
}

export class DefaultRegularExpressionConstraint extends DefaultConstraint implements RegularExpressionConstraint {
    constructor(metaModelVersion: string, aspectModelUrn: string, name: string, private _value: string) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public set value(value: string) {
        this._value = value;
    }

    public get value(): string {
        return this._value;
    }
}
