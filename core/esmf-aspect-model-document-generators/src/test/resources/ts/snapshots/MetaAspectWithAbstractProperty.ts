import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithAbstractProperty,} from './AspectWithAbstractProperty';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';

import { LangString,} from './core/langString';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';


    

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
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [ {
value : "Test Property",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test property.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
'http://example.com/me',
 ],
},
    characteristic :     new DefaultSingleEntity({
urn : this.NAMESPACE + 'EntityCharacteristic',
preferredNames : [ {
value : "Test Entity Characteristic",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test entity",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
}, DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "Test Entity",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test entity",
languageTag : 'en',
},
 ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "Abstract Test Entity",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a abstract test entity",
languageTag : 'en',
},
 ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty(),List.of(AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#ReplacedAspectArtifact" ))))))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




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
            {value: 'Test Aspect', languageTag: 'en'},
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            {value: 'This is a test description', languageTag: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            "http://example.com/",
        ];
        }

    }


