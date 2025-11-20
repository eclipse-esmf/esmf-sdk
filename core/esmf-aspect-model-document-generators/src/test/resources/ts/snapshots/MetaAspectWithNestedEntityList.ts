import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithNestedEntityList,} from './AspectWithNestedEntityList';
import { DefaultEntity,DefaultList,} from './aspect-meta-model';

import { StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithNestedEntityList (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityList).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithNestedEntityList implements StaticMetaClass<AspectWithNestedEntityList>, PropertyContainer<AspectWithNestedEntityList> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNestedEntityList';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithNestedEntityList();


 public static readonly  TEST_LIST = 
                
        new (class extends StaticContainerProperty<AspectWithNestedEntityList, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithNestedEntityList';
    }

        getContainedType(): string {
            return 'AspectWithNestedEntityList';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultList = new DefaultList(null, 
null, 
null, 
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


