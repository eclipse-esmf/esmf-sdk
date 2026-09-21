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

import {Base, BaseMetaModelElement} from './base';
import {Property} from './default-property';
import {ModelVisitor} from '../visitor/model-visitor';

export interface Event extends BaseMetaModelElement {
    parameters: Array<Property>;
}

export class DefaultEvent extends Base implements Event {
    constructor(metaModelVersion: string, aspectModelUrn: string, name: string, private _parameters: Array<Property> = []) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public get parameters(): Array<Property> {
        return this._parameters;
    }

    public set parameters(parameters: Array<Property>) {
        this._parameters = parameters;
    }

    public accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitEvent(this, context);
    }
}
