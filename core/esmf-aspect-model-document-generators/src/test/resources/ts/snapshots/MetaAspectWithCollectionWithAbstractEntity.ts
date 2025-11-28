import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';















import { AspectWithCollectionWithAbstractEntity,} from './AspectWithCollectionWithAbstractEntity';
import { DefaultCollection,DefaultEntity,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';

import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithCollectionWithAbstractEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithAbstractEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCollectionWithAbstractEntity implements StaticMetaClass<AspectWithCollectionWithAbstractEntity>, PropertyContainer<AspectWithCollectionWithAbstractEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithAbstractEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionWithAbstractEntity();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithAbstractEntity, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithAbstractEntity';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollectionWithAbstractEntity',
    'testProperty',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityCollectionCharacteristic',
'EntityCollectionCharacteristic',
true, false, undefined,
(() => { const abstractDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
abstractDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
abstractDefaultEntityReplacedAspectArtifact.addDescription('en' , 'This is an abstract test entity');
 return abstractDefaultEntityReplacedAspectArtifact; })())
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'EntityCollectionCharacteristic';
defaultCollection.addDescription('en' , 'This is an entity collection characteristic');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCollectionWithAbstractEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionWithAbstractEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCollectionWithAbstractEntity';
}

getProperties(): Array<StaticProperty<AspectWithCollectionWithAbstractEntity, any>> {
return [MetaAspectWithCollectionWithAbstractEntity.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionWithAbstractEntity, any>> {
        return this.getProperties();
}




    }


