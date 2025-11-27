













import { AspectWithCollectionOfSimpleType,} from './AspectWithCollectionOfSimpleType';
import { DefaultList,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithCollectionOfSimpleType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionOfSimpleType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCollectionOfSimpleType implements StaticMetaClass<AspectWithCollectionOfSimpleType>, PropertyContainer<AspectWithCollectionOfSimpleType> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionOfSimpleType';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionOfSimpleType();


 public static readonly  TEST_LIST = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionOfSimpleType, number, number[]> {

    
    getPropertyType(): string {
            return 'number';
    }

    getContainingType(): string {
        return 'AspectWithCollectionOfSimpleType';
    }

        getContainedType(): string {
            return 'AspectWithCollectionOfSimpleType';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollectionOfSimpleType',
    'testList',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'IntegerList',
'IntegerList',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ))
defaultList.addAspectModelUrn = this.NAMESPACE + 'IntegerList';
 return defaultList; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'35'),
        'testList',
    false,
    );




getModelClass(): string {
return 'AspectWithCollectionOfSimpleType';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionOfSimpleType .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCollectionOfSimpleType';
}

getProperties(): Array<StaticProperty<AspectWithCollectionOfSimpleType, any>> {
return [MetaAspectWithCollectionOfSimpleType.TEST_LIST];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionOfSimpleType, any>> {
        return this.getProperties();
}




    }


