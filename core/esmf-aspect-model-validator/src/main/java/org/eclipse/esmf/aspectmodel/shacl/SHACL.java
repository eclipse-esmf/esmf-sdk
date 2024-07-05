/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.shacl;

import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * Vocabulary for the Shapes Constraint Language (SHACL).
 */
// Since the class is an RDF vocabulary, naming rules for the class and for several methods (which should be named identically
// to the corresponding model elements) are suppressed.
@SuppressWarnings( { "checkstyle:AbbreviationAsWordInName", "NewMethodNamingConvention", "unused" } )
public class SHACL implements RdfNamespace {
   public static final String NS = "http://www.w3.org/ns/shacl#";

   @Override
   public String getUri() {
      return NS;
   }

   @Override
   public String getNamespace() {
      return NS;
   }

   @Override
   public String getShortForm() {
      return "sh";
   }

   /**
    * The (single) value of this property must be a list of path elements, representing
    * the elements of alternative paths.
    */
   public Property alternativePath() {
      return property( "alternativePath" );
   }

   /** RDF list of shapes to validate the value nodes against. */
   public Property and() {
      return property( "and" );
   }

   /** The annotation property that shall be set. */
   public Property annotationProperty() {
      return property( "annotationProperty" );
   }

   /** The (default) values of the annotation property. */
   public Property annotationValue() {
      return property( "annotationValue" );
   }

   /**
    * The name of the SPARQL variable from the SELECT clause that shall be used
    * for the values.
    */
   public Property annotationVarName() {
      return property( "annotationVarName" );
   }

   /** The SPARQL ASK query to execute. */
   public Property ask() {
      return property( "ask" );
   }

   /** The type that all value nodes must have. */
   public Property class_() {
      return property( "class" );
   }

   /** If set to true then the shape is closed. */
   public Property closed() {
      return property( "closed" );
   }

   /**
    * The shapes that the focus nodes need to conform to before a rule is executed
    * on them.
    */
   public Property condition() {
      return property( "condition" );
   }

   /** True if the validation did not produce any validation results, and false otherwise. */
   public Property conforms() {
      return property( "conforms" );
   }

   /** The SPARQL CONSTRUCT query to execute. */
   public Property construct() {
      return property( "construct" );
   }

   /** Specifies an RDF datatype that all value nodes must have. */
   public Property datatype() {
      return property( "datatype" );
   }

   /** If set to true then all nodes conform to this. */
   public Property deactivated() {
      return property( "deactivated" );
   }

   /** Links a resource with its namespace prefix declarations. */
   public Property declare() {
      return property( "declare" );
   }

   /**
    * A default value for a property, for example for user interface tools to pre-populate
    * input fields.
    */
   public Property defaultValue() {
      return property( "defaultValue" );
   }

   /**
    * Human-readable descriptions for the property in the context of the surrounding
    * shape.
    */
   public Property description() {
      return property( "description" );
   }

   /**
    * Links a result with other results that provide more details, for example to
    * describe violations against nested shapes.
    */
   public Property detail() {
      return property( "detail" );
   }

   /**
    * Specifies a property where the set of values must be disjoint with the value
    * nodes.
    */
   public Property disjoint() {
      return property( "disjoint" );
   }

   /**
    * An entailment regime that indicates what kind of inferencing is required by
    * a shapes graph.
    */
   public Property entailment() {
      return property( "entailment" );
   }

   /** Specifies a property that must have the same values as the value nodes. */
   public Property equals() {
      return property( "equals" );
   }

   /** The node expression that must return true for the value nodes. */
   public Property expression() {
      return property( "expression" );
   }

   /** The shape that all input nodes of the expression need to conform to. */
   public Property filterShape() {
      return property( "filterShape" );
   }

   /** An optional flag to be used with regular expression pattern matching. */
   public Property flags() {
      return property( "flags" );
   }

   /** The focus node that was validated when the result was produced. */
   public Property focusNode() {
      return property( "focusNode" );
   }

   /**
    * Can be used to link to a property group to indicate that a property shape
    * belongs to a group of related property shapes.
    */
   public Property group() {
      return property( "group" );
   }

