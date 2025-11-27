import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithScriptTags,} from './AspectWithScriptTags';
import { DefaultCharacteristic,DefaultEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';

import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';



    

/*
* Generated class MetaAspectWithScriptTags (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithScriptTags).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithScriptTags implements StaticMetaClass<AspectWithScriptTags>, PropertyContainer<AspectWithScriptTags> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithScriptTags';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithScriptTags();


 public static readonly  TEST_ENTITY = 
                
        new (class extends DefaultStaticProperty<AspectWithScriptTags, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithScriptTags';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithScriptTags',
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
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Test preferred name with script: <script>alert(\'Should not be alerted\');<\/script>');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Test description with script: <script>alert(\'Should not be alerted\');<\/script>');
 return defaultEntityReplacedAspectArtifact; })())
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic';
defaultCharacteristic.addPreferredName('en' , 'Test preferred name with script: <script>alert(\'Should not be alerted\');<\/script>');
defaultCharacteristic.addDescription('en' , 'Test description with script: <script>alert(\'Should not be alerted\');<\/script>');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testEntity',
    false,
    );




getModelClass(): string {
return 'AspectWithScriptTags';
}

getAspectModelUrn(): string {
return MetaAspectWithScriptTags .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithScriptTags';
}

getProperties(): Array<StaticProperty<AspectWithScriptTags, any>> {
return [MetaAspectWithScriptTags.TEST_ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithScriptTags, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Aspect With Entity', language: 'en'},
        ];
        }



    }


