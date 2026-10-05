import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntityCollections,} from './AspectWithMultipleEntityCollections';
import { DefaultEntity,DefaultList,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';

import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';



    

/*
* Generated class MetaAspectWithMultipleEntityCollections (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntityCollections).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultipleEntityCollections implements StaticMetaClass<AspectWithMultipleEntityCollections>, PropertyContainer<AspectWithMultipleEntityCollections> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntityCollections';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntityCollections();


 public static readonly  TEST_LIST_ONE = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleEntityCollections, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntityCollections';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

    getValue( object : AspectWithMultipleEntityCollections) : ReplacedAspectArtifact[] {
        return object.testListOne;
    }

        setValue( object : AspectWithMultipleEntityCollections, value : ReplacedAspectArtifact[] ) {
            object.testListOne = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntityCollections',
    'testListOne',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityList',
'EntityList',
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultList.addAspectModelUrn = this.NAMESPACE + 'EntityList';
 return defaultList; })()
,
    false,
    false,
    undefined,
        'testListOne',
    false,
    );




 public static readonly  TEST_LIST_TWO = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleEntityCollections, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntityCollections';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

    getValue( object : AspectWithMultipleEntityCollections) : ReplacedAspectArtifact[] {
        return object.testListTwo;
    }

        setValue( object : AspectWithMultipleEntityCollections, value : ReplacedAspectArtifact[] ) {
            object.testListTwo = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntityCollections',
    'testListTwo',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityList',
'EntityList',
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultList.addAspectModelUrn = this.NAMESPACE + 'EntityList';
 return defaultList; })()
,
    false,
    false,
    undefined,
        'testListTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithMultipleEntityCollections';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntityCollections .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntityCollections';
}

getProperties(): Array<StaticProperty<AspectWithMultipleEntityCollections, any>> {
return [MetaAspectWithMultipleEntityCollections.TEST_LIST_ONE, MetaAspectWithMultipleEntityCollections.TEST_LIST_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntityCollections, any>> {
        return this.getProperties();
}




    }


