













import { AspectWithCollections,} from './AspectWithCollections';
import { DefaultList,DefaultScalar,DefaultSet,} from './aspect-meta-model';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollections (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollections).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollections implements StaticMetaClass<AspectWithCollections>, PropertyContainer<AspectWithCollections> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollections';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollections();


 public static readonly  SET_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollections, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollections';
    }

        getContainedType(): string {
            return 'AspectWithCollections';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultSet = new DefaultSet(null, 
null, 
null, 
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultSet.isAnonymousNode = true;
 return defaultSet; })()
,
    false,
    false,
    undefined,
        'setProperty',
    false,
    );




 public static readonly  LIST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollections, number, number[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollections';
    }

        getContainedType(): string {
            return 'AspectWithCollections';
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
defaultList.isAnonymousNode = true;
 return defaultList; })()
,
    false,
    false,
    undefined,
        'listProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCollections';
}

getAspectModelUrn(): string {
return MetaAspectWithCollections .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollections';
}

                        getProperties(): Array<StaticProperty<AspectWithCollections, any>> {
return [MetaAspectWithCollections.SET_PROPERTY, MetaAspectWithCollections.LIST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCollections, any>> {
    return this.getProperties();
}




    }


