import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';














import { AspectWithMultipleEntitiesSameExtend,} from './AspectWithMultipleEntitiesSameExtend';
import { DefaultCharacteristic,DefaultEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';

import { MetatestEntityOne,} from './MetatestEntityOne';
import { MetatestEntityTwo,} from './MetatestEntityTwo';
import { testEntityOne,} from './testEntityOne';
import { testEntityTwo,} from './testEntityTwo';


    

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
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesSameExtend, testEntityOne>{

    
    getPropertyType(): string {
                return 'testEntityOne';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesSameExtend';
    }


    getValue( object : AspectWithMultipleEntitiesSameExtend) : testEntityOne {
        return object.testPropertyOne;
    }

        setValue( object : AspectWithMultipleEntitiesSameExtend, value : testEntityOne ) {
            object.testPropertyOne = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesSameExtend',
    'testPropertyOne',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testCharacteristicOne',
'testCharacteristicOne',
(() => { const defaultEntitytestEntityOne = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testEntityOne',
'testEntityOne',
MetatestEntityOne.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testEntityOne',
'testEntityOne',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return extendsDefaultEntityReplacedAspectArtifact; })())
defaultEntitytestEntityOne.addAspectModelUrn = this.NAMESPACE + 'testEntityOne';
 return defaultEntitytestEntityOne; })())
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
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesSameExtend, testEntityTwo>{

    
    getPropertyType(): string {
                return 'testEntityTwo';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesSameExtend';
    }


    getValue( object : AspectWithMultipleEntitiesSameExtend) : testEntityTwo {
        return object.testPropertyTwo;
    }

        setValue( object : AspectWithMultipleEntitiesSameExtend, value : testEntityTwo ) {
            object.testPropertyTwo = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesSameExtend',
    'testPropertyTwo',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testCharacteristicTwo',
'testCharacteristicTwo',
(() => { const defaultEntitytestEntityTwo = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testEntityTwo',
'testEntityTwo',
MetatestEntityTwo.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testEntityTwo',
'testEntityTwo',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return extendsDefaultEntityReplacedAspectArtifact; })())
defaultEntitytestEntityTwo.addAspectModelUrn = this.NAMESPACE + 'testEntityTwo';
 return defaultEntitytestEntityTwo; })())
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


