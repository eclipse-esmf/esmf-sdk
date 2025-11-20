import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityEnumerationAndNotInPayloadProperties,} from './AspectWithEntityEnumerationAndNotInPayloadProperties';
import { DefaultEntity,DefaultEnumeration,} from './aspect-meta-model';
import { DefaultEntityInstance,} from './aspect-meta-model/default-entity-instance';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';




    

/*
* Generated class MetaAspectWithEntityEnumerationAndNotInPayloadProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityEnumerationAndNotInPayloadProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityEnumerationAndNotInPayloadProperties implements StaticMetaClass<AspectWithEntityEnumerationAndNotInPayloadProperties>, PropertyContainer<AspectWithEntityEnumerationAndNotInPayloadProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityEnumerationAndNotInPayloadProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityEnumerationAndNotInPayloadProperties();


 public static readonly  SYSTEM_STATE = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityEnumerationAndNotInPayloadProperties, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityEnumerationAndNotInPayloadProperties';
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
return 'AspectWithEntityEnumerationAndNotInPayloadProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityEnumerationAndNotInPayloadProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityEnumerationAndNotInPayloadProperties';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityEnumerationAndNotInPayloadProperties, any>> {
return [MetaAspectWithEntityEnumerationAndNotInPayloadProperties.SYSTEM_STATE];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityEnumerationAndNotInPayloadProperties, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Aspect with entity enumeration and not in payload properties', 'en'),
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


