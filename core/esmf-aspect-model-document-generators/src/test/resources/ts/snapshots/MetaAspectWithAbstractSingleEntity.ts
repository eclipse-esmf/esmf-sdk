import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithAbstractSingleEntity,} from './AspectWithAbstractSingleEntity';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';

import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';


    

/*
* Generated class MetaAspectWithAbstractSingleEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAbstractSingleEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithAbstractSingleEntity implements StaticMetaClass<AspectWithAbstractSingleEntity>, PropertyContainer<AspectWithAbstractSingleEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithAbstractSingleEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithAbstractSingleEntity();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithAbstractSingleEntity, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithAbstractSingleEntity';
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
urn : this.NAMESPACE + 'EntityCharacteristic',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
}, DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [ {
value : "This is an abstract test entity",
languageTag : 'en',
},
 ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [ {
value : "This is an abstract test entity",
languageTag : 'en',
},
 ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty(),List.of(AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#ReplacedAspectArtifact" ))))))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithAbstractSingleEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithAbstractSingleEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithAbstractSingleEntity';
}

                        getProperties(): Array<StaticProperty<AspectWithAbstractSingleEntity, any>> {
return [MetaAspectWithAbstractSingleEntity.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithAbstractSingleEntity, any>> {
    return this.getProperties();
}




    }


