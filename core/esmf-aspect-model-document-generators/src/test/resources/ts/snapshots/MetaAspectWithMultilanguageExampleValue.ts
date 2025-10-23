













import { AspectWithMultilanguageExampleValue,} from './AspectWithMultilanguageExampleValue';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithMultilanguageExampleValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultilanguageExampleValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultilanguageExampleValue implements StaticMetaClass<AspectWithMultilanguageExampleValue>, PropertyContainer<AspectWithMultilanguageExampleValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultilanguageExampleValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultilanguageExampleValue();


 public static readonly  PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithMultilanguageExampleValue, LangString>{

    
    getPropertyType(): string {
                return 'LangString';
    }

    getContainingType(): string {
        return 'AspectWithMultilanguageExampleValue';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'prop',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#MultiLanguageText',
preferredNames : [ {
value : "Multi-Language Text",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property which contains plain text in multiple languages. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.",
languageTag : 'en',
},
 ],
see : [  ],
},
}
,
    exampleValue : {
metaModelBaseAttributes : {},
value : new LangString("Multilanguage example value.", Locale.forLanguageTag("de")),
type : new DefaultScalar("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'prop',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithMultilanguageExampleValue';
}

getAspectModelUrn(): string {
return MetaAspectWithMultilanguageExampleValue .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultilanguageExampleValue';
}

                        getProperties(): Array<StaticProperty<AspectWithMultilanguageExampleValue, any>> {
return [MetaAspectWithMultilanguageExampleValue.PROP];
}

getAllProperties(): Array<StaticProperty<AspectWithMultilanguageExampleValue, any>> {
    return this.getProperties();
}




    }


