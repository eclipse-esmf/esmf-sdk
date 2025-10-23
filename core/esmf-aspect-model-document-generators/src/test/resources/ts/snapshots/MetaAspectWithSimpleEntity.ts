import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithSimpleEntity,} from './AspectWithSimpleEntity';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';

import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';


    

/*
* Generated class MetaAspectWithSimpleEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimpleEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSimpleEntity implements StaticMetaClass<AspectWithSimpleEntity>, PropertyContainer<AspectWithSimpleEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSimpleEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSimpleEntity();


 public static readonly  ENTITY_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleEntity, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithSimpleEntity';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'entityProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultSingleEntity({
isAnonymous : true,
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
        payloadName : 'entityProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithSimpleEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithSimpleEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithSimpleEntity';
}

                        getProperties(): Array<StaticProperty<AspectWithSimpleEntity, any>> {
return [MetaAspectWithSimpleEntity.ENTITY_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithSimpleEntity, any>> {
    return this.getProperties();
}




    }


