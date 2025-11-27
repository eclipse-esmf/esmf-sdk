import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { EntityInstanceTest2,} from './EntityInstanceTest2';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaEntityInstanceTest2 (urn:samm:org.eclipse.esmf.test:1.0.0#EntityInstanceTest2).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaEntityInstanceTest2 implements StaticMetaClass<EntityInstanceTest2>, PropertyContainer<EntityInstanceTest2> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'EntityInstanceTest2';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaEntityInstanceTest2();


 public static readonly  ASPECT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<EntityInstanceTest2, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'EntityInstanceTest2';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'EntityInstanceTest2',
    'aspectProperty',
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
        'aspectProperty',
    false,
    );




getModelClass(): string {
return 'EntityInstanceTest2';
}

getAspectModelUrn(): string {
return MetaEntityInstanceTest2 .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'EntityInstanceTest2';
}

getProperties(): Array<StaticProperty<EntityInstanceTest2, any>> {
return [MetaEntityInstanceTest2.ASPECT_PROPERTY];
}

getAllProperties(): Array<StaticProperty<EntityInstanceTest2, any>> {
        return this.getProperties();
}




    }


