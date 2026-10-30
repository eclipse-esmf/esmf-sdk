import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityList,} from './AspectWithEntityList';
import { DefaultEntity,DefaultList,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';

import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';



    

/*
* Generated class MetaAspectWithEntityList (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityList).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEntityList implements StaticMetaClass<AspectWithEntityList>, PropertyContainer<AspectWithEntityList> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityList';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityList();


 public static readonly  TEST_LIST = 
                
        new (class extends StaticContainerProperty<AspectWithEntityList, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityList';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

    getValue( object : AspectWithEntityList) : ReplacedAspectArtifact[] {
        return object.testList;
    }

        setValue( object : AspectWithEntityList, value : ReplacedAspectArtifact[] ) {
            object.testList = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEntityList',
    'testList',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultList.isAnonymousNode = true;
 return defaultList; })()
,
    false,
    false,
    undefined,
        'testList',
    false,
    );




getModelClass(): string {
return 'AspectWithEntityList';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityList .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEntityList';
}

getProperties(): Array<StaticProperty<AspectWithEntityList, any>> {
return [MetaAspectWithEntityList.TEST_LIST];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityList, any>> {
        return this.getProperties();
}




    }