   /** Specifies a value that must be among the value nodes. */
   public Property hasValue() {
      return property( "hasValue" );
   }

   /**
    * An optional RDF list of properties that are also permitted in addition to
    * those explicitly enumerated via sh:property/sh:path.
    */
   public Property ignoredProperties() {
      return property( "ignoredProperties" );
   }

   /**
    * Specifies a list of allowed values so that each value node must be among the
    * members of the given list.
    */
   public Property in() {
      return property( "in" );
   }

   /** A list of node expressions that shall be intersected. */
   public Property intersection() {
      return property( "intersection" );
   }

   /**
    * The (single) value of this property represents an inverse path (object to
    * subject).
    */
   public Property inversePath() {
      return property( "inversePath" );
   }

   /** Constraints expressed in JavaScript. */
   public Property js() {
      return property( "js" );
   }

   /** The name of the JavaScript function to execute. */
   public Property jsFunctionName() {
      return property( "jsFunctionName" );
   }

   /** Declares which JavaScript libraries are needed to execute this. */
   public Property jsLibrary() {
      return property( "jsLibrary" );
   }

   /**
    * Declares the URLs of a JavaScript library. This should be the absolute URL
    * of a JavaScript file. Implementations may redirect those to local files.
    */
   public Property jsLibraryURL() {
      return property( "jsLibraryURL" );
   }

   /**
    * Outlines how human-readable labels of instances of the associated Parameterizable
    * shall be produced. The values can contain {?paramName} as placeholders for
    * the actual values of the given parameter.
    */
   public Property labelTemplate() {
      return property( "labelTemplate" );
   }

   /** Specifies a list of language tags that all value nodes must have. */
   public Property languageIn() {
      return property( "languageIn" );
   }

   /** Specifies a property that must have smaller values than the value nodes. */
   public Property lessThan() {
      return property( "lessThan" );
   }

   /**
    * Specifies a property that must have smaller or equal values than the value
    * nodes.
    */
   public Property lessThanOrEquals() {
      return property( "lessThanOrEquals" );
   }

   /** Specifies the maximum number of values in the set of value nodes. */
   public Property maxCount() {
      return property( "maxCount" );
   }

   /** Specifies the maximum exclusive value of each value node. */
   public Property maxExclusive() {
      return property( "maxExclusive" );
   }

   /** Specifies the maximum inclusive value of each value node. */
   public Property maxInclusive() {
      return property( "maxInclusive" );
   }

   /** Specifies the maximum string length of each value node. */
   public Property maxLength() {
      return property( "maxLength" );
   }

   /**
    * A human-readable message (possibly with placeholders for variables) explaining
    * the cause of the result.
    */
   public Property message() {
      return property( "message" );
   }

   /** Specifies the minimum number of values in the set of value nodes. */
   public Property minCount() {
      return property( "minCount" );
   }

   /** Specifies the minimum exclusive value of each value node. */
   public Property minExclusive() {
      return property( "minExclusive" );
   }

   /** Specifies the minimum inclusive value of each value node. */
   public Property minInclusive() {
      return property( "minInclusive" );
   }

   /** Specifies the minimum string length of each value node. */
   public Property minLength() {
      return property( "minLength" );
   }

   /** Human-readable labels for the property in the context of the surrounding shape. */
   public Property name() {
      return property( "name" );
   }

   /** The namespace associated with a prefix in a prefix declaration. */
   public Property namespace() {
      return property( "namespace" );
   }

   /** Specifies the node shape that all value nodes must conform to. */
   public Property node() {
      return property( "node" );
   }

   /** Specifies the node kind (e.g. IRI or literal) each value node. */
   public Property nodeKind() {
      return property( "nodeKind" );
   }

   /** The validator(s) used to evaluate a constraint in the context of a node shape. */
   public Property nodeValidator() {
      return property( "nodeValidator" );
   }

   /** The node expression producing the input nodes of a filter shape expression. */
   public Property nodes() {
      return property( "nodes" );
   }

   /** Specifies a shape that the value nodes must not conform to. */
   public Property not() {
      return property( "not" );
   }

