import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEitherWithComplexTypes,} from './AspectWithEitherWithComplexTypes';
import { DefaultCharacteristic,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Either,} from './esmf/aspect-meta-model/Either';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithEitherWithComplexTypes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithComplexTypes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEitherWithComplexTypes implements StaticMetaClass<AspectWithEitherWithComplexTypes>, PropertyContainer<AspectWithEitherWithComplexTypes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEitherWithComplexTypes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEitherWithComplexTypes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEitherWithComplexTypes, Either<ReplacedAspectArtifact, ReplacedAspectArtifact>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithEitherWithComplexTypes';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEitherWithComplexTypes',
    'testProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestEither',
'TestEither',
undefined)
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'TestEither';
defaultCharacteristic.addPreferredName('en' , 'Test Either');
defaultCharacteristic.addDescription('en' , 'Test Either Characteristic');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEitherWithComplexTypes';
}

getAspectModelUrn(): string {
return MetaAspectWithEitherWithComplexTypes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEitherWithComplexTypes';
}

getProperties(): Array<StaticProperty<AspectWithEitherWithComplexTypes, any>> {
return [MetaAspectWithEitherWithComplexTypes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEitherWithComplexTypes, any>> {
        return this.getProperties();
}




    }


