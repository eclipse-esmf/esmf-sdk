













import { AspectWithCollectionWithoutSeeAttribute,} from './AspectWithCollectionWithoutSeeAttribute';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollectionWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionWithoutSeeAttribute implements StaticMetaClass<AspectWithCollectionWithoutSeeAttribute>, PropertyContainer<AspectWithCollectionWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionWithoutSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithoutSeeAttribute, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithoutSeeAttribute';
    }

        getContainedType(): AspectWithCollectionWithoutSeeAttribute {
            return 'AspectWithCollectionWithoutSeeAttribute';
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
see : [  ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" )),Optional.empty())
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCollectionWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollectionWithoutSeeAttribute';
}

                        getProperties(): Array<StaticProperty<AspectWithCollectionWithoutSeeAttribute, any>> {
return [MetaAspectWithCollectionWithoutSeeAttribute.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionWithoutSeeAttribute, any>> {
    return this.getProperties();
}




    }


