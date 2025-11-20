import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityList,} from './AspectWithEntityList';
import { DefaultEntity,DefaultList,} from './aspect-meta-model';

import { StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithEntityList (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityList).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityList implements StaticMetaClass<AspectWithEntityList>, PropertyContainer<AspectWithEntityList> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityList';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityList();


 public static readonly  TEST_LIST = 
                
        new (class extends StaticContainerProperty<AspectWithEntityList, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithEntityList';
    }

        getContainedType(): string {
            return 'AspectWithEntityList';
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
return 'AspectWithEntityList';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityList .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
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


