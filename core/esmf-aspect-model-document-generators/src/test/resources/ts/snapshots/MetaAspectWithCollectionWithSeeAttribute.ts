













import { AspectWithCollectionWithSeeAttribute,} from './AspectWithCollectionWithSeeAttribute';
import { DefaultCollection,DefaultScalar,} from './aspect-meta-model';
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

        getContainedType(): string {
            return 'AspectWithCollectionWithSeeAttribute';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultCollection = new DefaultCollection(null, 
null, 
null, 
true, false, undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollection';
defaultCollection.addPreferredName('en' , 'Test Collection');
defaultCollection.addDescription('en' , 'Test Collection');
defaultCollection.addSeeReference('http:\/\/example.com\/');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithSeeAttribute, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithSeeAttribute';
    }

        getContainedType(): string {
            return 'AspectWithCollectionWithSeeAttribute';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultCollection = new DefaultCollection(null, 
null, 
null, 
true, false, undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollectionTwo';
defaultCollection.addPreferredName('en' , 'Test Collection Two');
defaultCollection.addDescription('en' , 'Test Collection Two');
defaultCollection.addSeeReference('http:\/\/example.com\/');
defaultCollection.addSeeReference('http:\/\/example.com\/me');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'testPropertyTwo',
    false,
    );




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


