













import { AspectWithCharacteristicWithMultipleSeeAttributes,} from './AspectWithCharacteristicWithMultipleSeeAttributes';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCharacteristicWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCharacteristicWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCharacteristicWithMultipleSeeAttributes implements StaticMetaClass<AspectWithCharacteristicWithMultipleSeeAttributes>, PropertyContainer<AspectWithCharacteristicWithMultipleSeeAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCharacteristicWithMultipleSeeAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCharacteristicWithMultipleSeeAttributes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithCharacteristicWithMultipleSeeAttributes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCharacteristicWithMultipleSeeAttributes';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "Test Characteristic",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Characteristic",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
'http://example.com/me',
 ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCharacteristicWithMultipleSeeAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithCharacteristicWithMultipleSeeAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCharacteristicWithMultipleSeeAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithCharacteristicWithMultipleSeeAttributes, any>> {
return [MetaAspectWithCharacteristicWithMultipleSeeAttributes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCharacteristicWithMultipleSeeAttributes, any>> {
    return this.getProperties();
}




    }


