import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityWithMultipleProperties,} from './AspectWithEntityWithMultipleProperties';
import { DefaultCharacteristic,DefaultEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithEntityWithMultipleProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityWithMultipleProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEntityWithMultipleProperties',
    'testEntity',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
'ReplacedAspectArtifactCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testEntity',
    false,
    );




getModelClass(): string {
return 'AspectWithEntityWithMultipleProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityWithMultipleProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
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


