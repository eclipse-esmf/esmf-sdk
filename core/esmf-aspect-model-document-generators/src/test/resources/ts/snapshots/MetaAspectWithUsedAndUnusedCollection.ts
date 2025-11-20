













import { AspectWithUsedAndUnusedCollection,} from './AspectWithUsedAndUnusedCollection';
import { DefaultCollection,DefaultScalar,} from './aspect-meta-model';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithUsedAndUnusedCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithUsedAndUnusedCollection implements StaticMetaClass<AspectWithUsedAndUnusedCollection>, PropertyContainer<AspectWithUsedAndUnusedCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUsedAndUnusedCollection();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithUsedAndUnusedCollection, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithUsedAndUnusedCollection';
    }

        getContainedType(): string {
            return 'AspectWithUsedAndUnusedCollection';
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
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'UsedTestCollection';
defaultCollection.addPreferredName('en' , 'Used Test Collection');
defaultCollection.addDescription('en' , 'Used Test Collection');
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
return 'AspectWithUsedAndUnusedCollection';
}

getAspectModelUrn(): string {
return MetaAspectWithUsedAndUnusedCollection .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithUsedAndUnusedCollection';
}

                        getProperties(): Array<StaticProperty<AspectWithUsedAndUnusedCollection, any>> {
return [MetaAspectWithUsedAndUnusedCollection.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUsedAndUnusedCollection, any>> {
    return this.getProperties();
}




    }


