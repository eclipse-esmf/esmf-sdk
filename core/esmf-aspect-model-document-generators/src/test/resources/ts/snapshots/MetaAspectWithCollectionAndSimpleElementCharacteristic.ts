













import { AspectWithCollectionAndSimpleElementCharacteristic,} from './AspectWithCollectionAndSimpleElementCharacteristic';
import { DefaultCharacteristic,DefaultCollection,DefaultScalar,} from './aspect-meta-model';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollectionAndSimpleElementCharacteristic (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionAndSimpleElementCharacteristic).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionAndSimpleElementCharacteristic implements StaticMetaClass<AspectWithCollectionAndSimpleElementCharacteristic>, PropertyContainer<AspectWithCollectionAndSimpleElementCharacteristic> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionAndSimpleElementCharacteristic';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionAndSimpleElementCharacteristic();


 public static readonly  ITEMS = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionAndSimpleElementCharacteristic, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionAndSimpleElementCharacteristic';
    }

        getContainedType(): string {
            return 'AspectWithCollectionAndSimpleElementCharacteristic';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultCollection = new DefaultCollection(null, 
null, 
null, 
true, false, (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.isAnonymousNode = true;
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'items',
    false,
    );




getModelClass(): string {
return 'AspectWithCollectionAndSimpleElementCharacteristic';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionAndSimpleElementCharacteristic .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollectionAndSimpleElementCharacteristic';
}

                        getProperties(): Array<StaticProperty<AspectWithCollectionAndSimpleElementCharacteristic, any>> {
return [MetaAspectWithCollectionAndSimpleElementCharacteristic.ITEMS];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionAndSimpleElementCharacteristic, any>> {
    return this.getProperties();
}




    }


