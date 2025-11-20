import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityEnumerationWithNotExistingEnum,} from './AspectWithEntityEnumerationWithNotExistingEnum';
import { DefaultEntity,DefaultEnumeration,} from './aspect-meta-model';
import { DefaultEntityInstance,} from './aspect-meta-model/default-entity-instance';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';




    

/*
* Generated class MetaAspectWithEntityEnumerationWithNotExistingEnum (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityEnumerationWithNotExistingEnum).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityEnumerationWithNotExistingEnum implements StaticMetaClass<AspectWithEntityEnumerationWithNotExistingEnum>, PropertyContainer<AspectWithEntityEnumerationWithNotExistingEnum> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityEnumerationWithNotExistingEnum';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityEnumerationWithNotExistingEnum();


 public static readonly  SYSTEM_STATE = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityEnumerationWithNotExistingEnum, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityEnumerationWithNotExistingEnum';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultEnumeration = new DefaultEnumeration(null, 
null, 
null, 
[new DefaultEntityInstance(null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance(null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance(null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance(null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })(),
undefined)],(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })())
defaultEnumeration.isAnonymousNode = true;
defaultEnumeration.addPreferredName('en' , 'System States');
defaultEnumeration.addDescription('en' , 'Defines which states the system may have.');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'systemState',
    false,
    );




getModelClass(): string {
return 'AspectWithEntityEnumerationWithNotExistingEnum';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityEnumerationWithNotExistingEnum .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityEnumerationWithNotExistingEnum';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityEnumerationWithNotExistingEnum, any>> {
return [MetaAspectWithEntityEnumerationWithNotExistingEnum.SYSTEM_STATE];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityEnumerationWithNotExistingEnum, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Aspect with entity enumeration', 'en'),
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