   /** An expression producing the nodes that shall be inferred as objects. */
   public Property object() {
      return property( "object" );
   }

   /**
    * The (single) value of this property represents a path that is matched one
    * or more times.
    */
   public Property oneOrMorePath() {
      return property( "oneOrMorePath" );
   }

   /** Indicates whether a parameter is optional. */
   public Property optional() {
      return property( "optional" );
   }

   /**
    * Specifies a list of shapes so that the value nodes must conform to at least
    * one of the shapes.
    */
   public Property or() {
      return property( "or" );
   }

   /**
    * Specifies the relative order of this compared to its siblings. For example
    * use 0 for the first, 1 for the second.
    */
   public Property order() {
      return property( "order" );
   }

   /** The parameters of a function or constraint component. */
   public Property parameter() {
      return property( "parameter" );
   }

   /** Specifies the property path of a property shape. */
   public Property path() {
      return property( "path" );
   }

   /**
    * Specifies a regular expression pattern that the string representations of
    * the value nodes must match.
    */
   public Property pattern() {
      return property( "pattern" );
   }

   /** An expression producing the properties that shall be inferred as predicates. */
   public Property predicate() {
      return property( "predicate" );
   }

   /** The prefix of a prefix declaration. */
   public Property prefix() {
      return property( "prefix" );
   }

   /** The prefixes that shall be applied before parsing the associated SPARQL query. */
   public Property prefixes() {
      return property( "prefixes" );
   }

   /** Links a shape to its property shapes. */
   public Property property() {
      return property( "property" );
   }

   /**
    * The validator(s) used to evaluate a constraint in the context of a property
    * shape.
    */
   public Property propertyValidator() {
      return property( "propertyValidator" );
   }

   /** The maximum number of value nodes that can conform to the shape. */
   public Property qualifiedMaxCount() {
      return property( "qualifiedMaxCount" );
   }

   /** The minimum number of value nodes that must conform to the shape. */
   public Property qualifiedMinCount() {
      return property( "qualifiedMinCount" );
   }

   /** The shape that a specified number of values must conform to. */
   public Property qualifiedValueShape() {
      return property( "qualifiedValueShape" );
   }

   /**
    * Can be used to mark the qualified value shape to be disjoint with its sibling
    * shapes.
    */
   public Property qualifiedValueShapesDisjoint() {
      return property( "qualifiedValueShapesDisjoint" );
   }

   /** The validation results contained in a validation report. */
   public Property result() {
      return property( "result" );
   }

   /**
    * Links a SPARQL validator with zero or more sh:ResultAnnotation instances,
    * defining how to derive additional result properties based on the variables
    * of the SELECT query.
    */
   public Property resultAnnotation() {
      return property( "resultAnnotation" );
   }

   /** Human-readable messages explaining the cause of the result. */
   public Property resultMessage() {
      return property( "resultMessage" );
   }

   /**
    * The path of a validation result, based on the path of the validated property
    * shape.
    */
   public Property resultPath() {
      return property( "resultPath" );
   }

   /** The severity of the result, e.g. warning. */
   public Property resultSeverity() {
      return property( "resultSeverity" );
   }

   /** The expected type of values returned by the associated function. */
   public Property returnType() {
      return property( "returnType" );
   }

   /** The rules linked to a shape. */
   public Property rule() {
      return property( "rule" );
   }

   /** The SPARQL SELECT query to execute. */
   public Property select() {
      return property( "select" );
   }

   /**
    * Defines the severity that validation results produced by a shape must have.
    * Defaults to sh:Violation.
    */
   public Property severity() {
      return property( "severity" );
   }

   /** Shapes graphs that should be used when validating this data graph. */
   public Property shapesGraph() {
      return property( "shapesGraph" );
   }

   /**
    * If true then the validation engine was certain that the shapes graph has passed
    * all SHACL syntax requirements during the validation process.
    */
   public Property shapesGraphWellFormed() {
      return property( "shapesGraphWellFormed" );
   }

   /** The constraint that was validated when the result was produced. */
   public Property sourceConstraint() {
      return property( "sourceConstraint" );
   }

