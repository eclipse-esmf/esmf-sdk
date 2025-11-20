













import { AspectWithCollectionWithMultipleSeeAttributes,} from './AspectWithCollectionWithMultipleSeeAttributes';
import { DefaultCollection,DefaultScalar,} from './aspect-meta-model';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollectionWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionWithMultipleSeeAttributes implements StaticMetaClass<AspectWithCollectionWithMultipleSeeAttributes>, PropertyContainer<AspectWithCollectionWithMultipleSeeAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithMultipleSeeAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionWithMultipleSeeAttributes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithMultipleSeeAttributes, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithMultipleSeeAttributes';
    }

        getContainedType(): string {
            return 'AspectWithCollectionWithMultipleSeeAttributes';
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
defaultCollection.addSeeReference('http:\/\/example.com\/me');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCollectionWithMultipleSeeAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionWithMultipleSeeAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollectionWithMultipleSeeAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithCollectionWithMultipleSeeAttributes, any>> {
return [MetaAspectWithCollectionWithMultipleSeeAttributes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionWithMultipleSeeAttributes, any>> {
    return this.getProperties();
}




    }


