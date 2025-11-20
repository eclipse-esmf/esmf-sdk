













import { AspectWithEither,} from './AspectWithEither';
import { DefaultCharacteristic,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { Either,} from './core/Either';


    

/*
* Generated class MetaAspectWithEither (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEither).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


