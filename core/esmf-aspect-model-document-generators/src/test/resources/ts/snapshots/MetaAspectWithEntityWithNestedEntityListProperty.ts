













import { AspectWithEntityWithNestedEntityListProperty,} from './AspectWithEntityWithNestedEntityListProperty';
import { DefaultEntity,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Entity,} from './Entity';
import { KnownVersion,} from './esmf/shared/known-version';
import { MetaEntity,} from './MetaEntity';


    

/*
* Generated class MetaAspectWithEntityWithNestedEntityListProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityWithNestedEntityListProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEntityWithNestedEntityListProperty implements StaticMetaClass<AspectWithEntityWithNestedEntityListProperty>, PropertyContainer<AspectWithEntityWithNestedEntityListProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityWithNestedEntityListProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityWithNestedEntityListProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityWithNestedEntityListProperty, Entity>{

    
    getPropertyType(): string {
                return 'Entity';
    }

    getContainingType(): string {
        return 'AspectWithEntityWithNestedEntityListProperty';
    }


    getValue( object : AspectWithEntityWithNestedEntityListProperty) : Entity {
        return object.testProperty;
    }

        setValue( object : AspectWithEntityWithNestedEntityListProperty, value : Entity ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEntityWithNestedEntityListProperty',
    'testProperty',
    (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
'ReplacedAspectArtifactCharacteristic',
(() => { const defaultEntityEntity = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Entity',
'Entity',
MetaEntity.INSTANCE.getProperties(),false,
undefined)
defaultEntityEntity.addAspectModelUrn = this.NAMESPACE + 'Entity';
 return defaultEntityEntity; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic';
defaultSingleEntity.addPreferredName('en' , 'Test Entity Characteristic');
defaultSingleEntity.addDescription('en' , 'This is a test Entity Characteristic');
defaultSingleEntity.addSeeReference('http:\/\/example.com\/');
 return defaultSingleEntity; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEntityWithNestedEntityListProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityWithNestedEntityListProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEntityWithNestedEntityListProperty';
}

getProperties(): Array<StaticProperty<AspectWithEntityWithNestedEntityListProperty, any>> {
return [MetaAspectWithEntityWithNestedEntityListProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityWithNestedEntityListProperty, any>> {
        return this.getProperties();
}




    }


