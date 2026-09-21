













import { AspectWithNestedEntity,} from './AspectWithNestedEntity';
import { DefaultCharacteristic,DefaultEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Entity,} from './Entity';
import { KnownVersion,} from './esmf/shared/known-version';
import { MetaEntity,} from './MetaEntity';


    

/*
* Generated class MetaAspectWithNestedEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithNestedEntity implements StaticMetaClass<AspectWithNestedEntity>, PropertyContainer<AspectWithNestedEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNestedEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithNestedEntity();


 public static readonly  ENTITY = 
                
        new (class extends DefaultStaticProperty<AspectWithNestedEntity, Entity>{

    
    getPropertyType(): string {
                return 'Entity';
    }

    getContainingType(): string {
        return 'AspectWithNestedEntity';
    }


    getValue( object : AspectWithNestedEntity) : Entity {
        return object.entity;
    }

        setValue( object : AspectWithNestedEntity, value : Entity ) {
            object.entity = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithNestedEntity',
    'entity',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityCharacteristic',
'EntityCharacteristic',
(() => { const defaultEntityEntity = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Entity',
'Entity',
MetaEntity.INSTANCE.getProperties(),false,
undefined)
defaultEntityEntity.addAspectModelUrn = this.NAMESPACE + 'Entity';
 return defaultEntityEntity; })())
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'EntityCharacteristic';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'entity',
    false,
    );




getModelClass(): string {
return 'AspectWithNestedEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithNestedEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithNestedEntity';
}

getProperties(): Array<StaticProperty<AspectWithNestedEntity, any>> {
return [MetaAspectWithNestedEntity.ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithNestedEntity, any>> {
        return this.getProperties();
}




    }