   /** The constraint component that is the source of the result. */
   public Property sourceConstraintComponent() {
      return property( "sourceConstraintComponent" );
   }

   /** The shape that is was validated when the result was produced. */
   public Property sourceShape() {
      return property( "sourceShape" );
   }

   /** Links a shape with SPARQL constraints. */
   public Property sparql() {
      return property( "sparql" );
   }

   /** An expression producing the resources that shall be inferred as subjects. */
   public Property subject() {
      return property( "subject" );
   }

   /**
    * Suggested shapes graphs for this ontology. The values of this property may
    * be used in the absence of specific sh:shapesGraph statements.
    */
   public Property suggestedShapesGraph() {
      return property( "suggestedShapesGraph" );
   }

   /**
    * Links a shape to a target specified by an extension language, for example
    * instances of sh:SPARQLTarget.
    */
   public Property target() {
      return property( "target" );
   }

   /**
    * Links a shape to a class, indicating that all instances of the class must
    * conform to the shape.
    */
   public Property targetClass() {
      return property( "targetClass" );
   }

   /**
    * Links a shape to individual nodes, indicating that these nodes must conform
    * to the shape.
    */
   public Property targetNode() {
      return property( "targetNode" );
   }

   /**
    * Links a shape to a property, indicating that all all objects of triples that
    * have the given property as their predicate must conform to the shape.
    */
   public Property targetObjectsOf() {
      return property( "targetObjectsOf" );
   }

   /**
    * Links a shape to a property, indicating that all subjects of triples that
    * have the given property as their predicate must conform to the shape.
    */
   public Property targetSubjectsOf() {
      return property( "targetSubjectsOf" );
   }

   /** A list of node expressions that shall be used together. */
   public Property union() {
      return property( "union" );
   }

   /** Specifies whether all node values must have a unique (or no) language tag. */
   public Property uniqueLang() {
      return property( "uniqueLang" );
   }

   /** The SPARQL UPDATE to execute. */
   public Property update() {
      return property( "update" );
   }

   /** The validator(s) used to evaluate constraints of either node or property shapes. */
   public Property validator() {
      return property( "validator" );
   }

   /** An RDF node that has caused the result. */
   public Property value() {
      return property( "value" );
   }

   /**
    * Specifies a list of shapes so that the value nodes must conform to exactly
    * one of the shapes.
    */
   public Property xone() {
      return property( "xone" );
   }

   /**
    * The (single) value of this property represents a path that is matched zero
    * or more times.
    */
   public Property zeroOrMorePath() {
      return property( "zeroOrMorePath" );
   }

   /**
    * The (single) value of this property represents a path that is matched zero
    * or one times.
    */
   public Property zeroOrOnePath() {
      return property( "zeroOrOnePath" );
   }

