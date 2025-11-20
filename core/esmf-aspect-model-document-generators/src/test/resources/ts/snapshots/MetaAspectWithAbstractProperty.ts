import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithAbstractProperty,} from './AspectWithAbstractProperty';
import { DefaultEntity,DefaultSingleEntity,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';

import { LangString,} from './core/langString';




    

/*
* Generated class MetaAspectWithAbstractProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAbstractProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithAbstractProperty implements StaticMetaClass<AspectWithAbstractProperty>, PropertyContainer<AspectWithAbstractProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithAbstractProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithAbstractProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithAbstractProperty, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithAbstractProperty';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultSingleEntity = new DefaultSingleEntity(null, 
null, 
null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
extendsDefaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Abstract Test Entity');
extendsDefaultEntityReplacedAspectArtifact.addDescription('en' , 'This is a abstract test entity');
 return extendsDefaultEntityReplacedAspectArtifact; })())
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Test Entity');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'This is a test entity');
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'EntityCharacteristic';
defaultSingleEntity.addPreferredName('en' , 'Test Entity Characteristic');
defaultSingleEntity.addDescription('en' , 'This is a test entity');
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
return 'AspectWithAbstractProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithAbstractProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithAbstractProperty';
}

                        getProperties(): Array<StaticProperty<AspectWithAbstractProperty, any>> {
return [MetaAspectWithAbstractProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithAbstractProperty, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Test Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('This is a test description', 'en'),
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


