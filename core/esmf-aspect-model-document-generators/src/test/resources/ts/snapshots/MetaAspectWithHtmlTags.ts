import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithHtmlTags,} from './AspectWithHtmlTags';
import { DefaultCharacteristic,DefaultEntity,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';




    

/*
* Generated class MetaAspectWithHtmlTags (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithHtmlTags).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithHtmlTags implements StaticMetaClass<AspectWithHtmlTags>, PropertyContainer<AspectWithHtmlTags> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithHtmlTags';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithHtmlTags();


 public static readonly  TEST_ENTITY = 
                
        new (class extends DefaultStaticProperty<AspectWithHtmlTags, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithHtmlTags';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
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
return 'AspectWithHtmlTags';
}

getAspectModelUrn(): string {
return MetaAspectWithHtmlTags .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithHtmlTags';
}

                        getProperties(): Array<StaticProperty<AspectWithHtmlTags, any>> {
return [MetaAspectWithHtmlTags.TEST_ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithHtmlTags, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Aspect With <img src=xss.png onerror=alert(\'Boom!\')> Entity', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('Aspect With <p>inside html tag<\/p> Entity', 'en'),
        ];
        }


    }


