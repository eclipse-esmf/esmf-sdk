import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntitiesSameExtend,} from './AspectWithMultipleEntitiesSameExtend';
import { DefaultCharacteristic,DefaultEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';







    

/*
* Generated class MetaAspectWithMultipleEntitiesSameExtend (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesSameExtend).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultipleEntitiesSameExtend implements StaticMetaClass<AspectWithMultipleEntitiesSameExtend>, PropertyContainer<AspectWithMultipleEntitiesSameExtend> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntitiesSameExtend';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntitiesSameExtend();


 public static readonly  TEST_PROPERTY_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesSameExtend, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesSameExtend';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesSameExtend',
    'testPropertyOne',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testCharacteristicOne',
'testCharacteristicOne',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return extendsDefaultEntityReplacedAspectArtifact; })())
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'testCharacteristicOne';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testPropertyOne',
    false,
    );




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesSameExtend, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesSameExtend';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesSameExtend',
    'testPropertyTwo',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testCharacteristicTwo',
'testCharacteristicTwo',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return extendsDefaultEntityReplacedAspectArtifact; })())
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'testCharacteristicTwo';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testPropertyTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithMultipleEntitiesSameExtend';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntitiesSameExtend .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntitiesSameExtend';
}

getProperties(): Array<StaticProperty<AspectWithMultipleEntitiesSameExtend, any>> {
return [MetaAspectWithMultipleEntitiesSameExtend.TEST_PROPERTY_ONE, MetaAspectWithMultipleEntitiesSameExtend.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntitiesSameExtend, any>> {
        return this.getProperties();
}




    }


