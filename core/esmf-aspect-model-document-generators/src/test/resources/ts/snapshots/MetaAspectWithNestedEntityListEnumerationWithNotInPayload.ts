import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithNestedEntityListEnumerationWithNotInPayload,} from './AspectWithNestedEntityListEnumerationWithNotInPayload';
import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithNestedEntityListEnumerationWithNotInPayload (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityListEnumerationWithNotInPayload).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithNestedEntityListEnumerationWithNotInPayload) : ReplacedAspectArtifact {
        return object.testProperty;
    }

        setValue( object : AspectWithNestedEntityListEnumerationWithNotInPayload, value : ReplacedAspectArtifact ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithNestedEntityListEnumerationWithNotInPayload',
    'testProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultEntityInstance('entityInstance',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })(),
undefined)],(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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


