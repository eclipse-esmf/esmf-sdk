













import { AspectWithCollectionWithSeeAttribute,} from './AspectWithCollectionWithSeeAttribute';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollectionWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionWithSeeAttribute implements StaticMetaClass<AspectWithCollectionWithSeeAttribute>, PropertyContainer<AspectWithCollectionWithSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionWithSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithSeeAttribute, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithSeeAttribute';
    }

        getContainedType(): AspectWithCollectionWithSeeAttribute {
            return 'AspectWithCollectionWithSeeAttribute';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultCollection({
urn : this.NAMESPACE + 'TestCollection',
preferredNames : [ {
value : "Test Collection",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Collection",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" )),Optional.empty())
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithSeeAttribute, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithSeeAttribute';
    }

        getContainedType(): AspectWithCollectionWithSeeAttribute {
            return 'AspectWithCollectionWithSeeAttribute';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyTwo',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultCollection({
urn : this.NAMESPACE + 'TestCollectionTwo',
preferredNames : [ {
value : "Test Collection Two",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Collection Two",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
'http://example.com/me',
 ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" )),Optional.empty())
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyTwo',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCollectionWithSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionWithSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollectionWithSeeAttribute';
}

                        getProperties(): Array<StaticProperty<AspectWithCollectionWithSeeAttribute, any>> {
return [MetaAspectWithCollectionWithSeeAttribute.TEST_PROPERTY, MetaAspectWithCollectionWithSeeAttribute.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionWithSeeAttribute, any>> {
    return this.getProperties();
}




    }


