













import { AspectWithCollectionWithElementCharacteristic,} from './AspectWithCollectionWithElementCharacteristic';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollectionWithElementCharacteristic (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithElementCharacteristic).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionWithElementCharacteristic implements StaticMetaClass<AspectWithCollectionWithElementCharacteristic>, PropertyContainer<AspectWithCollectionWithElementCharacteristic> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithElementCharacteristic';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionWithElementCharacteristic();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithElementCharacteristic, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithElementCharacteristic';
    }

        getContainedType(): AspectWithCollectionWithElementCharacteristic {
            return 'AspectWithCollectionWithElementCharacteristic';
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
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" )),Optional.of({
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#Text',
preferredNames : [ {
value : "Text",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.",
languageTag : 'en',
},
 ],
see : [  ],
},
}))
,
    exampleValue : {
metaModelBaseAttributes : {},
value : "Example Value",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCollectionWithElementCharacteristic';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionWithElementCharacteristic .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollectionWithElementCharacteristic';
}

                        getProperties(): Array<StaticProperty<AspectWithCollectionWithElementCharacteristic, any>> {
return [MetaAspectWithCollectionWithElementCharacteristic.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionWithElementCharacteristic, any>> {
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