   /** The base class of validation results, typically not instantiated directly. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource AbstractResult() {
      return resource( "AbstractResult" );
   }

   /**
    * A constraint component that can be used to test whether a value node conforms
    * to all members of a provided list of shapes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource AndConstraintComponent() {
      return resource( "AndConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource AndConstraintComponent_and() {
      return resource( "AndConstraintComponent-and" );
   }

   /** The node kind of all blank nodes. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource BlankNode() {
      return resource( "BlankNode" );
   }

   /** The node kind of all blank nodes or IRIs. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource BlankNodeOrIRI() {
      return resource( "BlankNodeOrIRI" );
   }

   /** The node kind of all blank nodes or literals. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource BlankNodeOrLiteral() {
      return resource( "BlankNodeOrLiteral" );
   }

   /**
    * A constraint component that can be used to verify that each value node is
    * an instance of a given type.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ClassConstraintComponent() {
      return resource( "ClassConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ClassConstraintComponent_class() {
      return resource( "ClassConstraintComponent-class" );
   }

   /**
    * A constraint component that can be used to indicate that focus nodes must
    * only have values for those properties that have been explicitly enumerated
    * via sh:property/sh:path.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ClosedConstraintComponent() {
      return resource( "ClosedConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ClosedConstraintComponent_closed() {
      return resource( "ClosedConstraintComponent-closed" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ClosedConstraintComponent_ignoredProperties() {
      return resource( "ClosedConstraintComponent-ignoredProperties" );
   }

   /** The class of constraint components. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ConstraintComponent() {
      return resource( "ConstraintComponent" );
   }

   /**
    * A constraint component that can be used to restrict the datatype of all value
    * nodes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource DatatypeConstraintComponent() {
      return resource( "DatatypeConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource DatatypeConstraintComponent_datatype() {
      return resource( "DatatypeConstraintComponent-datatype" );
   }

   /**
    * A constraint component that can be used to verify that the set of value nodes
    * is disjoint with the the set of nodes that have the focus node as subject
    * and the value of a given property as predicate.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource DisjointConstraintComponent() {
      return resource( "DisjointConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource DisjointConstraintComponent_disjoint() {
      return resource( "DisjointConstraintComponent-disjoint" );
   }

   /**
    * A constraint component that can be used to verify that the set of value nodes
    * is equal to the set of nodes that have the focus node as subject and the value
    * of a given property as predicate.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource EqualsConstraintComponent() {
      return resource( "EqualsConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource EqualsConstraintComponent_equals() {
      return resource( "EqualsConstraintComponent-equals" );
   }

   /**
    * A constraint component that can be used to verify that a given node expression
    * produces true for all value nodes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ExpressionConstraintComponent() {
      return resource( "ExpressionConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ExpressionConstraintComponent_expression() {
      return resource( "ExpressionConstraintComponent-expression" );
   }

   /** The class of SHACL functions. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Function() {
      return resource( "Function" );
   }

   /**
    * A constraint component that can be used to verify that one of the value nodes
    * is a given RDF node.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource HasValueConstraintComponent() {
      return resource( "HasValueConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource HasValueConstraintComponent_hasValue() {
      return resource( "HasValueConstraintComponent-hasValue" );
   }

   /** The node kind of all IRIs. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource IRI() {
      return resource( "IRI" );
   }

   /** The node kind of all IRIs or literals. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource IRIOrLiteral() {
      return resource( "IRIOrLiteral" );
   }

   /**
    * A constraint component that can be used to exclusively enumerate the permitted
    * value nodes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource InConstraintComponent() {
      return resource( "InConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource InConstraintComponent_in() {
      return resource( "InConstraintComponent-in" );
   }

   /** The severity for an informational validation result. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Info() {
      return resource( "Info" );
   }

   /** The class of constraints backed by a JavaScript function. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSConstraint() {
      return resource( "JSConstraint" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSConstraint_js() {
      return resource( "JSConstraint-js" );
   }

   /**
    * A constraint component with the parameter sh:js linking to a sh:JSConstraint
    * containing a sh:script.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSConstraintComponent() {
      return resource( "JSConstraintComponent" );
   }

   /** Abstract base class of resources that declare an executable JavaScript. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSExecutable() {
      return resource( "JSExecutable" );
   }

   /** The class of SHACL functions that execute a JavaScript function when called. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSFunction() {
      return resource( "JSFunction" );
   }

   /**
    * Represents a JavaScript library, typically identified by one or more URLs
    * of files to include.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSLibrary() {
      return resource( "JSLibrary" );
   }

   /** The class of SHACL rules expressed using JavaScript. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSRule() {
      return resource( "JSRule" );
   }

   /** The class of targets that are based on JavaScript functions. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSTarget() {
      return resource( "JSTarget" );
   }

   /**
    * The (meta) class for parameterizable targets that are based on JavaScript
    * functions.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSTargetType() {
      return resource( "JSTargetType" );
   }

   /**
    * A SHACL validator based on JavaScript. This can be used to declare SHACL constraint
    * components that perform JavaScript-based validation when used.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource JSValidator() {
      return resource( "JSValidator" );
   }

   /**
    * A constraint component that can be used to enumerate language tags that all
    * value nodes must have.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LanguageInConstraintComponent() {
      return resource( "LanguageInConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LanguageInConstraintComponent_languageIn() {
      return resource( "LanguageInConstraintComponent-languageIn" );
   }

   /**
    * A constraint component that can be used to verify that each value node is
    * smaller than all the nodes that have the focus node as subject and the value
    * of a given property as predicate.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LessThanConstraintComponent() {
      return resource( "LessThanConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LessThanConstraintComponent_lessThan() {
      return resource( "LessThanConstraintComponent-lessThan" );
   }

   /**
    * A constraint component that can be used to verify that every value node is
    * smaller than all the nodes that have the focus node as subject and the value
    * of a given property as predicate.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LessThanOrEqualsConstraintComponent() {
      return resource( "LessThanOrEqualsConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource LessThanOrEqualsConstraintComponent_lessThanOrEquals() {
      return resource( "LessThanOrEqualsConstraintComponent-lessThanOrEquals" );
   }

   /** The node kind of all literals. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Literal() {
      return resource( "Literal" );
   }

   /**
    * A constraint component that can be used to restrict the maximum number of
    * value nodes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MaxCountConstraintComponent() {
      return resource( "MaxCountConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MaxCountConstraintComponent_maxCount() {
      return resource( "MaxCountConstraintComponent-maxCount" );
   }

   /**
    * A constraint component that can be used to restrict the range of value nodes
    * with a maximum exclusive value.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MaxExclusiveConstraintComponent() {
      return resource( "MaxExclusiveConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MaxExclusiveConstraintComponent_maxExclusive() {
      return resource( "MaxExclusiveConstraintComponent-maxExclusive" );
   }

   /**
    * A constraint component that can be used to restrict the range of value nodes
    * with a maximum inclusive value.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MaxInclusiveConstraintComponent() {
      return resource( "MaxInclusiveConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MaxInclusiveConstraintComponent_maxInclusive() {
      return resource( "MaxInclusiveConstraintComponent-maxInclusive" );
   }

   /**
    * A constraint component that can be used to restrict the maximum string length
    * of value nodes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MaxLengthConstraintComponent() {
      return resource( "MaxLengthConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MaxLengthConstraintComponent_maxLength() {
      return resource( "MaxLengthConstraintComponent-maxLength" );
   }

   /**
    * A constraint component that can be used to restrict the minimum number of
    * value nodes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MinCountConstraintComponent() {
      return resource( "MinCountConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MinCountConstraintComponent_minCount() {
      return resource( "MinCountConstraintComponent-minCount" );
   }

   /**
    * A constraint component that can be used to restrict the range of value nodes
    * with a minimum exclusive value.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MinExclusiveConstraintComponent() {
      return resource( "MinExclusiveConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MinExclusiveConstraintComponent_minExclusive() {
      return resource( "MinExclusiveConstraintComponent-minExclusive" );
   }

   /**
    * A constraint component that can be used to restrict the range of value nodes
    * with a minimum inclusive value.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MinInclusiveConstraintComponent() {
      return resource( "MinInclusiveConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MinInclusiveConstraintComponent_minInclusive() {
      return resource( "MinInclusiveConstraintComponent-minInclusive" );
   }

   /**
    * A constraint component that can be used to restrict the minimum string length
    * of value nodes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MinLengthConstraintComponent() {
      return resource( "MinLengthConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource MinLengthConstraintComponent_minLength() {
      return resource( "MinLengthConstraintComponent-minLength" );
   }

   /**
    * A constraint component that can be used to verify that all value nodes conform
    * to the given node shape.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource NodeConstraintComponent() {
      return resource( "NodeConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource NodeConstraintComponent_node() {
      return resource( "NodeConstraintComponent-node" );
   }

   /**
    * The class of all node kinds, including sh:BlankNode, sh:IRI, sh:Literal or
    * the combinations of these: sh:BlankNodeOrIRI, sh:BlankNodeOrLiteral, sh:IRIOrLiteral.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource NodeKind() {
      return resource( "NodeKind" );
   }

   /**
    * A constraint component that can be used to restrict the RDF node kind of each
    * value node.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource NodeKindConstraintComponent() {
      return resource( "NodeKindConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource NodeKindConstraintComponent_nodeKind() {
      return resource( "NodeKindConstraintComponent-nodeKind" );
   }

   /**
    * A node shape is a shape that specifies constraint that need to be met with
    * respect to focus nodes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource NodeShape() {
      return resource( "NodeShape" );
   }

   /**
    * A constraint component that can be used to verify that value nodes do not
    * conform to a given shape.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource NotConstraintComponent() {
      return resource( "NotConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource NotConstraintComponent_not() {
      return resource( "NotConstraintComponent-not" );
   }

   /**
    * A constraint component that can be used to restrict the value nodes so that
    * they conform to at least one out of several provided shapes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource OrConstraintComponent() {
      return resource( "OrConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource OrConstraintComponent_or() {
      return resource( "OrConstraintComponent-or" );
   }

   /**
    * The class of parameter declarations, consisting of a path predicate and (possibly)
    * information about allowed value type, cardinality and other characteristics.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Parameter() {
      return resource( "Parameter" );
   }

   /**
    * Superclass of components that can take parameters, especially functions and
    * constraint components.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Parameterizable() {
      return resource( "Parameterizable" );
   }

   /**
    * A constraint component that can be used to verify that every value node matches
    * a given regular expression.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource PatternConstraintComponent() {
      return resource( "PatternConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource PatternConstraintComponent_flags() {
      return resource( "PatternConstraintComponent-flags" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource PatternConstraintComponent_pattern() {
      return resource( "PatternConstraintComponent-pattern" );
   }

   /** The class of prefix declarations, consisting of pairs of a prefix with a namespace. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource PrefixDeclaration() {
      return resource( "PrefixDeclaration" );
   }

   /**
    * A constraint component that can be used to verify that all value nodes conform
    * to the given property shape.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource PropertyConstraintComponent() {
      return resource( "PropertyConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource PropertyConstraintComponent_property() {
      return resource( "PropertyConstraintComponent-property" );
   }

   /** Instances of this class represent groups of property shapes that belong together. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource PropertyGroup() {
      return resource( "PropertyGroup" );
   }

   /**
    * A property shape is a shape that specifies constraints on the values of a
    * focus node for a given property or path.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource PropertyShape() {
      return resource( "PropertyShape" );
   }

   /**
    * A constraint component that can be used to verify that a specified maximum
    * number of value nodes conforms to a given shape.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QualifiedMaxCountConstraintComponent() {
      return resource( "QualifiedMaxCountConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QualifiedMaxCountConstraintComponent_qualifiedMaxCount() {
      return resource( "QualifiedMaxCountConstraintComponent-qualifiedMaxCount" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QualifiedMaxCountConstraintComponent_qualifiedValueShape() {
      return resource( "QualifiedMaxCountConstraintComponent-qualifiedValueShape" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QualifiedMaxCountConstraintComponent_qualifiedValueShapesDisjoint() {
      return resource( "QualifiedMaxCountConstraintComponent-qualifiedValueShapesDisjoint" );
   }

   /**
    * A constraint component that can be used to verify that a specified minimum
    * number of value nodes conforms to a given shape.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QualifiedMinCountConstraintComponent() {
      return resource( "QualifiedMinCountConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QualifiedMinCountConstraintComponent_qualifiedMinCount() {
      return resource( "QualifiedMinCountConstraintComponent-qualifiedMinCount" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QualifiedMinCountConstraintComponent_qualifiedValueShape() {
      return resource( "QualifiedMinCountConstraintComponent-qualifiedValueShape" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource QualifiedMinCountConstraintComponent_qualifiedValueShapesDisjoint() {
      return resource( "QualifiedMinCountConstraintComponent-qualifiedValueShapesDisjoint" );
   }

   /**
    * A class of result annotations, which define the rules to derive the values
    * of a given annotation property as extra values for a validation result.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ResultAnnotation() {
      return resource( "ResultAnnotation" );
   }

   /** The class of SHACL rules. Never instantiated directly. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Rule() {
      return resource( "Rule" );
   }

   /** The class of SPARQL executables that are based on an ASK query. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLAskExecutable() {
      return resource( "SPARQLAskExecutable" );
   }

   /**
    * The class of validators based on SPARQL ASK queries. The queries are evaluated
    * for each value node and are supposed to return true if the given node conforms.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLAskValidator() {
      return resource( "SPARQLAskValidator" );
   }

   /** The class of constraints based on SPARQL SELECT queries. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLConstraint() {
      return resource( "SPARQLConstraint" );
   }

   /**
    * A constraint component that can be used to define constraints based on SPARQL
    * queries.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLConstraintComponent() {
      return resource( "SPARQLConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLConstraintComponent_sparql() {
      return resource( "SPARQLConstraintComponent-sparql" );
   }

   /** The class of SPARQL executables that are based on a CONSTRUCT query. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLConstructExecutable() {
      return resource( "SPARQLConstructExecutable" );
   }

   /** The class of resources that encapsulate a SPARQL query. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLExecutable() {
      return resource( "SPARQLExecutable" );
   }

   /** A function backed by a SPARQL query - either ASK or SELECT. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLFunction() {
      return resource( "SPARQLFunction" );
   }

   /** The class of SHACL rules based on SPARQL CONSTRUCT queries. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLRule() {
      return resource( "SPARQLRule" );
   }

   /** The class of SPARQL executables based on a SELECT query. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLSelectExecutable() {
      return resource( "SPARQLSelectExecutable" );
   }

   /**
    * The class of validators based on SPARQL SELECT queries. The queries are evaluated
    * for each focus node and are supposed to produce bindings for all focus nodes
    * that do not conform.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLSelectValidator() {
      return resource( "SPARQLSelectValidator" );
   }

   /** The class of targets that are based on SPARQL queries. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLTarget() {
      return resource( "SPARQLTarget" );
   }

   /** The (meta) class for parameterizable targets that are based on SPARQL queries. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLTargetType() {
      return resource( "SPARQLTargetType" );
   }

   /** The class of SPARQL executables based on a SPARQL UPDATE. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource SPARQLUpdateExecutable() {
      return resource( "SPARQLUpdateExecutable" );
   }

   /**
    * The class of validation result severity levels, including violation and warning
    * levels.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Severity() {
      return resource( "Severity" );
   }

   /** A shape is a collection of constraints that may be targeted for certain nodes. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Shape() {
      return resource( "Shape" );
   }

   /** The base class of targets such as those based on SPARQL queries. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Target() {
      return resource( "Target" );
   }

   /**
    * The (meta) class for parameterizable targets. Instances of this are instantiated
    * as values of the sh:target property.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource TargetType() {
      return resource( "TargetType" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource TripleRule() {
      return resource( "TripleRule" );
   }

   /**
    * A constraint component that can be used to specify that no pair of value nodes
    * may use the same language tag.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource UniqueLangConstraintComponent() {
      return resource( "UniqueLangConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource UniqueLangConstraintComponent_uniqueLang() {
      return resource( "UniqueLangConstraintComponent-uniqueLang" );
   }

   /** The class of SHACL validation reports. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ValidationReport() {
      return resource( "ValidationReport" );
   }

   /** The class of validation results. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource ValidationResult() {
      return resource( "ValidationResult" );
   }

   /**
    * The class of validators, which provide instructions on how to process a constraint
    * definition. This class serves as base class for the SPARQL-based validators
    * and other possible implementations.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Validator() {
      return resource( "Validator" );
   }

   /** The severity for a violation validation result. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Violation() {
      return resource( "Violation" );
   }

   /** The severity for a warning validation result. */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource Warning() {
      return resource( "Warning" );
   }

   /**
    * A constraint component that can be used to restrict the value nodes so that
    * they conform to exactly one out of several provided shapes.
    */
   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource XoneConstraintComponent() {
      return resource( "XoneConstraintComponent" );
   }

   @SuppressWarnings( "checkstyle:MethodName" )
   public Resource XoneConstraintComponent_xone() {
      return resource( "XoneConstraintComponent-xone" );
   }
}
