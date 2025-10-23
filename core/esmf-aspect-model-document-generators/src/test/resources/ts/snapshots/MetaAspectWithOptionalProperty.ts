













import { AspectWithOptionalProperty,} from './AspectWithOptionalProperty';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithOptionalProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalProperty implements StaticMetaClass<AspectWithOptionalProperty>, PropertyContainer<AspectWithOptionalProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalProperty, string, string> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalProperty';
    }

        getContainedType(): AspectWithOptionalProperty {
            return 'AspectWithOptionalProperty';
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
    exampleValue : {},
    optional : true,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithOptionalProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithOptionalProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithOptionalProperty';
}

                        getProperties(): Array<StaticProperty<AspectWithOptionalProperty, any>> {
return [MetaAspectWithOptionalProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithOptionalProperty, any>> {
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


