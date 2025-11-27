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

import {Base, BaseMetaModelElement} from '../base';
import {ModelVisitor} from '../../visitor/model-visitor';

export type Constraint = BaseMetaModelElement;

export class DefaultConstraint extends Base implements Constraint {
    constructor(metaModelVersion: string, aspectModelUrn: string, name: string) {
        super(metaModelVersion, aspectModelUrn, name);
    }

    accept<T, U>(visitor: ModelVisitor<T, U>, context: U): T {
        return visitor.visitConstraint(this, context);
    }
}
