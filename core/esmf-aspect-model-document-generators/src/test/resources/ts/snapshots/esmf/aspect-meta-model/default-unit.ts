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
import {QuantityKind} from './default-quantity-kind';
import {ModelVisitor} from '../visitor/model-visitor';

export interface Unit extends BaseMetaModelElement {
    symbol?: string;
    code?: string;
    name: string;
    referenceUnit?: string;
    conversionFactor?: string;
    quantityKinds?: Array<any>;
}

export class DefaultUnit extends Base implements Unit {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _symbol?: string,
        private _code?: string,
        private _referenceUnit?: string,
        private _conversionFactor?: string,
        private _quantityKinds: Array<QuantityKind> = []
    ) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public set symbol(value: string | undefined) {
        this._symbol = value;
    }

    public get symbol(): string | undefined {
        return this._symbol;
    }

    public get code(): string | undefined {
        return this._code;
    }

    public set code(value: string | undefined) {
        this._code = value;
    }

    public set referenceUnit(value: string | undefined) {
        this._referenceUnit = value;
    }

    public get referenceUnit(): string | undefined {
        return this._referenceUnit;
    }

    public set conversionFactor(value: string | undefined) {
        this._conversionFactor = value;
    }

    public get conversionFactor(): string | undefined {
        return this._conversionFactor;
    }

    public set quantityKinds(value: Array<QuantityKind>) {
        this._quantityKinds = value;
    }

    public get quantityKinds(): Array<QuantityKind> {
        return this._quantityKinds;
    }

    public accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitUnit(this, context);
    }
}
