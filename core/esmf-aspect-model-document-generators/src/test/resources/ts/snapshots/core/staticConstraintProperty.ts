/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

export interface StaticMetaClass<T> {
    getModelClass(): string;

    getAspectModelUrn(): string;

    getMetaModelVersion(): string;

    getName(): string;

}

export interface PropertyContainer<C> {
    getProperties(): StaticProperty<C, any>[];

    getAllProperties(): StaticProperty<C, any>[];
}

export interface StaticProperty<C, T> {
    // getValue: (object: C) => T;
}

export abstract class AbstractStaticConstraintProperty<C, T> implements StaticProperty <C, T> {
    protected metaModelBaseAttributes: any;
    protected characteristic: any;
    protected exampleValue: any;
    protected optional: boolean;
    protected payloadName: string;
    protected notInPayload: boolean;
    protected isAbstract: boolean;

    constructor(params: {
        metaModelBaseAttributes: any;
        characteristic: any;
        exampleValue: any;
        optional: boolean;
        payloadName: string;
        notInPayload: boolean;
        isAbstract: boolean;
    }) {
        this.metaModelBaseAttributes = params.metaModelBaseAttributes;
        this.characteristic = params.characteristic;
        this.exampleValue = params.exampleValue;
        this.optional = params.optional;
        this.notInPayload = params.notInPayload;
        this.payloadName = params.payloadName;
        this.isAbstract = params.isAbstract;
    }

    abstract getPropertyType(): string;

    abstract getContainingType(): string;

}

export abstract class StaticContainerProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}

export abstract class StaticConstraintContainerProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}

export abstract class StaticConstraintUnitProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}

export abstract class StaticUnitProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}

export abstract class DefaultStaticProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}


export interface Constraint {
    metaModelBaseAttributes: {
        isAnonymous?: boolean;
        description: { value: string; languageTag: string };
    };
    maxValue?: number;
    value?: string;
}

export interface DefaultCode {
    metaModelBaseAttributes: {
        urn: string;
        description: { value: string; languageTag: string };
    };
    dataType: string;
}

export interface ProductHeaderInformation {
    id: string;
}

export interface Trait {
    get(): any;
}

export interface MetaModelBaseAttributes {
    urn: string;
    preferredName?: { value: string; languageTag: string };
    description: { value: string; languageTag: string };
}