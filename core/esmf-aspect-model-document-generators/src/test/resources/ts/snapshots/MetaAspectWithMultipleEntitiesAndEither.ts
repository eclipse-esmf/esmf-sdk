import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntitiesAndEither,} from './AspectWithMultipleEntitiesAndEither';
import { DefaultCharacteristic,DefaultEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Either,} from './esmf/aspect-meta-model/Either';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithMultipleEntitiesAndEither (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesAndEither).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultipleEntitiesAndEither implements StaticMetaClass<AspectWithMultipleEntitiesAndEither>, PropertyContainer<AspectWithMultipleEntitiesAndEither> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntitiesAndEither';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntitiesAndEither();


 public static readonly  TEST_ENTITY_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesAndEither, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesAndEither';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesAndEither',
    'testEntityOne',
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
        'testEntityOne',
    false,
    );




 public static readonly  TEST_ENTITY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesAndEither, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesAndEither';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesAndEither',
    'testEntityTwo',
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
        'testEntityTwo',
    false,
    );




 public static readonly  TEST_EITHER_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesAndEither, Either<ReplacedAspectArtifact, ReplacedAspectArtifact>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesAndEither';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultipleEntitiesAndEither',
    'testEitherProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestEither',
'TestEither',
undefined)
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'TestEither';
defaultCharacteristic.addPreferredName('en' , 'Test Either');
defaultCharacteristic.addDescription('en' , 'This is a test Either.');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testEitherProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithMultipleEntitiesAndEither';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntitiesAndEither .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntitiesAndEither';
}

getProperties(): Array<StaticProperty<AspectWithMultipleEntitiesAndEither, any>> {
return [MetaAspectWithMultipleEntitiesAndEither.TEST_ENTITY_ONE, MetaAspectWithMultipleEntitiesAndEither.TEST_ENTITY_TWO, MetaAspectWithMultipleEntitiesAndEither.TEST_EITHER_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntitiesAndEither, any>> {
        return this.getProperties();
}




    }


