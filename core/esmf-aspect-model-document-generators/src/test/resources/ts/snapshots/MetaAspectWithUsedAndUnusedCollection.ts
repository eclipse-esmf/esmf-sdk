













import { AspectWithUsedAndUnusedCollection,} from './AspectWithUsedAndUnusedCollection';
import { DefaultCollection,DefaultScalar,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithUsedAndUnusedCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithUsedAndUnusedCollection implements StaticMetaClass<AspectWithUsedAndUnusedCollection>, PropertyContainer<AspectWithUsedAndUnusedCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUsedAndUnusedCollection();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithUsedAndUnusedCollection, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithUsedAndUnusedCollection';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithUsedAndUnusedCollection) : string[] {
        return object.testProperty;
    }

        setValue( object : AspectWithUsedAndUnusedCollection, value : string[] ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithUsedAndUnusedCollection',
    'testProperty',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'UsedTestCollection',
'UsedTestCollection',
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
return KnownVersion.getLatest()
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


