import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityWithMultipleProperties,} from './AspectWithEntityWithMultipleProperties';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithEntityWithMultipleProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityWithMultipleProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityWithMultipleProperties implements StaticMetaClass<AspectWithEntityWithMultipleProperties>, PropertyContainer<AspectWithEntityWithMultipleProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityWithMultipleProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityWithMultipleProperties();


 public static readonly  TEST_ENTITY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityWithMultipleProperties, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityWithMultipleProperties';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testEntity',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testEntity',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEntityWithMultipleProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityWithMultipleProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityWithMultipleProperties';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityWithMultipleProperties, any>> {
return [MetaAspectWithEntityWithMultipleProperties.TEST_ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityWithMultipleProperties, any>> {
    return this.getProperties();
}




    }


