













import { AspectWithEnglishAndGermanDescription,} from './AspectWithEnglishAndGermanDescription';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithEnglishAndGermanDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnglishAndGermanDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnglishAndGermanDescription implements StaticMetaClass<AspectWithEnglishAndGermanDescription>, PropertyContainer<AspectWithEnglishAndGermanDescription> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnglishAndGermanDescription';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnglishAndGermanDescription();


 public static readonly  TEST_STRING = 
                
        new (class extends DefaultStaticProperty<AspectWithEnglishAndGermanDescription, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithEnglishAndGermanDescription';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testString',
preferredNames : [ {
value : "testString",
languageTag : 'de',
},
{
value : "testString",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Es ist ein Test-String",
languageTag : 'de',
},
{
value : "This is a test string",
languageTag : 'en',
},
 ],
see : [  ],
},
    characteristic :     {
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
}
,
    exampleValue : {
metaModelBaseAttributes : {},
value : "Example Value Test",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testString',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEnglishAndGermanDescription';
}

getAspectModelUrn(): string {
return MetaAspectWithEnglishAndGermanDescription .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEnglishAndGermanDescription';
}

                        getProperties(): Array<StaticProperty<AspectWithEnglishAndGermanDescription, any>> {
return [MetaAspectWithEnglishAndGermanDescription.TEST_STRING];
}

getAllProperties(): Array<StaticProperty<AspectWithEnglishAndGermanDescription, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Testaspekt', languageTag: 'de'},
            {value: 'Test Aspect', languageTag: 'en'},
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            {value: 'Aspekt mit mehrsprachigen Beschreibungen', languageTag: 'de'},
            {value: 'Aspect With Multilingual Descriptions', languageTag: 'en'},
        ];
        }


    }


