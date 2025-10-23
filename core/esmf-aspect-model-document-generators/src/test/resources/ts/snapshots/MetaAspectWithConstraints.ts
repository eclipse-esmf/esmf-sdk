













import { AspectWithConstraints,} from './AspectWithConstraints';
import { DefaultStaticProperty,StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstraints implements StaticMetaClass<AspectWithConstraints>, PropertyContainer<AspectWithConstraints> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraints';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstraints();


 public static readonly  TEST_PROPERTY_WITH_REGULAR_EXPRESSION = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithRegularExpression',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestRegularExpressionConstraint',
preferredNames : [ {
value : "Test Regular Expression Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Regular Expression Constraint",
languageTag : 'en',
},
 ],
see : [  ],
},{
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#Text',
preferredNames : [ {
value : "Text",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.",
languageTag : 'en',
},
 ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultRegularExpressionConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},"^[a-zA-Z]\\.[0-9]"));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithRegularExpression',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_DECIMAL_MIN_DECIMAL_MAX_RANGE_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithDecimalMinDecimalMaxRangeConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestWithDecimalMinDecimalMaxRangeConstraint',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementDecimal',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),Units.fromName("metrePerSecond")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : new BigDecimal( "2.3" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : new BigDecimal( "10.5" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithDecimalMinDecimalMaxRangeConstraint',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_DECIMAL_MAX_RANGE_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithDecimalMaxRangeConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestWithDecimalMaxRangeConstraint',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementDecimal',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),Units.fromName("metrePerSecond")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.empty(),Optional.of({
metaModelBaseAttributes : {},
value : new BigDecimal( "10.5" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),
}),BoundDefinition.OPEN,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithDecimalMaxRangeConstraint',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_MIN_MAX_RANGE_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithMinMaxRangeConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestWithMinMaxRangeConstraint',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'Measurement',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),Units.fromName("metrePerSecond")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Integer.valueOf( "1" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Integer.valueOf( "10" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithMinMaxRangeConstraint',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_MIN_RANGE_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithMinRangeConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestWithMinRangeConstraint',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'Measurement',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),Units.fromName("metrePerSecond")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Integer.valueOf( "1" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),
}),Optional.empty(),BoundDefinition.AT_LEAST,BoundDefinition.OPEN));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithMinRangeConstraint',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_RANGE_CONSTRAINT_WITH_FLOAT_TYPE = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyRangeConstraintWithFloatType',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestRangeConstraintWithFloatType',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementWithFloatType',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),Units.fromName("metrePerSecond")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Float.valueOf( "1.0" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Float.valueOf( "10.0" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyRangeConstraintWithFloatType',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_RANGE_CONSTRAINT_WITH_DOUBLE_TYPE = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyRangeConstraintWithDoubleType',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestRangeConstraintWithDoubleType',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementWithDoubleType',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),Units.fromName("metrePerSecond")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Double.valueOf( "1.0" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Double.valueOf( "10.0" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyRangeConstraintWithDoubleType',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_MIN_MAX_LENGTH_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithMinMaxLengthConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestLengthConstraint',
preferredNames : [ {
value : "Test Length Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Length Constraint",
languageTag : 'en',
},
 ],
see : [  ],
},{
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#Text',
preferredNames : [ {
value : "Text",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.",
languageTag : 'en',
},
 ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultLengthConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new BigInteger( "1" )),Optional.of(new BigInteger( "10" ))));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithMinMaxLengthConstraint',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_MIN_LENGTH_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithMinLengthConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestLengthConstraintOnlyMin',
preferredNames : [ {
value : "Test Length Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Length Constraint",
languageTag : 'en',
},
 ],
see : [  ],
},{
metaModelBaseAttributes : {
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultLengthConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new BigInteger( "1" )),Optional.empty()));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithMinLengthConstraint',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT = 
                
        new (class extends StaticContainerProperty<AspectWithConstraints, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }

        getContainedType(): AspectWithConstraints {
            return 'AspectWithConstraints';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyCollectionLengthConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestLengthConstraintWithCollection',
preferredNames : [ {
value : "Test Length Constraint with collection",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Length Constraint with collection",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultList({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" )),Optional.empty()),new ArrayList<Constraint>(){{add(new DefaultLengthConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new BigInteger( "1" )),Optional.of(new BigInteger( "10" ))));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyCollectionLengthConstraint',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithConstraints';
}

getAspectModelUrn(): string {
return MetaAspectWithConstraints .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithConstraints';
}

                        getProperties(): Array<StaticProperty<AspectWithConstraints, any>> {
return [MetaAspectWithConstraints.TEST_PROPERTY_WITH_REGULAR_EXPRESSION, MetaAspectWithConstraints.TEST_PROPERTY_WITH_DECIMAL_MIN_DECIMAL_MAX_RANGE_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_WITH_DECIMAL_MAX_RANGE_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_WITH_MIN_MAX_RANGE_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_WITH_MIN_RANGE_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_RANGE_CONSTRAINT_WITH_FLOAT_TYPE, MetaAspectWithConstraints.TEST_PROPERTY_RANGE_CONSTRAINT_WITH_DOUBLE_TYPE, MetaAspectWithConstraints.TEST_PROPERTY_WITH_MIN_MAX_LENGTH_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_WITH_MIN_LENGTH_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT];
}

getAllProperties(): Array<StaticProperty<AspectWithConstraints, any>> {
    return this.getProperties();
}




    }


