import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithNestedEntityListEnumerationWithNotInPayload,} from './AspectWithNestedEntityListEnumerationWithNotInPayload';
import { DefaultEntity,DefaultEnumeration,} from './aspect-meta-model';
import { DefaultEntityInstance,} from './aspect-meta-model/default-entity-instance';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';




    

/*
* Generated class MetaAspectWithNestedEntityListEnumerationWithNotInPayload (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityListEnumerationWithNotInPayload).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithNestedEntityListEnumerationWithNotInPayload implements StaticMetaClass<AspectWithNestedEntityListEnumerationWithNotInPayload>, PropertyContainer<AspectWithNestedEntityListEnumerationWithNotInPayload> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNestedEntityListEnumerationWithNotInPayload';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithNestedEntityListEnumerationWithNotInPayload();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithNestedEntityListEnumerationWithNotInPayload, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithNestedEntityListEnumerationWithNotInPayload';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultEnumeration = new DefaultEnumeration(null, 
null, 
null, 
[new DefaultEntityInstance(null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })(),
undefined)],(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithNestedEntityListEnumerationWithNotInPayload';
}

getAspectModelUrn(): string {
return MetaAspectWithNestedEntityListEnumerationWithNotInPayload .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithNestedEntityListEnumerationWithNotInPayload';
}

                        getProperties(): Array<StaticProperty<AspectWithNestedEntityListEnumerationWithNotInPayload, any>> {
return [MetaAspectWithNestedEntityListEnumerationWithNotInPayload.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithNestedEntityListEnumerationWithNotInPayload, any>> {
    return this.getProperties();
}




    }


