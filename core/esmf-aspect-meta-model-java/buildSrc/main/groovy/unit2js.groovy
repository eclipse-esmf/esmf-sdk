/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.RDFLanguages

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

def generateJs(List quantityKinds, List units) {
    def joinedQuantityKinds = quantityKinds.join(",\n        ")
    def joinedUnits = units.join(",\n        ")
    return """\
/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

var samm = {
    quantityKinds: {
        ${joinedQuantityKinds}
    },

    units: {
        ${joinedUnits}
    }
};
"""
}

def model = ModelFactory.createDefaultModel()
model.read(getClass().getResourceAsStream("samm/unit/1.0.0/units.ttl"), "", RDFLanguages.TURTLE.getName())

def quantityKinds = []
QueryExecutionFactory.create(new File("${project.basedir}/buildSrc/main/resources/quantitykinds.sparql").text, model).execSelect().each { unit ->
    def quantityKindName = unit.get("quantityKindName").asLiteral().toString()
    def label = unit.get("label").asLiteral().toString()
    quantityKinds.add(""""${quantityKindName}": { name: "${quantityKindName}", label: "${label}" }""")
}

def units = []
QueryExecutionFactory.create(new File("${project.basedir}/buildSrc/main/resources/units.sparql").text, model).execSelect().each { unit ->
    def unitName = unit.get("unitName").asLiteral().toString()
    def label = unit.get("label").asLiteral().toString()
    def symbol = unit.get("symbol") == null ? "null" : "\"" + unit.get("symbol") + "\""
    def code = unit.get("code") == null ? "null" : "\"" + unit.get("code") + "\""
    def referenceUnitName = unit.get("referenceUnitName") == null ? "null" :
            "function() { return samm.units[\"" + unit.get("referenceUnitName").asLiteral().toString() + "\"] }"
    def conversionFactor = unit.get("conversionFactor") == null ? "null" :
            "\"" + unit.get("conversionFactor").asLiteral().toString() + "\""
    def quantityKindsString = unit.get("quantityKinds") == null ? "null" :
            "[ " + unit.get("quantityKinds").asLiteral().toString().split(",")
                    .collect { quantityKind -> "\"" + quantityKind + "\"" }.join(", ") + " ]"

    units.add(""""${unitName}": { name: "${unitName}", label: "${label}", symbol: ${symbol}, code: ${code}, """
            + """referenceUnit: ${referenceUnitName}, conversionFactor: ${conversionFactor}, """
            + """quantityKinds: ${quantityKindsString} }""")
}

def path = "${project.basedir}/src-gen/main/resources"
new File(path).mkdirs()
System.out.println("Writing generated source file: " + path + "/samm-units.js")
Paths.get(path).resolve("samm-units.js")
        .toFile()
        .newWriter(StandardCharsets.UTF_8.name())
        .withWriter { w -> w << generateJs(quantityKinds, units) }
