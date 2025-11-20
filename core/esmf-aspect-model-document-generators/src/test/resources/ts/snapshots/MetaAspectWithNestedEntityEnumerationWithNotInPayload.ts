import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithNestedEntityEnumerationWithNotInPayload,} from './AspectWithNestedEntityEnumerationWithNotInPayload';
import { DefaultEntity,DefaultEnumeration,} from './aspect-meta-model';
import { DefaultEntityInstance,} from './aspect-meta-model/default-entity-instance';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';




    

/*
* Generated class MetaAspectWithNestedEntityEnumerationWithNotInPayload (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityEnumerationWithNotInPayload).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithNestedEntityEnumerationWithNotInPayload implements StaticMetaClass<AspectWithNestedEntityEnumerationWithNotInPayload>, PropertyContainer<AspectWithNestedEntityEnumerationWithNotInPayload> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNestedEntityEnumerationWithNotInPayload';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithNestedEntityEnumerationWithNotInPayload();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithNestedEntityEnumerationWithNotInPayload, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithNestedEntityEnumerationWithNotInPayload';
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
return 'AspectWithNestedEntityEnumerationWithNotInPayload';
}

getAspectModelUrn(): string {
return MetaAspectWithNestedEntityEnumerationWithNotInPayload .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithNestedEntityEnumerationWithNotInPayload';
}

                        getProperties(): Array<StaticProperty<AspectWithNestedEntityEnumerationWithNotInPayload, any>> {
return [MetaAspectWithNestedEntityEnumerationWithNotInPayload.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithNestedEntityEnumerationWithNotInPayload, any>> {
    return this.getProperties();
}




    }


