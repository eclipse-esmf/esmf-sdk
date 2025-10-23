













import { AspectWithConstraintWithoutSeeAttribute,} from './AspectWithConstraintWithoutSeeAttribute';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithConstraintWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraintWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstraintWithoutSeeAttribute implements StaticMetaClass<AspectWithConstraintWithoutSeeAttribute>, PropertyContainer<AspectWithConstraintWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraintWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstraintWithoutSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraintWithoutSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraintWithoutSeeAttribute';
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
see : [  ],
},Optional.of(new BigInteger( "5" )),Optional.of(new BigInteger( "10" ))));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithConstraintWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithConstraintWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithConstraintWithoutSeeAttribute';
}

                        getProperties(): Array<StaticProperty<AspectWithConstraintWithoutSeeAttribute, any>> {
return [MetaAspectWithConstraintWithoutSeeAttribute.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithConstraintWithoutSeeAttribute, any>> {
    return this.getProperties();
}




    }


