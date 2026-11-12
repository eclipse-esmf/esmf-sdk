import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { EntityInstanceTest1,} from './EntityInstanceTest1';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaEntityInstanceTest1 (urn:samm:org.eclipse.esmf.test:1.0.0#EntityInstanceTest1).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaEntityInstanceTest1 implements StaticMetaClass<EntityInstanceTest1>, PropertyContainer<EntityInstanceTest1> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'EntityInstanceTest1';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaEntityInstanceTest1();


 public static readonly  ASPECT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<EntityInstanceTest1, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'EntityInstanceTest1';
    }


    getValue( object : EntityInstanceTest1) : ReplacedAspectArtifact {
        return object.aspectProperty;
    }

        setValue( object : EntityInstanceTest1, value : ReplacedAspectArtifact ) {
            object.aspectProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'EntityInstanceTest1',
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
return 'EntityInstanceTest1';
}

getAspectModelUrn(): string {
return MetaEntityInstanceTest1 .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'EntityInstanceTest1';
}

getProperties(): Array<StaticProperty<EntityInstanceTest1, any>> {
return [MetaEntityInstanceTest1.ASPECT_PROPERTY];
}

getAllProperties(): Array<StaticProperty<EntityInstanceTest1, any>> {
        return this.getProperties();
}




    }


