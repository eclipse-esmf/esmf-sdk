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
import {HasProperties} from './has-properties';
import {Property} from './default-property';
import {Type} from './type';
import {ModelVisitor} from '../visitor/model-visitor';

export interface Entity extends BaseMetaModelElement, HasProperties, Type {
    /**
     * Gets the properties of the entity excluding extended once.
     */
    getOwnProperties(): Array<Property>;
}

export class DefaultEntity extends Base implements Entity {
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        private _properties: Array<Property> = [],
        private _isAbstract: boolean = false,
        private _extends?: Entity
    ) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    public getProperty(name: string): Property {
        if (!this._properties) {
            return undefined;
        }
        return this.properties.find(property => property.name === name);
    }

    public getOwnProperties(): Array<Property> {
        return this._properties;
    }

    public set properties(value: Array<Property>) {
        this._properties = value;
    }

    public get properties(): Array<Property> {
        const propertyUrns = [];
        return [...this._properties, ...this.resolveAllExtendedProperties(this, [])].filter(property => {
            if (!propertyUrns.includes(property.aspectModelUrn)) {
                propertyUrns.push(property.aspectModelUrn);
                return true;
            }
            return false;
        });
    }

    private resolveAllExtendedProperties(entity: Entity, properties: Array<Property>): Array<Property> {
        if (entity && (entity as DefaultEntity).extends) {
            properties.push(...((entity as DefaultEntity).extends.properties || []));
            this.resolveAllExtendedProperties((entity as DefaultEntity).extends, properties);
        }

        return properties;
    }

    public get seeReferences(): Array<string> {
        const see = super.seeReferences;
        if (this.extends) {
            see.push(...this.extends.seeReferences);
        }
        return see;
    }

    public getDescription(locale = 'en'): string | undefined {
        let description = super.getDescription(locale);
        if (!description && this.extends) {
            description = this.extends.getDescription(locale);
        }
        return description;
    }

    public getPreferredName(locale = 'en'): string | undefined {
        let preferredName = super.getPreferredName(locale);
        if (!preferredName && this.extends) {
            preferredName = this.extends.getPreferredName(locale);
        }
        return preferredName;
    }

    public get isComplex(): boolean {
        return true;
    }

    public get isScalar(): boolean {
        return false;
    }

    public get isAbstract(): boolean {
        return this._isAbstract;
    }

    public get urn(): string {
        return this.aspectModelUrn;
    }

    public get shortUrn(): string {
        return this.urn.split('#').pop();
    }

    public get extends(): Entity | undefined {
        return this._extends as Entity;
    }

    public set extends(entity: Entity | undefined) {
        if (!entity.isAbstract) {
            throw Error(`Entity ${entity.urn} is not abstract.`);
        }
        this._extends = entity;
    }

    public accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitEntity(this, context);
    }
}
