import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithNestedEntityList,} from './AspectWithNestedEntityList';
import { DefaultEntity,DefaultList,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';

import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';



    

/*
* Generated class MetaAspectWithNestedEntityList (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityList).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithNestedEntityList implements StaticMetaClass<AspectWithNestedEntityList>, PropertyContainer<AspectWithNestedEntityList> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNestedEntityList';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithNestedEntityList();


 public static readonly  TEST_LIST = 
                
        new (class extends StaticContainerProperty<AspectWithNestedEntityList, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithNestedEntityList';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

    getValue( object : AspectWithNestedEntityList) : ReplacedAspectArtifact[] {
        return object.testList;
    }

        setValue( object : AspectWithNestedEntityList, value : ReplacedAspectArtifact[] ) {
            object.testList = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithNestedEntityList',
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
return 'AspectWithNestedEntityList';
}

getAspectModelUrn(): string {
return MetaAspectWithNestedEntityList .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithNestedEntityList';
}

getProperties(): Array<StaticProperty<AspectWithNestedEntityList, any>> {
return [MetaAspectWithNestedEntityList.TEST_LIST];
}

getAllProperties(): Array<StaticProperty<AspectWithNestedEntityList, any>> {
        return this.getProperties();
}




    }


