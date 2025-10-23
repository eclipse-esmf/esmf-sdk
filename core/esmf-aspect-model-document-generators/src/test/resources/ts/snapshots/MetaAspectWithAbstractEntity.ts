import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithAbstractEntity,} from './AspectWithAbstractEntity';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';

import { LangString,} from './core/langString';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';


    

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
value : "This is a test Entity Characteristic",
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


