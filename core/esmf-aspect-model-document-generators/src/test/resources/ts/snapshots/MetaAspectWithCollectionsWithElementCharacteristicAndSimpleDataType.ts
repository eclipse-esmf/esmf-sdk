













import { AspectWithCollectionsWithElementCharacteristicAndSimpleDataType,} from './AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
import { DefaultCharacteristic,DefaultCollection,DefaultScalar,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionsWithElementCharacteristicAndSimpleDataType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType implements StaticMetaClass<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType>, PropertyContainer<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
    }

        getContainedType(): string {
            return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType',
    'testProperty',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestCollection',
'TestCollection',
true, false, undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollection';
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
    }

        getContainedType(): string {
            return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType',
    'testPropertyTwo',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestCollectionTwo',
'TestCollectionTwo',
true, false, (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollectionTwo';
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'testPropertyTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCollectionsWithElementCharacteristicAndSimpleDataType';
}

getProperties(): Array<StaticProperty<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, any>> {
return [MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType.TEST_PROPERTY, MetaAspectWithCollectionsWithElementCharacteristicAndSimpleDataType.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionsWithElementCharacteristicAndSimpleDataType, any>> {
        return this.getProperties();
}




    }


