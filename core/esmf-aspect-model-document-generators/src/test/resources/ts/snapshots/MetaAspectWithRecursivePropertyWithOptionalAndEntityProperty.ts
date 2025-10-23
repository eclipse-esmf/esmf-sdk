import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithRecursivePropertyWithOptionalAndEntityProperty,} from './AspectWithRecursivePropertyWithOptionalAndEntityProperty';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRecursivePropertyWithOptionalAndEntityProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty implements StaticMetaClass<AspectWithRecursivePropertyWithOptionalAndEntityProperty>, PropertyContainer<AspectWithRecursivePropertyWithOptionalAndEntityProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRecursivePropertyWithOptionalAndEntityProperty, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultSingleEntity({
urn : this.NAMESPACE + 'TestItemCharacteristic',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
}, DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty()))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';
}

                        getProperties(): Array<StaticProperty<AspectWithRecursivePropertyWithOptionalAndEntityProperty, any>> {
return [MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRecursivePropertyWithOptionalAndEntityProperty, any>> {
    return this.getProperties();
}




    }


