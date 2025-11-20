import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithAbstractEntity,} from './AspectWithAbstractEntity';
import { DefaultEntity,DefaultSingleEntity,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';

import { LangString,} from './core/langString';




    

/*
* Generated class MetaAspectWithAbstractEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAbstractEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithAbstractEntity implements StaticMetaClass<AspectWithAbstractEntity>, PropertyContainer<AspectWithAbstractEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithAbstractEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithAbstractEntity();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithAbstractEntity, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithAbstractEntity';
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
return 'AspectWithAbstractEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithAbstractEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithAbstractEntity';
}

                        getProperties(): Array<StaticProperty<AspectWithAbstractEntity, any>> {
return [MetaAspectWithAbstractEntity.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithAbstractEntity, any>> {
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


