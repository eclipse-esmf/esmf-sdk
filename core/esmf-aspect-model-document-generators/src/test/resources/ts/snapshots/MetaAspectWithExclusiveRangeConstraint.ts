













import { AspectWithExclusiveRangeConstraint,} from './AspectWithExclusiveRangeConstraint';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithExclusiveRangeConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExclusiveRangeConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithExclusiveRangeConstraint implements StaticMetaClass<AspectWithExclusiveRangeConstraint>, PropertyContainer<AspectWithExclusiveRangeConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithExclusiveRangeConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithExclusiveRangeConstraint();


 public static readonly  FLOAT_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'floatProp',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'FloatRange',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},{
metaModelBaseAttributes : {
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [ {
value : "This is a floating range constraint",
languageTag : 'en',
},
 ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Float.valueOf( "12.3" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Float.valueOf( "23.45" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),BoundDefinition.GREATER_THAN,BoundDefinition.LESS_THAN));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'floatProp',
    isAbstract : false,
    });




 public static readonly  DOUBLE_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'doubleProp',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'DoubleRange',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},{
metaModelBaseAttributes : {
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [ {
value : "This is a double range constraint",
languageTag : 'en',
},
 ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Double.valueOf( "12.3" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Double.valueOf( "23.45" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),
}),BoundDefinition.GREATER_THAN,BoundDefinition.LESS_THAN));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'doubleProp',
    isAbstract : false,
    });




 public static readonly  DECIMAL_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'decimalProp',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'DecimalRange',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},{
metaModelBaseAttributes : {
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [ {
value : "This is a decimal range constraint",
languageTag : 'en',
},
 ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : new BigDecimal( "12.3" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : new BigDecimal( "23.45" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),
}),BoundDefinition.GREATER_THAN,BoundDefinition.LESS_THAN));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'decimalProp',
    isAbstract : false,
    });




 public static readonly  INTEGER_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'integerProp',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'IntegerRange',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},{
metaModelBaseAttributes : {
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [ {
value : "This is a integer range constraint",
languageTag : 'en',
},
 ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : new BigInteger( "12" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : new BigInteger( "23" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),
}),BoundDefinition.GREATER_THAN,BoundDefinition.LESS_THAN));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'integerProp',
    isAbstract : false,
    });




 public static readonly  INT_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'intProp',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'IntRange',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},{
metaModelBaseAttributes : {
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Integer.valueOf( "12" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Integer.valueOf( "23" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),
}),BoundDefinition.GREATER_THAN,BoundDefinition.LESS_THAN));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'intProp',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithExclusiveRangeConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithExclusiveRangeConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithExclusiveRangeConstraint';
}

                        getProperties(): Array<StaticProperty<AspectWithExclusiveRangeConstraint, any>> {
return [MetaAspectWithExclusiveRangeConstraint.FLOAT_PROP, MetaAspectWithExclusiveRangeConstraint.DOUBLE_PROP, MetaAspectWithExclusiveRangeConstraint.DECIMAL_PROP, MetaAspectWithExclusiveRangeConstraint.INTEGER_PROP, MetaAspectWithExclusiveRangeConstraint.INT_PROP];
}

getAllProperties(): Array<StaticProperty<AspectWithExclusiveRangeConstraint, any>> {
    return this.getProperties();
}




    }


