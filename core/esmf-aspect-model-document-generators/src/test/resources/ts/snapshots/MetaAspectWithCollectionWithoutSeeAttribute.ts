













import { AspectWithCollectionWithoutSeeAttribute,} from './AspectWithCollectionWithoutSeeAttribute';
import { DefaultCollection,DefaultScalar,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithCollectionWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCollectionWithoutSeeAttribute implements StaticMetaClass<AspectWithCollectionWithoutSeeAttribute>, PropertyContainer<AspectWithCollectionWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionWithoutSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithoutSeeAttribute, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithoutSeeAttribute';
    }

        getContainedType(): string {
            return 'AspectWithCollectionWithoutSeeAttribute';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollectionWithoutSeeAttribute',
    'testProperty',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestCollection',
'TestCollection',
true, false, undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollection';
defaultCollection.addPreferredName('en' , 'Test Collection');
defaultCollection.addDescription('en' , 'Test Collection');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCollectionWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
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


