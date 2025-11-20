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

import { DefaultProperty, } from '../aspect-meta-model';

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

export class StaticProperty<C, T> extends DefaultProperty {
    // getValue: (object: C) => T;
}

export abstract class AbstractStaticConstraintProperty<C, T> extends StaticProperty <C, T> {

    abstract getPropertyType(): string;

    abstract getContainingType(): string;
}

export abstract class StaticContainerProperty<C, E, T> extends AbstractStaticConstraintProperty<C, T> {
}

export abstract class StaticConstraintContainerProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}

export abstract class StaticConstraintUnitProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}

export abstract class StaticUnitProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}

export abstract class DefaultStaticProperty<C, T> extends AbstractStaticConstraintProperty<C, T> {
}


