import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityWithoutProperty,} from './AspectWithEntityWithoutProperty';
import { DefaultEntity,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MetaAbstractEntity,} from './MetaAbstractEntity';

import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';



    

/*
* Generated class MetaAspectWithEntityWithoutProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityWithoutProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEntityWithoutProperty implements StaticMetaClass<AspectWithEntityWithoutProperty>, PropertyContainer<AspectWithEntityWithoutProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityWithoutProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityWithoutProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityWithoutProperty, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityWithoutProperty';
    }


    getValue( object : AspectWithEntityWithoutProperty) : ReplacedAspectArtifact {
        return object.testProperty;
    }

        setValue( object : AspectWithEntityWithoutProperty, value : ReplacedAspectArtifact ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEntityWithoutProperty',
    'testProperty',
    (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityCharacteristic',
'EntityCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityAbstractEntity = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaAbstractEntity.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityAbstractEntity.addAspectModelUrn = this.NAMESPACE + 'AbstractEntity';
extendsDefaultEntityAbstractEntity.addPreferredName('en' , 'Abstract test Entity');
extendsDefaultEntityAbstractEntity.addDescription('en' , 'This is an abstract test entity');
 return extendsDefaultEntityAbstractEntity; })())
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
return 'AspectWithEntityWithoutProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityWithoutProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEntityWithoutProperty';
}

getProperties(): Array<StaticProperty<AspectWithEntityWithoutProperty, any>> {
return [MetaAspectWithEntityWithoutProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityWithoutProperty, any>> {
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


