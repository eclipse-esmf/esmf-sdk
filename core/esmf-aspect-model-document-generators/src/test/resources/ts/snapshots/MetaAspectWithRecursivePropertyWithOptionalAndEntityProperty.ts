import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithRecursivePropertyWithOptionalAndEntityProperty,} from './AspectWithRecursivePropertyWithOptionalAndEntityProperty';
import { DefaultEntity,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRecursivePropertyWithOptionalAndEntityProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithRecursivePropertyWithOptionalAndEntityProperty',
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
return 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
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


