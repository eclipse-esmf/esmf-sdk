import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithOptionalPropertiesWithEntity,} from './AspectWithOptionalPropertiesWithEntity';
import { DefaultCharacteristic,DefaultEntity,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithOptionalPropertiesWithEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertiesWithEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOptionalPropertiesWithEntity implements StaticMetaClass<AspectWithOptionalPropertiesWithEntity>, PropertyContainer<AspectWithOptionalPropertiesWithEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertiesWithEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalPropertiesWithEntity();


 public static readonly  TEST_STRING = 
                
        new (class extends DefaultStaticProperty<AspectWithOptionalPropertiesWithEntity, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesWithEntity';
    }


    getValue( object : AspectWithOptionalPropertiesWithEntity) : string {
        return object.testString;
    }

        setValue( object : AspectWithOptionalPropertiesWithEntity, value : string ) {
            object.testString = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithOptionalPropertiesWithEntity',
    'testString',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value Test'),
        'testString',
    false,
    );




 public static readonly  TEST_OPTIONAL_STRING = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertiesWithEntity, string, string> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesWithEntity';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithOptionalPropertiesWithEntity) : string {
        return object.testOptionalString;
    }

        setValue( object : AspectWithOptionalPropertiesWithEntity, value : string ) {
            object.testOptionalString = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithOptionalPropertiesWithEntity',
    'testOptionalString',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    true,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value Test'),
        'testOptionalString',
    false,
    );




 public static readonly  TEST_OPTIONAL_ENTITY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertiesWithEntity, ReplacedAspectArtifact, ReplacedAspectArtifact> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesWithEntity';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

    getValue( object : AspectWithOptionalPropertiesWithEntity) : ReplacedAspectArtifact {
        return object.testOptionalEntity;
    }

        setValue( object : AspectWithOptionalPropertiesWithEntity, value : ReplacedAspectArtifact ) {
            object.testOptionalEntity = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithOptionalPropertiesWithEntity',
    'testOptionalEntity',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
'ReplacedAspectArtifactCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic';
 return defaultCharacteristic; })()
,
    false,
    true,
    undefined,
        'testOptionalEntity',
    false,
    );




getModelClass(): string {
return 'AspectWithOptionalPropertiesWithEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithOptionalPropertiesWithEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOptionalPropertiesWithEntity';
}

getProperties(): Array<StaticProperty<AspectWithOptionalPropertiesWithEntity, any>> {
return [MetaAspectWithOptionalPropertiesWithEntity.TEST_STRING, MetaAspectWithOptionalPropertiesWithEntity.TEST_OPTIONAL_STRING, MetaAspectWithOptionalPropertiesWithEntity.TEST_OPTIONAL_ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithOptionalPropertiesWithEntity, any>> {
        return this.getProperties();
}




    }


