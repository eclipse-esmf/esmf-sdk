import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { EntityInstanceTest3,} from './EntityInstanceTest3';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaEntityInstanceTest3 (urn:samm:org.eclipse.esmf.test:1.0.0#EntityInstanceTest3).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaEntityInstanceTest3 implements StaticMetaClass<EntityInstanceTest3>, PropertyContainer<EntityInstanceTest3> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'EntityInstanceTest3';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaEntityInstanceTest3();


 public static readonly  ASPECT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<EntityInstanceTest3, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'EntityInstanceTest3';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'EntityInstanceTest3',
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
return 'EntityInstanceTest3';
}

getAspectModelUrn(): string {
return MetaEntityInstanceTest3 .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'EntityInstanceTest3';
}

getProperties(): Array<StaticProperty<EntityInstanceTest3, any>> {
return [MetaEntityInstanceTest3.ASPECT_PROPERTY];
}

getAllProperties(): Array<StaticProperty<EntityInstanceTest3, any>> {
        return this.getProperties();
}




    }


