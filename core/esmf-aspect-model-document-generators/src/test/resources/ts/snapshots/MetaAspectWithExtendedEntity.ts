import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithExtendedEntity,} from './AspectWithExtendedEntity';
import { DefaultEntity,DefaultSortedSet,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';



import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';



    

/*
* Generated class MetaAspectWithExtendedEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExtendedEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithExtendedEntity implements StaticMetaClass<AspectWithExtendedEntity>, PropertyContainer<AspectWithExtendedEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithExtendedEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithExtendedEntity();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithExtendedEntity, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithExtendedEntity';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

    getValue( object : AspectWithExtendedEntity) : ReplacedAspectArtifact[] {
        return object.testProperty;
    }

        setValue( object : AspectWithExtendedEntity, value : ReplacedAspectArtifact[] ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithExtendedEntity',
    'testProperty',
    (() => { const defaultSortedSet = new DefaultSortedSet(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return extendsDefaultEntityReplacedAspectArtifact; })())
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return extendsDefaultEntityReplacedAspectArtifact; })())
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultSortedSet.isAnonymousNode = true;
 return defaultSortedSet; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithExtendedEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithExtendedEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithExtendedEntity';
}

getProperties(): Array<StaticProperty<AspectWithExtendedEntity, any>> {
return [MetaAspectWithExtendedEntity.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithExtendedEntity, any>> {
        return this.getProperties();
}




    }


