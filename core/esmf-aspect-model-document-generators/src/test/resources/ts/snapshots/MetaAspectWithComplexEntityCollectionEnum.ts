import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithComplexEntityCollectionEnum,} from './AspectWithComplexEntityCollectionEnum';
import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithComplexEntityCollectionEnum (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexEntityCollectionEnum).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithComplexEntityCollectionEnum implements StaticMetaClass<AspectWithComplexEntityCollectionEnum>, PropertyContainer<AspectWithComplexEntityCollectionEnum> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithComplexEntityCollectionEnum';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithComplexEntityCollectionEnum();


 public static readonly  MY_PROPERTY_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithComplexEntityCollectionEnum, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithComplexEntityCollectionEnum';
    }


    getValue( object : AspectWithComplexEntityCollectionEnum) : ReplacedAspectArtifact {
        return object.myPropertyOne;
    }

        setValue( object : AspectWithComplexEntityCollectionEnum, value : ReplacedAspectArtifact ) {
            object.myPropertyOne = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithComplexEntityCollectionEnum',
    'myPropertyOne',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultEntityInstance('entityInstanceOne',
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
defaultEnumeration.addDescription('en' , 'This is my enumeration one');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'myPropertyOne',
    false,
    );




getModelClass(): string {
return 'AspectWithComplexEntityCollectionEnum';
}

getAspectModelUrn(): string {
return MetaAspectWithComplexEntityCollectionEnum .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithComplexEntityCollectionEnum';
}

getProperties(): Array<StaticProperty<AspectWithComplexEntityCollectionEnum, any>> {
return [MetaAspectWithComplexEntityCollectionEnum.MY_PROPERTY_ONE];
}

getAllProperties(): Array<StaticProperty<AspectWithComplexEntityCollectionEnum, any>> {
        return this.getProperties();
}




    }


