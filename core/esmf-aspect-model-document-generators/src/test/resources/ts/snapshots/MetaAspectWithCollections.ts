













import { AspectWithCollections,} from './AspectWithCollections';
import { DefaultList,DefaultScalar,DefaultSet,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithCollections (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollections).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCollections implements StaticMetaClass<AspectWithCollections>, PropertyContainer<AspectWithCollections> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollections';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollections();


 public static readonly  SET_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollections, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCollections';
    }

        getContainedType(): string {
            return 'string';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollections',
    'setProperty',
    (() => { const defaultSet = new DefaultSet(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
            return 'number';
    }

    getContainingType(): string {
        return 'AspectWithCollections';
    }

        getContainedType(): string {
            return 'number';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollections',
    'listProperty',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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


