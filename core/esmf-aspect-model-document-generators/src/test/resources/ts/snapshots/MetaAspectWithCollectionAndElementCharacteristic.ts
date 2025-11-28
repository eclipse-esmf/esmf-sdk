import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithCollectionAndElementCharacteristic,} from './AspectWithCollectionAndElementCharacteristic';
import { DefaultCollection,DefaultEntity,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';

import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';



    

/*
* Generated class MetaAspectWithCollectionAndElementCharacteristic (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionAndElementCharacteristic).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCollectionAndElementCharacteristic implements StaticMetaClass<AspectWithCollectionAndElementCharacteristic>, PropertyContainer<AspectWithCollectionAndElementCharacteristic> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionAndElementCharacteristic';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionAndElementCharacteristic();


 public static readonly  ITEMS = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionAndElementCharacteristic, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithCollectionAndElementCharacteristic';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollectionAndElementCharacteristic',
    'items',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
true, false, (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
'ReplacedAspectArtifactCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic';
 return defaultSingleEntity; })(),
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultCollection.isAnonymousNode = true;
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'items',
    false,
    );




getModelClass(): string {
return 'AspectWithCollectionAndElementCharacteristic';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionAndElementCharacteristic .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCollectionAndElementCharacteristic';
}

getProperties(): Array<StaticProperty<AspectWithCollectionAndElementCharacteristic, any>> {
return [MetaAspectWithCollectionAndElementCharacteristic.ITEMS];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionAndElementCharacteristic, any>> {
        return this.getProperties();
}




    }


