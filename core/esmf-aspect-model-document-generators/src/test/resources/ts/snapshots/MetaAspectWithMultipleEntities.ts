import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntities,} from './AspectWithMultipleEntities';
import { DefaultCharacteristic,DefaultEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithMultipleEntities (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntities).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultipleEntities implements StaticMetaClass<AspectWithMultipleEntities>, PropertyContainer<AspectWithMultipleEntities> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntities';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntities();


 public static readonly  TEST_ENTITY_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntities, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntities';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntities',
    'testEntityOne',
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
        'testEntityOne',
    false,
    );




 public static readonly  TEST_ENTITY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntities, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntities';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntities',
    'testEntityTwo',
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
        'testEntityTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithMultipleEntities';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntities .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntities';
}

getProperties(): Array<StaticProperty<AspectWithMultipleEntities, any>> {
return [MetaAspectWithMultipleEntities.TEST_ENTITY_ONE, MetaAspectWithMultipleEntities.TEST_ENTITY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntities, any>> {
        return this.getProperties();
}




    }


