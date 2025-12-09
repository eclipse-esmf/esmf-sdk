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

import {Characteristic, DefaultCharacteristic} from './characteristic/default-characteristic';
import {Base, BaseMetaModelElement} from './base';
import {Type} from './type';
import {ModelVisitor} from '../visitor/model-visitor';
import {DefaultTrait} from './characteristic/default-trait';
import {Constraint} from './constraint/default-constraint';

export interface Property extends BaseMetaModelElement {
    characteristic: Characteristic;
    isAbstract: boolean;
    isNotInPayload: boolean;
    isOptional: boolean;
    exampleValue?: any;
    payloadName?: string;
    effectiveDataType: Type | undefined;
    extends?: Property | undefined;
    constraints: Array<Constraint>;
}

export class DefaultProperty extends Base implements Property {
    _characteristic: Characteristic;
    _isNotInPayload: boolean = false;
    _isOptional: boolean = false;
    _exampleValue?: any;
    _payloadName?: string;
    _isAbstract: boolean = false;
    _extends?: Property;
    constructor(
        metaModelVersion: string,
        aspectModelUrn: string,
        name: string,
        _characteristic: Characteristic,
        _isNotInPayload: boolean = false,
        _isOptional: boolean = false,
        _exampleValue?: any,
        _payloadName?: string,
        _isAbstract: boolean = false,
        _extends?: Property
    ) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    get extends(): Property {
        return this._extends;
    }

    set extends(value: Property) {
        this._extends = value;
    }

    set isAbstract(value: boolean) {
        this._isAbstract = value;
    }

    get isAbstract(): boolean {
        return this._isAbstract;
    }

    public set characteristic(value: Characteristic) {
        this._characteristic = value;
    }

    public get characteristic(): Characteristic {
        return this._characteristic;
    }

    public set isNotInPayload(value: boolean) {
        this._isNotInPayload = value;
    }

    public get isNotInPayload(): boolean {
        return this._isNotInPayload;
    }

    public set isOptional(value: boolean) {
        this._isOptional = value;
    }

    public get isOptional(): boolean {
        return this._isOptional;
    }

    public set exampleValue(value: any) {
        this._exampleValue = value;
    }

    public get exampleValue(): any {
        return this._exampleValue;
    }

    public set payloadName(value: string | undefined) {
        this._payloadName = value;
    }

    public get payloadName(): string | undefined {
        return this._payloadName;
    }

    public get effectiveDataType(): Type | undefined {
        return DefaultCharacteristic.getEffectiveDataType(this.characteristic);
    }

    public accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitProperty(this, context);
    }

    public get constraints(): Array<Constraint> {
        if (this.characteristic && this.characteristic instanceof DefaultTrait) {
            return this.characteristic.constraints;
        }
        return [];
    }
}

/**
 * Properties which are defined inline e.g. samm:properties ( [ samm:extends :abstractTestProperty ; samm:characteristic samm-c:Text ] ).
 */
export class DefaultPropertyInstanceDefinition implements Property {
    /**
     * Create a DefaultPropertyInstanceDefinition instance
     * @param _wrappedProperty the wrapped property
     * @param _isNotInPayload value of the property is not in the JSON payload
     * @param _isOptional value of the property is optional in the JSON payload
     * @param _payloadName custom key of the value in the JSON payload
     * @param _characteristic characteristic in case the property extends an abstract property
     * @param _extends abstract property which the current property extends
     */
    constructor(
        private _wrappedProperty: DefaultProperty,
        private _isNotInPayload: boolean,
        private _isOptional: boolean,
        private _payloadName: string,
        private _characteristic?: Characteristic,
        private _extends?: Property
    ) {}

    get extends(): Property {
        return this._extends;
    }

    get namespace(): string {
        if (this._wrappedProperty.isAnonymousNode) {
            return '';
        }
        return this._wrappedProperty.aspectModelUrn.split('#')[0] + '#';
    }

