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
import {IsVersioned} from './is-versioned';
import {IsDescribed} from './is-described';
import {ModelVisitor} from './visitor/model-visitor';

export interface BaseMetaModelElement extends IsNamed, IsVersioned, IsDescribed {
    accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T;

    parents: Array<BaseMetaModelElement>;
}

export abstract class Base implements BaseMetaModelElement {
    private _preferredNames: Map<string, string> = new Map();
    private _descriptions: Map<string, string> = new Map();
    private _see: Array<string> = [];

    private _parents: Array<BaseMetaModelElement> = [];

    protected constructor(
        private _metaModelVersion: string,
        private _aspectModelUrn: string,
        private _name: string,
        private _isAnonymousNode: boolean = false
    ) {}

    public get namespace(): string {
        if (this._isAnonymousNode) {
            return '';
        }
        return this._aspectModelUrn.split('#')[0] + '#';
    }

    public get parents(): Array<BaseMetaModelElement> {
        return this._parents;
    }

    public addParent(parent: BaseMetaModelElement) {
        if (this._parents.find(baseElement => baseElement.name === parent.name)) {
            return;
        }
        this._parents.push(parent);
    }

    public set metaModelVersion(value: string) {
        this._metaModelVersion = value;
    }

    public get metaModelVersion(): string {
        return this._metaModelVersion;
    }

    public set addAspectModelUrn(value: string) {
        if (value) {
            this.name = value.split('#')[1];
        } else {
            this.name = null;
        }
        this._aspectModelUrn = value;
    }

    public get aspectModelUrn(): string {
        return this._aspectModelUrn;
    }

    public set name(value: string) {
        this._name = value;
    }

    public get name(): string {
        return this._name;
    }

    public set isAnonymousNode(value: boolean) {
        this._isAnonymousNode = value;
    }

    public get isAnonymousNode() {
        return this._isAnonymousNode;
    }

    public addPreferredName(locale: string, name: string | undefined): void {
        this._preferredNames.set(locale, name);
    }

    public addDescription(locale: string, description: string | undefined): void {
        this._descriptions.set(locale, description);
    }

    public get localesPreferredNames(): string[] {
        return [...this._preferredNames.keys()];
    }

    public get seeReferences(): Array<string> {
        return [...this._see];
    }

    public addSeeReference(reference: string): void {
        this._see.push(reference);
    }

    public getDescription(locale = 'en'): string | undefined {
        return this._descriptions.get(locale);
    }

    public getPreferredName(locale = 'en'): string | undefined {
        return this._preferredNames.get(locale);
    }

    public get localesDescriptions(): Array<string> {
        return Array.from(this._descriptions.keys());
    }

    public getAllLocalesPreferredNames(): Array<string> {
        return Array.from(this._preferredNames.keys());
    }

    public getSeeReferences(): Array<string> {
        return this._see;
    }

    abstract accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T;
}
