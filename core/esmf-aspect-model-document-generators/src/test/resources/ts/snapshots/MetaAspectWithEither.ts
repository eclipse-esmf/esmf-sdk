













import { AspectWithEither,} from './AspectWithEither';
import { DefaultCharacteristic,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Either,} from './esmf/aspect-meta-model/Either';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithEither (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEither).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEither implements StaticMetaClass<AspectWithEither>, PropertyContainer<AspectWithEither> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEither';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEither();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEither, Either<string, boolean>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithEither';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEither',
    'testProperty',
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
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEither';
}

getAspectModelUrn(): string {
return MetaAspectWithEither .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEither';
}

getProperties(): Array<StaticProperty<AspectWithEither, any>> {
return [MetaAspectWithEither.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEither, any>> {
        return this.getProperties();
}




    }


