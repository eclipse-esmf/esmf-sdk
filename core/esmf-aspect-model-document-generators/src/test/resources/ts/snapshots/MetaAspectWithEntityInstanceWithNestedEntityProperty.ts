import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityInstanceWithNestedEntityProperty,} from './AspectWithEntityInstanceWithNestedEntityProperty';
import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithEntityInstanceWithNestedEntityProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityInstanceWithNestedEntityProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEntityInstanceWithNestedEntityProperty implements StaticMetaClass<AspectWithEntityInstanceWithNestedEntityProperty>, PropertyContainer<AspectWithEntityInstanceWithNestedEntityProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityInstanceWithNestedEntityProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityInstanceWithNestedEntityProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityInstanceWithNestedEntityProperty, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityInstanceWithNestedEntityProperty';
    }


    getValue( object : AspectWithEntityInstanceWithNestedEntityProperty) : ReplacedAspectArtifact {
        return object.testProperty;
    }

        setValue( object : AspectWithEntityInstanceWithNestedEntityProperty, value : ReplacedAspectArtifact ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEntityInstanceWithNestedEntityProperty',
    'testProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultEntityInstance('ReplacedAspectArtifactInstance',
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
return 'AspectWithEntityInstanceWithNestedEntityProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityInstanceWithNestedEntityProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEntityInstanceWithNestedEntityProperty';
}

getProperties(): Array<StaticProperty<AspectWithEntityInstanceWithNestedEntityProperty, any>> {
return [MetaAspectWithEntityInstanceWithNestedEntityProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityInstanceWithNestedEntityProperty, any>> {
        return this.getProperties();
}




    }


