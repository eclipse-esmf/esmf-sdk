import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntityCollections,} from './AspectWithMultipleEntityCollections';
import { DefaultEntity,DefaultList,} from './aspect-meta-model';

import { StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithMultipleEntityCollections (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntityCollections).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleEntityCollections implements StaticMetaClass<AspectWithMultipleEntityCollections>, PropertyContainer<AspectWithMultipleEntityCollections> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntityCollections';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntityCollections();


 public static readonly  TEST_LIST_ONE = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleEntityCollections, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntityCollections';
    }

        getContainedType(): string {
            return 'AspectWithMultipleEntityCollections';
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
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntityCollections';
    }

        getContainedType(): string {
            return 'AspectWithMultipleEntityCollections';
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
return KnownVersionUtils.getLatest()
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


