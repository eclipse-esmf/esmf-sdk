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

import {Property} from './default-property';
import {Base, BaseMetaModelElement} from './base';
import {ModelVisitor} from '../visitor/model-visitor';

export interface Operation extends BaseMetaModelElement {
    input: Array<Property>;
    output?: Property;

    /**
     * Gets an input property by name
     *
     * @param name Name of the property
     * @return Property or undefined if no property with the name exists
     */
    getInputProperty(name: string): Property;
}

export class DefaultOperation extends Base implements Operation {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _input: Array<Property> = [],
        private _output?: Property
    ) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public getInputProperty(name: string): Property {
        if (!this._input) {
            return undefined;
        }
        return this._input.find(property => property.name === name);
    }

    public set input(value: Array<Property>) {
        this._input = value;
    }

    public get input(): Array<Property> {
        return this._input;
    }

    public set output(value: Property | undefined) {
        this._output = value;
    }

    public get output(): Property | undefined {
        return this._output;
    }

    public accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitOperation(this, context);
    }
}
