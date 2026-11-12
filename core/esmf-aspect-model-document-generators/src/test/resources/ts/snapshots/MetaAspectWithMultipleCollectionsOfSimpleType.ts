













import { AspectWithMultipleCollectionsOfSimpleType,} from './AspectWithMultipleCollectionsOfSimpleType';
import { DefaultList,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithMultipleCollectionsOfSimpleType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleCollectionsOfSimpleType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultipleCollectionsOfSimpleType implements StaticMetaClass<AspectWithMultipleCollectionsOfSimpleType>, PropertyContainer<AspectWithMultipleCollectionsOfSimpleType> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleCollectionsOfSimpleType';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleCollectionsOfSimpleType();


 public static readonly  TEST_LIST_INT = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleCollectionsOfSimpleType, number, number[]> {

    
    getPropertyType(): string {
            return 'number';
    }

    getContainingType(): string {
        return 'AspectWithMultipleCollectionsOfSimpleType';
    }

        getContainedType(): string {
            return 'number';
        }

    getValue( object : AspectWithMultipleCollectionsOfSimpleType) : number[] {
        return object.testListInt;
    }

        setValue( object : AspectWithMultipleCollectionsOfSimpleType, value : number[] ) {
            object.testListInt = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleCollectionsOfSimpleType',
    'testListInt',
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
        'testListInt',
    false,
    );




 public static readonly  TEST_LIST_STRING = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleCollectionsOfSimpleType, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithMultipleCollectionsOfSimpleType';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithMultipleCollectionsOfSimpleType) : string[] {
        return object.testListString;
    }

        setValue( object : AspectWithMultipleCollectionsOfSimpleType, value : string[] ) {
            object.testListString = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleCollectionsOfSimpleType',
    'testListString',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StringList',
'StringList',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultList.addAspectModelUrn = this.NAMESPACE + 'StringList';
 return defaultList; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'test string'),
        'testListString',
    false,
    );




getModelClass(): string {
return 'AspectWithMultipleCollectionsOfSimpleType';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleCollectionsOfSimpleType .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultipleCollectionsOfSimpleType';
}

getProperties(): Array<StaticProperty<AspectWithMultipleCollectionsOfSimpleType, any>> {
return [MetaAspectWithMultipleCollectionsOfSimpleType.TEST_LIST_INT, MetaAspectWithMultipleCollectionsOfSimpleType.TEST_LIST_STRING];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleCollectionsOfSimpleType, any>> {
        return this.getProperties();
}




    }


