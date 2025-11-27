import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithAbstractSingleEntity,} from './AspectWithAbstractSingleEntity';
import { DefaultEntity,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';

import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithAbstractSingleEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAbstractSingleEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithAbstractSingleEntity',
    'testProperty',
    (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityCharacteristic',
'EntityCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
extendsDefaultEntityReplacedAspectArtifact.addDescription('en' , 'This is an abstract test entity');
 return extendsDefaultEntityReplacedAspectArtifact; })())
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addDescription('en' , 'This is an abstract test entity');
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'EntityCharacteristic';
 return defaultSingleEntity; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithAbstractSingleEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithAbstractSingleEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
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


