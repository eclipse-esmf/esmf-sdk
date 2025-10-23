













import { AspectWithListAndElementCharacteristic,} from './AspectWithListAndElementCharacteristic';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithListAndElementCharacteristic (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithListAndElementCharacteristic).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithListAndElementCharacteristic implements StaticMetaClass<AspectWithListAndElementCharacteristic>, PropertyContainer<AspectWithListAndElementCharacteristic> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithListAndElementCharacteristic';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithListAndElementCharacteristic();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithListAndElementCharacteristic, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithListAndElementCharacteristic';
    }

        getContainedType(): AspectWithListAndElementCharacteristic {
            return 'AspectWithListAndElementCharacteristic';
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
    characteristic :     new DefaultList({
urn : this.NAMESPACE + 'TestList',
preferredNames : [ {
value : "Test List",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test list.",
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
return 'AspectWithListAndElementCharacteristic';
}

getAspectModelUrn(): string {
return MetaAspectWithListAndElementCharacteristic .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithListAndElementCharacteristic';
}

                        getProperties(): Array<StaticProperty<AspectWithListAndElementCharacteristic, any>> {
return [MetaAspectWithListAndElementCharacteristic.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithListAndElementCharacteristic, any>> {
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


