import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithRecursivePropertyWithOptionalAndEntityProperty,} from './AspectWithRecursivePropertyWithOptionalAndEntityProperty';
import { DefaultEntity,DefaultSingleEntity,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';




    

/*
* Generated class MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRecursivePropertyWithOptionalAndEntityProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty implements StaticMetaClass<AspectWithRecursivePropertyWithOptionalAndEntityProperty>, PropertyContainer<AspectWithRecursivePropertyWithOptionalAndEntityProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRecursivePropertyWithOptionalAndEntityProperty, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultSingleEntity = new DefaultSingleEntity(null, 
null, 
null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'TestItemCharacteristic';
 return defaultSingleEntity; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRecursivePropertyWithOptionalAndEntityProperty';
}

                        getProperties(): Array<StaticProperty<AspectWithRecursivePropertyWithOptionalAndEntityProperty, any>> {
return [MetaAspectWithRecursivePropertyWithOptionalAndEntityProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRecursivePropertyWithOptionalAndEntityProperty, any>> {
    return this.getProperties();
}




    }


