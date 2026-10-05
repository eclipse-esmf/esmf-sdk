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

import {Entity} from './default-entity';
import {IsInstance} from './is-instance';

export class DefaultEntityInstance implements IsInstance<Entity> {
    private _metaModelType: Entity;
    private _name: string;
    private _descriptions: Map<string, string>;

    constructor(name: string, metaModelType: Entity, description?: Map<string, string>) {
        this._name = name;
        this._metaModelType = metaModelType;
        this._descriptions = description || new Map();
    }

    public get metaModelType(): Entity {
        return this._metaModelType;
    }

    public get name(): string {
        return this._name;
    }

    public get descriptionKey(): string | undefined {
        return this._metaModelType.properties.find(
            property => property.name.toLowerCase().includes('description') && property.isNotInPayload === true
        ).name;
    }

    public get valuePayloadKey(): string {
        return this._metaModelType.properties.find(property => property.isNotInPayload === false).name;
    }

    public get value(): string | number | boolean {
        return this[this.valuePayloadKey];
    }

    public getDescription(locale = 'en'): string | undefined {
        return this._descriptions.get(locale);
    }

    public get localesDescriptions(): Array<string> {
        return Array.from(this._descriptions.keys());
    }
}
