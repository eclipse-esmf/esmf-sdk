import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityCollection,} from './AspectWithEntityCollection';
import { LangString,} from './core/langString';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithEntityCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityCollection implements StaticMetaClass<AspectWithEntityCollection>, PropertyContainer<AspectWithEntityCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityCollection();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithEntityCollection, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithEntityCollection';
    }

        getContainedType(): AspectWithEntityCollection {
            return 'AspectWithEntityCollection';
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
    characteristic :     new DefaultCollection({
urn : this.NAMESPACE + 'TestCollection',
preferredNames : [ {
value : "Test Collection",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test collection.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},Optional.of(DefaultEntity.createDefaultEntity({
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
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty())),Optional.empty())
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEntityCollection';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityCollection .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityCollection';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityCollection, any>> {
return [MetaAspectWithEntityCollection.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityCollection, any>> {
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


