/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

/**
 * Checks if a given value is a regular expression.
 *
 * @param $this The context of the Constraint, i.e. the focus node
 * @param $value The value that can be reached from the focus node following the Shape's sh:path
 */
function isRegularExpression($this, $value) {
    if (!$value.isLiteral()) {
        return false;
    }

    var isValid = true;
    try {
        new RegExp($value.lex);
    } catch (e) {
        isValid = false;
    }

    return isValid;
}