    set extends(value: Property) {
        this._extends = value;
    }

    set isAbstract(value: boolean) {
        this._wrappedProperty.isAbstract = value;
    }

    get isAbstract(): boolean {
        return this._wrappedProperty.isAbstract;
    }

    public get wrappedProperty(): DefaultProperty {
        return this._wrappedProperty;
    }

    public addParent(parent: BaseMetaModelElement) {
        this._wrappedProperty.addParent(parent);
    }

    public set characteristic(value: Characteristic) {
        if (this.extends && this.extends.isAbstract) {
            this._characteristic = value;
        } else {
            this._wrappedProperty.characteristic = value;
        }
    }

    public get characteristic(): Characteristic {
        if (this.extends && this.extends.isAbstract) {
            return this._characteristic;
        }
        return this._wrappedProperty.characteristic;
    }

    public set isNotInPayload(value: boolean) {
        this._isNotInPayload = value;
    }

    public get isNotInPayload(): boolean {
        return this._isNotInPayload || this._wrappedProperty.isNotInPayload;
    }

    public set isOptional(value: boolean) {
        this._isOptional = value;
    }

    public get isOptional(): boolean {
        return this._isOptional || this._wrappedProperty.isOptional;
    }

    public set exampleValue(value: any) {
        this._wrappedProperty.exampleValue = value;
    }

    public get exampleValue(): any {
        return this._wrappedProperty.exampleValue;
    }

    public set payloadName(value: string | undefined) {
        this._wrappedProperty.payloadName = value;
    }

    public get payloadName(): string | undefined {
        return this._wrappedProperty.payloadName;
    }

    public get parents(): Array<BaseMetaModelElement> {
        return this._wrappedProperty.parents;
    }

    public set metaModelVersion(value: string) {
        this._wrappedProperty.metaModelVersion = value;
    }

    public get metaModelVersion(): string {
        return this._wrappedProperty.metaModelVersion;
    }

    public set aspectModelUrn(value: string) {
        this._wrappedProperty.addAspectModelUrn = value;
    }

    public get aspectModelUrn(): string {
        return this._wrappedProperty.aspectModelUrn;
    }

    public set name(value: string) {
        this._wrappedProperty.name = value;
    }

    public get name(): string {
        return this._wrappedProperty.name;
    }

    public set isAnonymousNode(value: boolean) {
        this._wrappedProperty.isAnonymousNode = value;
    }

    public get isAnonymousNode(): boolean {
        return this._wrappedProperty.isAnonymousNode;
    }

    public get localesPreferredNames(): string[] {
        return this._wrappedProperty.localesPreferredNames;
    }

    public get seeReferences(): Array<string> {
        return this._wrappedProperty.seeReferences;
    }

    public get localesDescriptions(): Array<string> {
        return this._wrappedProperty.localesDescriptions;
    }

    public getAllLocalesPreferredNames(): Array<string> {
        return this._wrappedProperty.getAllLocalesPreferredNames();
    }

    public get effectiveDataType(): Type | undefined {
        if (this.extends && this.extends.isAbstract) {
            return DefaultCharacteristic.getEffectiveDataType(this.characteristic);
        } else {
            return this._wrappedProperty.effectiveDataType;
        }
    }

    public get constraints(): Array<Constraint> {
        return this._wrappedProperty.constraints;
    }

    accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitProperty(this, context);
    }

    addDescription(locale: string, description: string | undefined): void {
        this._wrappedProperty.addDescription(locale, description);
    }

    addPreferredName(locale: string, name: string | undefined): void {
        this._wrappedProperty.addPreferredName(locale, name);
    }

    addSeeReference(reference: string): void {
        this._wrappedProperty.addSeeReference(reference);
    }

    getDescription(locale: string): string | undefined {
        return this._wrappedProperty.getDescription(locale);
    }

    getPreferredName(locale: string): string | undefined {
        return this._wrappedProperty.getPreferredName(locale);
    }
}
