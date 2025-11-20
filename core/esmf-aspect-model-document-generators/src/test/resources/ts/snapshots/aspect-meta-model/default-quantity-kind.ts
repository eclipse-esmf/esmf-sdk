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

import {IsNamed} from './is-named';
import {Base} from './base';
import {ModelVisitor} from './visitor/model-visitor';

export interface QuantityKind extends IsNamed {
    label: string;
}

export class DefaultQuantityKind extends Base implements QuantityKind {
    constructor(metaModelVersion: string, aspectModelUrn: string, name: string, private _label: string) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public set label(value: string) {
        this._label = value;
    }

    public get label(): string {
        return this._label;
    }

    public accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitQuantityKind(this, context);
    }
}
