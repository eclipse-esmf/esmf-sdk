













import { AspectWithCollectionOfSimpleType,} from './AspectWithCollectionOfSimpleType';
import { DefaultList,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollectionOfSimpleType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionOfSimpleType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionOfSimpleType implements StaticMetaClass<AspectWithCollectionOfSimpleType>, PropertyContainer<AspectWithCollectionOfSimpleType> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionOfSimpleType';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionOfSimpleType();


 public static readonly  TEST_LIST = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionOfSimpleType, number, number[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionOfSimpleType';
    }

        getContainedType(): string {
            return 'AspectWithCollectionOfSimpleType';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultList = new DefaultList(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


