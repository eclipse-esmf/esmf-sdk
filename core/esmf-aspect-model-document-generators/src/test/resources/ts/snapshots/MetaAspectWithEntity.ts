import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntity,} from './AspectWithEntity';
import { DefaultEntity,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';

import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';



    

/*
* Generated class MetaAspectWithEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEntity implements StaticMetaClass<AspectWithEntity>, PropertyContainer<AspectWithEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntity();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntity, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntity';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEntity',
    'testProperty',
    (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityCharacteristic',
'EntityCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Test Entity');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'This is a test entity');
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'EntityCharacteristic';
defaultSingleEntity.addPreferredName('en' , 'Test Entity Characteristic');
defaultSingleEntity.addDescription('en' , 'This is a test Entity Characteristic');
defaultSingleEntity.addSeeReference('http:\/\/example.com\/');
 return defaultSingleEntity; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEntity';
}

getProperties(): Array<StaticProperty<AspectWithEntity, any>> {
return [MetaAspectWithEntity.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntity, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This is a test description', language: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


