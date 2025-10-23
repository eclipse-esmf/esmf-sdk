













import { AspectWithRangeConstraintWithBoundDefinitionAttributes,} from './AspectWithRangeConstraintWithBoundDefinitionAttributes';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithRangeConstraintWithBoundDefinitionAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithBoundDefinitionAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithBoundDefinitionAttributes implements StaticMetaClass<AspectWithRangeConstraintWithBoundDefinitionAttributes>, PropertyContainer<AspectWithRangeConstraintWithBoundDefinitionAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithBoundDefinitionAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithBoundDefinitionAttributes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithBoundDefinitionAttributes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithBoundDefinitionAttributes';
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
}),BoundDefinition.GREATER_THAN,BoundDefinition.LESS_THAN));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithRangeConstraintWithBoundDefinitionAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithBoundDefinitionAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithBoundDefinitionAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithBoundDefinitionAttributes, any>> {
return [MetaAspectWithRangeConstraintWithBoundDefinitionAttributes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithBoundDefinitionAttributes, any>> {
    return this.getProperties();
}




    }


