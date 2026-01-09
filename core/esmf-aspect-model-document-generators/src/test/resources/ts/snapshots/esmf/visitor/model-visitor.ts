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

import {Aspect, Characteristic, Constraint, Entity, Event, Operation, Property, QuantityKind, Unit} from '../aspect-meta-model';

/**
 * Visitor interface to traverse a loaded model and apply operations to the
 * different defined concepts.
 *
 * @template T return type of the visitor.
 * @template U context object of the visitor.
 */
export interface ModelVisitor<T, U> {
    /** Visit a Property definition */
    visitProperty(property: Property, context: U): T;
    /** Visit an Aspect definition */
    visitAspect(aspect: Aspect, context: U): T;
    /** Visit a Operation definition */
    visitOperation(operation: Operation, context: U): T;
    /** Visit a Constraint definition */
    visitConstraint(constraint: Constraint, context: U): T;
    /** Visit a Characteristic definition */
    visitCharacteristic(characteristic: Characteristic, context: U): T;
    /** Visit a Unit definition */
    visitUnit(unit: Unit, context: U): T;
    /** Visit a QuantityKind definition */
    visitQuantityKind(quantityKind: QuantityKind, context: U): T;
    /** Visit an Entity definition */
    visitEntity(entity: Entity, context: U): T;
    /** Visit an Event definition */
    visitEvent(entity: Event, context: U): T;
}
