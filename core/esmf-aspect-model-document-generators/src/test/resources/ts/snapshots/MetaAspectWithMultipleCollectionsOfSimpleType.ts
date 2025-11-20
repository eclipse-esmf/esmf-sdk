













import { AspectWithMultipleCollectionsOfSimpleType,} from './AspectWithMultipleCollectionsOfSimpleType';
import { DefaultList,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithMultipleCollectionsOfSimpleType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleCollectionsOfSimpleType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleCollectionsOfSimpleType implements StaticMetaClass<AspectWithMultipleCollectionsOfSimpleType>, PropertyContainer<AspectWithMultipleCollectionsOfSimpleType> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleCollectionsOfSimpleType';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleCollectionsOfSimpleType();


 public static readonly  TEST_LIST_INT = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleCollectionsOfSimpleType, number, number[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithMultipleCollectionsOfSimpleType';
    }

        getContainedType(): string {
            return 'AspectWithMultipleCollectionsOfSimpleType';
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
        'testListInt',
    false,
    );




 public static readonly  TEST_LIST_STRING = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleCollectionsOfSimpleType, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithMultipleCollectionsOfSimpleType';
    }

        getContainedType(): string {
            return 'AspectWithMultipleCollectionsOfSimpleType';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultList = new DefaultList(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


