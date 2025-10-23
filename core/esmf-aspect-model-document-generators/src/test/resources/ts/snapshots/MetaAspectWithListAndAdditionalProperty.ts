













import { AspectWithListAndAdditionalProperty,} from './AspectWithListAndAdditionalProperty';
import { DefaultStaticProperty,StaticContainerProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithListAndAdditionalProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithListAndAdditionalProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithListAndAdditionalProperty implements StaticMetaClass<AspectWithListAndAdditionalProperty>, PropertyContainer<AspectWithListAndAdditionalProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithListAndAdditionalProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithListAndAdditionalProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithListAndAdditionalProperty, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithListAndAdditionalProperty';
    }

        getContainedType(): AspectWithListAndAdditionalProperty {
            return 'AspectWithListAndAdditionalProperty';
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
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" )),Optional.empty())
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




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithListAndAdditionalProperty, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithListAndAdditionalProperty';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyTwo',
preferredNames : [ {
value : "Test Property Two",
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
    exampleValue : {
metaModelBaseAttributes : {},
value : "Example Value",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyTwo',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithListAndAdditionalProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithListAndAdditionalProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithListAndAdditionalProperty';
}

                        getProperties(): Array<StaticProperty<AspectWithListAndAdditionalProperty, any>> {
return [MetaAspectWithListAndAdditionalProperty.TEST_PROPERTY, MetaAspectWithListAndAdditionalProperty.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithListAndAdditionalProperty, any>> {
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


