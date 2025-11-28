import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityEnumerationAndNotInPayloadProperties,} from './AspectWithEntityEnumerationAndNotInPayloadProperties';
import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';

import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';



    

/*
* Generated class MetaAspectWithEntityEnumerationAndNotInPayloadProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityEnumerationAndNotInPayloadProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithEntityEnumerationAndNotInPayloadProperties) : ReplacedAspectArtifact {
        return object.systemState;
    }

        setValue( object : AspectWithEntityEnumerationAndNotInPayloadProperties, value : ReplacedAspectArtifact ) {
            object.systemState = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEntityEnumerationAndNotInPayloadProperties',
    'systemState',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultEntityInstance('CoolDown',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance('HeatUp',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance('Off',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance('On',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'System State');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Represents a specific state the system may have.');
 return defaultEntityReplacedAspectArtifact; })(),
undefined)],(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Aspect with entity enumeration and not in payload properties', language: 'en'},
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


