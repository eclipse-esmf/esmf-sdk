import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEnumerationsOnMultipleLevels,} from './AspectWithMultipleEnumerationsOnMultipleLevels';
import { DefaultEntity,DefaultEnumeration,DefaultScalar,DefaultSingleEntity,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';






    

/*
* Generated class MetaAspectWithMultipleEnumerationsOnMultipleLevels (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEnumerationsOnMultipleLevels).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleEnumerationsOnMultipleLevels implements StaticMetaClass<AspectWithMultipleEnumerationsOnMultipleLevels>, PropertyContainer<AspectWithMultipleEnumerationsOnMultipleLevels> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEnumerationsOnMultipleLevels';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEnumerationsOnMultipleLevels();


 public static readonly  TEST_PROPERTY_WITH_ENUM_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEnumerationsOnMultipleLevels';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultEnumeration = new DefaultEnumeration(null, 
null, 
null, 
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'1'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'2'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'3')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testPropertyWithEnumOne',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_ENUM_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEnumerationsOnMultipleLevels';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultEnumeration = new DefaultEnumeration(null, 
null, 
null, 
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'One'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Three'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Two')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testPropertyWithEnumTwo',
    false,
    );




 public static readonly  TEST_ENTITY_WITH_ENUM_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEnumerationsOnMultipleLevels';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultSingleEntity = new DefaultSingleEntity(null, 
null, 
null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic';
 return defaultSingleEntity; })()
,
    false,
    false,
    undefined,
        'testEntityWithEnumOne',
    false,
    );




getModelClass(): string {
return 'AspectWithMultipleEnumerationsOnMultipleLevels';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEnumerationsOnMultipleLevels .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultipleEnumerationsOnMultipleLevels';
}

                        getProperties(): Array<StaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, any>> {
return [MetaAspectWithMultipleEnumerationsOnMultipleLevels.TEST_PROPERTY_WITH_ENUM_ONE, MetaAspectWithMultipleEnumerationsOnMultipleLevels.TEST_PROPERTY_WITH_ENUM_TWO, MetaAspectWithMultipleEnumerationsOnMultipleLevels.TEST_ENTITY_WITH_ENUM_ONE];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, any>> {
    return this.getProperties();
}




    }


