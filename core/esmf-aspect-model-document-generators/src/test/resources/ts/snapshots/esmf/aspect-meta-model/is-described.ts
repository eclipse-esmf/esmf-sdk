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

export interface IsDescribed {
    getPreferredName(locale: string): string | undefined;

    addPreferredName(locale: string, name: string | undefined): void;

    getDescription(locale: string): string | undefined;

    addDescription(locale: string, description: string | undefined): void;

    localesPreferredNames: Array<string>;

    localesDescriptions: Array<string>;

    seeReferences: Array<string>;

    addSeeReference(reference: string): void;
}
