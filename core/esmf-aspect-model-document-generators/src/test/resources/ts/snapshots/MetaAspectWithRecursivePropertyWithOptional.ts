import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithRecursivePropertyWithOptional,} from './AspectWithRecursivePropertyWithOptional';
import { DefaultEntity,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithRecursivePropertyWithOptional (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRecursivePropertyWithOptional).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithRecursivePropertyWithOptional implements StaticMetaClass<AspectWithRecursivePropertyWithOptional>, PropertyContainer<AspectWithRecursivePropertyWithOptional> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRecursivePropertyWithOptional';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRecursivePropertyWithOptional();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRecursivePropertyWithOptional, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithRecursivePropertyWithOptional';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithRecursivePropertyWithOptional',
    'testProperty',
    (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestItemCharacteristic',
'TestItemCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'TestItemCharacteristic';
 return defaultSingleEntity; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithRecursivePropertyWithOptional';
}

getAspectModelUrn(): string {
return MetaAspectWithRecursivePropertyWithOptional .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithRecursivePropertyWithOptional';
}

getProperties(): Array<StaticProperty<AspectWithRecursivePropertyWithOptional, any>> {
return [MetaAspectWithRecursivePropertyWithOptional.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRecursivePropertyWithOptional, any>> {
        return this.getProperties();
}




    }


