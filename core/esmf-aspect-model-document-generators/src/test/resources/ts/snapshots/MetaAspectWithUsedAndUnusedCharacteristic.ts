













import { AspectWithUsedAndUnusedCharacteristic,} from './AspectWithUsedAndUnusedCharacteristic';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithUsedAndUnusedCharacteristic (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedCharacteristic).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithUsedAndUnusedCharacteristic implements StaticMetaClass<AspectWithUsedAndUnusedCharacteristic>, PropertyContainer<AspectWithUsedAndUnusedCharacteristic> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedCharacteristic';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUsedAndUnusedCharacteristic();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithUsedAndUnusedCharacteristic, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithUsedAndUnusedCharacteristic';
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
urn : this.NAMESPACE + 'UsedReplacedAspectArtifact',
preferredNames : [ {
value : "Used Test Characteristic",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Used Test Characteristic",
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
return 'AspectWithUsedAndUnusedCharacteristic';
}

getAspectModelUrn(): string {
return MetaAspectWithUsedAndUnusedCharacteristic .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithUsedAndUnusedCharacteristic';
}

                        getProperties(): Array<StaticProperty<AspectWithUsedAndUnusedCharacteristic, any>> {
return [MetaAspectWithUsedAndUnusedCharacteristic.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUsedAndUnusedCharacteristic, any>> {
    return this.getProperties();
}




    }


