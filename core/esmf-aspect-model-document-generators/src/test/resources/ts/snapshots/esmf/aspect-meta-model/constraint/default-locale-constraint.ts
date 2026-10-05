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

export interface LocaleConstraint extends Constraint {
    localeCode: string;
}

export class DefaultLocaleConstraint extends DefaultConstraint implements LocaleConstraint {
    constructor(metaModelVersion: string, aspectModelUrn: string, name: string, private _localeCode: string) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public set localeCode(value: string) {
        this._localeCode = value;
    }

    public get localeCode(): string {
        return this._localeCode;
    }
}
