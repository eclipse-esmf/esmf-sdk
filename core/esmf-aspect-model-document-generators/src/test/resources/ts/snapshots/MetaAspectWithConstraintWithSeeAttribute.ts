













import { AspectWithConstraintWithSeeAttribute,} from './AspectWithConstraintWithSeeAttribute';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithConstraintWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraintWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstraintWithSeeAttribute implements StaticMetaClass<AspectWithConstraintWithSeeAttribute>, PropertyContainer<AspectWithConstraintWithSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraintWithSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstraintWithSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraintWithSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraintWithSeeAttribute';
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
},Optional.of(new BigInteger( "5" )),Optional.of(new BigInteger( "10" ))));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraintWithSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraintWithSeeAttribute';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyTwo',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestTraitTwo',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},{
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'ReplacedAspectArtifactTwo',
preferredNames : [ {
value : "Test Characteristic Two",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Characteristic Two",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/me2',
 ],
},
},new ArrayList<Constraint>(){{add(new DefaultRegularExpressionConstraint({
isAnonymous : true,
preferredNames : [ {
value : "Test Constraint Two",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Constraint Two",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/me',
 ],
},"^[A-Z][A-Z][A-Z]$"));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyTwo',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithConstraintWithSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithConstraintWithSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithConstraintWithSeeAttribute';
}

                        getProperties(): Array<StaticProperty<AspectWithConstraintWithSeeAttribute, any>> {
return [MetaAspectWithConstraintWithSeeAttribute.TEST_PROPERTY, MetaAspectWithConstraintWithSeeAttribute.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithConstraintWithSeeAttribute, any>> {
    return this.getProperties();
}




    }


