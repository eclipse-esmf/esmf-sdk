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

export class DefaultScalar implements Type {
    constructor(private _urn: string) {}

    public get urn(): string {
        return this._urn;
    }

    public get isComplex(): boolean {
        return false;
    }

    public get isScalar(): boolean {
        return true;
    }

    public get shortUrn(): string {
        return this.urn.split('#').pop();
    }

    public get isAbstract(): boolean {
        return false;
    }
}
