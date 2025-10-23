













import { AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues,} from './AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues implements StaticMetaClass<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues>, PropertyContainer<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';
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
}),Optional.of({
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
value : new BigInteger( "10" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),
}),BoundDefinition.GREATER_THAN,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';
}

                        getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues, any>> {
return [MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues, any>> {
    return this.getProperties();
}




    }


