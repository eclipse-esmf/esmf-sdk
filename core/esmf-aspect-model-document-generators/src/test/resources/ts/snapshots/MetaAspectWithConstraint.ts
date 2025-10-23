













import { AspectWithConstraint,} from './AspectWithConstraint';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstraint implements StaticMetaClass<AspectWithConstraint>, PropertyContainer<AspectWithConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstraint();


 public static readonly  STRING_LC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'stringLcProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'StringLengthConstraint',
preferredNames : [ {
value : "Used Test Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Used Test Constraint",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
'http://example.com/me',
 ],
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
},Optional.of(new BigInteger( "20" )),Optional.of(new BigInteger( "22" ))));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'stringLcProperty',
    isAbstract : false,
    });




 public static readonly  DOUBLE_RC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'doubleRcProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'DoubleRangeConstraint',
preferredNames : [ {
value : "Test Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Constraint",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'DoubleMeasurement',
preferredNames : [  ],
descriptions : [ {
value : "The acceleration",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),Units.fromName("metrePerSecondSquared")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Double.valueOf( "-0.1" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Double.valueOf( "0.2" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'doubleRcProperty',
    isAbstract : false,
    });




 public static readonly  INT_RC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'intRcProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'IntegerRangeConstraint',
preferredNames : [ {
value : "Test Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Constraint",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'IntegerMeasurement',
preferredNames : [  ],
descriptions : [ {
value : "The acceleration",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),Units.fromName("metrePerSecondSquared")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Integer.valueOf( "-1" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Integer.valueOf( "-1" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'intRcProperty',
    isAbstract : false,
    });




 public static readonly  BIG_INT_RC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'bigIntRcProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'BigIntegerRangeConstraint',
preferredNames : [ {
value : "Test Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Constraint",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'BigIntegerMeasurement',
preferredNames : [  ],
descriptions : [ {
value : "The acceleration",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),Units.fromName("metrePerSecondSquared")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : new BigInteger( "10" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : new BigInteger( "15" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'bigIntRcProperty',
    isAbstract : false,
    });




 public static readonly  FLOAT_RC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'floatRcProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'FloatRangeConstraint',
preferredNames : [ {
value : "Test Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Constraint",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'FloatMeasurement',
preferredNames : [  ],
descriptions : [ {
value : "The acceleration",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),Units.fromName("metrePerSecondSquared")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Float.valueOf( "100.0" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Float.valueOf( "112.0" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'floatRcProperty',
    isAbstract : false,
    });




 public static readonly  STRING_REGEXC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'stringRegexcProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'RegularExpressionConstraint',
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
},"[a-zA-Z]"));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'stringRegexcProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithConstraint';
}

                        getProperties(): Array<StaticProperty<AspectWithConstraint, any>> {
return [MetaAspectWithConstraint.STRING_LC_PROPERTY, MetaAspectWithConstraint.DOUBLE_RC_PROPERTY, MetaAspectWithConstraint.INT_RC_PROPERTY, MetaAspectWithConstraint.BIG_INT_RC_PROPERTY, MetaAspectWithConstraint.FLOAT_RC_PROPERTY, MetaAspectWithConstraint.STRING_REGEXC_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithConstraint, any>> {
    return this.getProperties();
}




    }


