import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithOptionalPropertiesWithEntity,} from './AspectWithOptionalPropertiesWithEntity';
import { DefaultCharacteristic,DefaultEntity,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,StaticContainerProperty,} from './core/staticConstraintProperty';




    

/*
* Generated class MetaAspectWithOptionalPropertiesWithEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertiesWithEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesWithEntity';
    }

        getContainedType(): string {
            return 'AspectWithOptionalPropertiesWithEntity';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesWithEntity';
    }

        getContainedType(): string {
            return 'AspectWithOptionalPropertiesWithEntity';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


