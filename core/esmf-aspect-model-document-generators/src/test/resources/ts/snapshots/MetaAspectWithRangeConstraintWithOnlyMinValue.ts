













import { AspectWithRangeConstraintWithOnlyMinValue,} from './AspectWithRangeConstraintWithOnlyMinValue';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithRangeConstraintWithOnlyMinValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithOnlyMinValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithOnlyMinValue implements StaticMetaClass<AspectWithRangeConstraintWithOnlyMinValue>, PropertyContainer<AspectWithRangeConstraintWithOnlyMinValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyMinValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithOnlyMinValue();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithOnlyMinValue, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithOnlyMinValue';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestTrait',
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
see : [ 'http://example.com/',
 ],
},Optional.of({
metaModelBaseAttributes : {
isAnonymous : true,
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
see : [ 'http://example.com/',
 ],
},
value : new BigInteger( "5" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),
}),Optional.empty(),BoundDefinition.GREATER_THAN,BoundDefinition.OPEN));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithRangeConstraintWithOnlyMinValue';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithOnlyMinValue .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithOnlyMinValue';
}

                        getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyMinValue, any>> {
return [MetaAspectWithRangeConstraintWithOnlyMinValue.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyMinValue, any>> {
    return this.getProperties();
}




    }


