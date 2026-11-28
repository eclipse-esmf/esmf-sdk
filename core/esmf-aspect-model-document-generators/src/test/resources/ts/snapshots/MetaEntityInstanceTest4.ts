import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { EntityInstanceTest4,} from './EntityInstanceTest4';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaEntityInstanceTest4 (urn:samm:org.eclipse.esmf.test:1.0.0#EntityInstanceTest4).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaEntityInstanceTest4 implements StaticMetaClass<EntityInstanceTest4>, PropertyContainer<EntityInstanceTest4> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'EntityInstanceTest4';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaEntityInstanceTest4();


 public static readonly  ASPECT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<EntityInstanceTest4, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'EntityInstanceTest4';
    }


    getValue( object : EntityInstanceTest4) : ReplacedAspectArtifact {
        return object.aspectProperty;
    }

        setValue( object : EntityInstanceTest4, value : ReplacedAspectArtifact ) {
            object.aspectProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'EntityInstanceTest4',
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
return 'EntityInstanceTest4';
}

getAspectModelUrn(): string {
return MetaEntityInstanceTest4 .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'EntityInstanceTest4';
}

getProperties(): Array<StaticProperty<EntityInstanceTest4, any>> {
return [MetaEntityInstanceTest4.ASPECT_PROPERTY];
}

getAllProperties(): Array<StaticProperty<EntityInstanceTest4, any>> {
        return this.getProperties();
}




    }


