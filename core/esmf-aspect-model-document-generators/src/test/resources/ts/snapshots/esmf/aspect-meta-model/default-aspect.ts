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

import {Base, DefaultCollection, Event, Operation, Property} from './index';
import {BaseMetaModelElement} from './base';
import {HasProperties} from './has-properties';
import {ModelVisitor} from '../visitor/model-visitor';

export interface Aspect extends BaseMetaModelElement, HasProperties {
    operations: Array<Operation>;
    events: Array<Event>;
    isCollectionAspect?: boolean;
}

export class DefaultAspect extends Base implements Aspect {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _properties: Array<Property> = [],
        private _operations: Array<Operation> = [],
        private _events: Array<Event> = [],
        private _isCollectionAspect: boolean = false
    ) {
        super(metaModelVersion, aspectModelUrn, name);
        const collectionProperty = _properties.find(property => property.characteristic instanceof DefaultCollection);
        if (collectionProperty && collectionProperty.name === 'items') {
            this._isCollectionAspect = true;
        }
    }

    public getProperty(name: string): Property {
        if (!this._properties) {
            return undefined;
        }
        return this._properties.find(property => property.name === name);
    }

    public get properties(): Array<Property> {
        return this._properties;
    }

    public get operations(): Array<Operation> {
        return this._operations;
    }

    public get isCollectionAspect(): boolean {
        return this._isCollectionAspect;
    }

    get events(): Array<Event> {
        return this._events;
    }

    set events(value: Array<Event>) {
        this._events = value;
    }

    public accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitAspect(this, context);
    }
}
