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
 * @param $this the focus node
 * @param $value the value
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

/**
 * Test function for accessing TermFactory, data graph and returning message objects
 */
function testTermFactoryAndMessageResult($this) {
    var ex = "http://example.com#";
    var testProperty = TermFactory.namedNode(ex + "testProperty");
    var iterator = $data.find($this, testProperty, null);
    var object = iterator.next().object;
    if (object == null || !object.isLiteral()) {
        return null;
    }
    var theValue = object.lex;
    if (theValue != "secret valid value") {
        return { message: "Invalid value: {$value} on {$property}", value: theValue, property: testProperty };
    }
    return true;
}
