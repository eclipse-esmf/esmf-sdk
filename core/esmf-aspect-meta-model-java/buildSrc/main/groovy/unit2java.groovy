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


import com.google.common.collect.Lists
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.RDFLanguages

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

def generateUnitsClass(List<String> units) {
    List<List<String>> slices = Lists.partition(units, 100)
    int numSlices = slices.size()
    def initMethods = []
    def initMethodCalls = []
    for (int slice = 0; slice < numSlices; slice++) {
        String method = """
   private static void init${slice}() {
      ${slices[slice].join("\n   ")}
   }
"""
        initMethods << method
        initMethodCalls << "init${slice}();"
    }

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

package org.eclipse.esmf.metamodel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.impl.DefaultUnit;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;

/**
 * Enumeration of Units as defined in <a href="http://tfig.unece.org/contents/recommendation-20.htm">Recommendation 20</a> by the
 * <a href="http://www.unece.org/info/ece-homepage.html">UNECE</a> (United Nations Economic Commission for Europe).
 */
public class Units {
   private static final Map<String, Function<KnownVersion, Unit>> UNITS_BY_NAME = new HashMap<>();
   private static final KnownVersion LATEST = KnownVersion.getLatest();

   private Units() {
   }

   private static AspectModelUrn urn( final KnownVersion version, final String name ) {
      return AspectModelUrn.fromUrn(
            String.format( "urn:samm:org.eclipse.esmf.samm:unit:%s#%s", version.toVersionString(), name ) );
   }

   ${initMethods.join("\n")}

   /**
    * Returns the unit with a given name
    */
   public static synchronized Optional<Unit> fromName( final String name, final KnownVersion metaModelVersion ) {
      if ( UNITS_BY_NAME.isEmpty() ) {
         ${initMethodCalls.join("\n         ")}
      }
      return Optional.ofNullable( UNITS_BY_NAME.get( name ) ).map( unitGenerator -> unitGenerator.apply( metaModelVersion ) );
   }

   /**
    * Returns the unit with a given name
    */
   public static Optional<Unit> fromName( final String name ) {
      return fromName( name, LATEST );
   }

   /**
    * Finds the unit with a given code
    */
   public static Optional<Unit> fromCode( final String code, final KnownVersion metaModelVersion ) {
      if ( UNITS_BY_NAME.isEmpty() ) {
         fromName( "" );
      }
      return UNITS_BY_NAME.values()
                  .stream()
                  .map( unitGenerator -> unitGenerator.apply( metaModelVersion ) )
                  .filter( unit -> unit.getCode().map( code2 -> code2.equals( code ) ).orElse( false ) )
                  .findAny();
   }

   /**
    * Finds the unit with a given code
    */
   public static Optional<Unit> fromCode( final String code ) {
      return fromCode( code, LATEST );
   }

   /**
    * Finds the units with a given symbol. In most, but not all cases, this set will contain exactly one unit.
    */
   public static Set<Unit> fromSymbol( final String symbol, final KnownVersion metaModelVersion ) {
      if ( UNITS_BY_NAME.isEmpty() ) {
         fromName( "" );
      }
      return UNITS_BY_NAME.values()
                  .stream()
                  .map( unitGenerator -> unitGenerator.apply( metaModelVersion ) )
                  .flatMap( unit -> unit.getSymbol()
                                        .filter( symbol2 -> symbol2.equals( symbol ) )
                                        .stream()
                                        .map( symbol2 -> unit ) )
                  .collect( Collectors.toSet() );
   }

   /**
    * Finds the units with a given symbol. In most, but not all cases, this set will contain exactly one unit.
    */
   public static Set<Unit> fromSymbol( final String symbol ) {
      return fromSymbol( symbol, LATEST );
   }

   /**
    * Returns all units of a given quantity kind
    */
   public static Set<Unit> unitsWithQuantityKind( final QuantityKind quantityKind,
         final KnownVersion metaModelVersion ) {
      if ( UNITS_BY_NAME.isEmpty() ) {
         fromName( "" );
      }
      return UNITS_BY_NAME.values()
                  .stream()
                  .map( unitGenerator -> unitGenerator.apply( metaModelVersion ) )
                  .filter( unit -> unit.getQuantityKinds().contains( quantityKind ) )
                  .collect( Collectors.toSet() );
   }

   /**
    * Returns all units of a given quantity kind
    */
   public static Set<Unit> unitsWithQuantityKind( final QuantityKind quantityKind ) {
      return unitsWithQuantityKind( quantityKind, LATEST );
   }

}
"""
}

def generateQuantityKindClass(List quantityKinds) {
    def joinedQuantityKinds = quantityKinds.join(",\n   ")
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

package org.eclipse.esmf.metamodel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.Optional;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

/**
 * Enumeration of Quantity Kinds as defined in <a href="http://tfig.unece.org/contents/recommendation-20.htm">Recommendation 20</a>
 * by the <a href="http://www.unece.org/info/ece-homepage.html">UNECE</a> (United Nations Economic Commission for Europe).
 */
public enum QuantityKinds implements QuantityKind {
   ${joinedQuantityKinds};

   private static final KnownVersion LATEST = KnownVersion.getLatest();
   private final String name;
   private final String label;

   QuantityKinds( final String name, final String label ) {
      this.name = name;
      this.label = label;
   }

   /**
    * Returns the quantity kind's unique name
    */
   public String getName() {
      return this.name;
   }

   /**
    * Returns the quantity kind's human-readable name
    */
   public String getLabel() {
      return this.label;
   }

   /**
    * Returns the quantity kind's human-readable name
    */
   @Override
   public String toString() {
      return getLabel();
   }

   @Override
   public Optional<AspectModelUrn> getAspectModelUrn() {
      return Optional.of( AspectModelUrn.fromUrn( String.format( "urn:samm:org.eclipse.esmf.samm:unit:%s#%s", LATEST.toVersionString(), this.name ) ) );
   }

   @Override
   public KnownVersion getMetaModelVersion() {
      return LATEST;
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, C context ) {
      return visitor.visitQuantityKind( this, context );
   }

   /**
    * Finds the quantity kind with a given name
    */
   public static Optional<QuantityKind> fromName( final String name ) {
      return Arrays.stream( QuantityKinds.values() )
                   .filter( quantityKind -> quantityKind.getName().equals( name ) )
                   .map( QuantityKind.class::cast )
                   .findAny();
   }
}
"""
}

