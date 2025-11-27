import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntitiesOnMultipleLevels,} from './AspectWithMultipleEntitiesOnMultipleLevels';
import { DefaultCharacteristic,DefaultEntity,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';






    

/*
* Generated class MetaAspectWithMultipleEntitiesOnMultipleLevels (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesOnMultipleLevels).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultipleEntitiesOnMultipleLevels implements StaticMetaClass<AspectWithMultipleEntitiesOnMultipleLevels>, PropertyContainer<AspectWithMultipleEntitiesOnMultipleLevels> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntitiesOnMultipleLevels';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntitiesOnMultipleLevels();


 public static readonly  TEST_ENTITY_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesOnMultipleLevels';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesOnMultipleLevels',
    'ReplacedAspectArtifact',
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
    false,
    undefined,
        'ReplacedAspectArtifact',
    false,
    );




 public static readonly  TEST_ENTITY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesOnMultipleLevels';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesOnMultipleLevels',
    'ReplacedAspectArtifact',
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
    false,
    undefined,
        'ReplacedAspectArtifact',
    false,
    );




 public static readonly  TEST_STRING = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesOnMultipleLevels';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesOnMultipleLevels',
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




 public static readonly  TEST_SECOND_ENTITY = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesOnMultipleLevels';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesOnMultipleLevels',
    'testSecondEntity',
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
    false,
    undefined,
        'testSecondEntity',
    false,
    );




getModelClass(): string {
return 'AspectWithMultipleEntitiesOnMultipleLevels';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntitiesOnMultipleLevels .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntitiesOnMultipleLevels';
}

getProperties(): Array<StaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, any>> {
return [MetaAspectWithMultipleEntitiesOnMultipleLevels.TEST_ENTITY_ONE, MetaAspectWithMultipleEntitiesOnMultipleLevels.TEST_ENTITY_TWO, MetaAspectWithMultipleEntitiesOnMultipleLevels.TEST_STRING, MetaAspectWithMultipleEntitiesOnMultipleLevels.TEST_SECOND_ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, any>> {
        return this.getProperties();
}




    }