def toUpperSnakeCase(String s) {
    return s.replaceAll(/([A-Z])/, /_$1/).toUpperCase().replaceAll(/^_/, '')
}

def model = ModelFactory.createDefaultModel()
model.read(getClass().getResourceAsStream("samm/unit/1.0.0/units.ttl"), "", RDFLanguages.TURTLE.getName())

def units = []
QueryExecutionFactory.create(new File("${project.basedir}/buildSrc/main/resources/units.sparql").text, model).execSelect().each { unit ->
    def unitName = unit.get("unitName").asLiteral().toString()
    def label = unit.get("label").asLiteral().toString()
    def symbol = unit.get("symbol") == null ? "Optional.empty()" : "Optional.of( \"" + unit.get("symbol") + "\" )"
    def code = unit.get("code") == null ? "Optional.empty()" : "Optional.of( \"" + unit.get("code") + "\" )"
    def referenceUnitName = unit.get("referenceUnitName") == null ? "Optional.empty()" :
            "Optional.of( \"" + unit.get("referenceUnitName").asLiteral().toString() + "\" )"
    def conversionFactor = unit.get("conversionFactor") == null ? "Optional.empty()" :
            "Optional.of( \"" + unit.get("conversionFactor").asLiteral().toString() + "\" )"

    def quantityKindsString = unit.get("quantityKinds") == null ? "" : unit.get("quantityKinds").asLiteral().toString().split(",")
            .collect { quantityKind -> "QuantityKinds." + toUpperSnakeCase(quantityKind) }.join(", ")
    def quantityKinds = unit.get("quantityKinds") == null ? "Collections.emptySet()" :
            "new HashSet<>( Arrays.asList( " + quantityKindsString + " ) )"
    units.add("""      UNITS_BY_NAME.put( "${unitName}", version -> new DefaultUnit( MetaModelBaseAttributes.from( version, urn( version, "${unitName}" ), "${unitName}", "${label}" ), ${symbol}, ${code}, ${referenceUnitName}, ${conversionFactor}, ${quantityKinds} ) );""")
}

def quantityKinds = []
QueryExecutionFactory.create(new File("${project.basedir}/buildSrc/main/resources/quantitykinds.sparql").text, model).execSelect().each { unit ->
    def quantityKindName = unit.get("quantityKindName").asLiteral().toString()
    def label = unit.get("label").asLiteral().toString()
    def enumFieldName = toUpperSnakeCase(quantityKindName)
    quantityKinds.add("""${enumFieldName}( "${quantityKindName}", "${label}" )""")
}

def path = "${project.basedir}/src-gen/main/java/org/eclipse/esmf/metamodel"
new File(path).mkdirs()
System.out.println("Writing generated source file: " + path + "/Units.java")
Paths.get(path).resolve("Units.java")
        .toFile()
        .newWriter(StandardCharsets.UTF_8.name())
        .withWriter { w -> w << generateUnitsClass(units) }
System.out.println("Writing generated source file: " + path + "/QuantityKinds.java")
Paths.get(path).resolve("QuantityKinds.java")
        .toFile()
        .newWriter(StandardCharsets.UTF_8.name())
        .withWriter { w -> w << generateQuantityKindClass(quantityKinds) }
