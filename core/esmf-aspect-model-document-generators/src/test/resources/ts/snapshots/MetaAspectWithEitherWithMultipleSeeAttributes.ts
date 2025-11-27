













import { AspectWithEitherWithMultipleSeeAttributes,} from './AspectWithEitherWithMultipleSeeAttributes';
import { DefaultCharacteristic,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Either,} from './esmf/aspect-meta-model/Either';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithEitherWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEitherWithMultipleSeeAttributes implements StaticMetaClass<AspectWithEitherWithMultipleSeeAttributes>, PropertyContainer<AspectWithEitherWithMultipleSeeAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEitherWithMultipleSeeAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEitherWithMultipleSeeAttributes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEitherWithMultipleSeeAttributes, Either<number, string>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithEitherWithMultipleSeeAttributes';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEitherWithMultipleSeeAttributes',
    'testProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestEither',
'TestEither',
undefined)
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'TestEither';
defaultCharacteristic.addPreferredName('en' , 'Test Either');
defaultCharacteristic.addDescription('en' , 'Test Either Characteristic');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/me');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEitherWithMultipleSeeAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithEitherWithMultipleSeeAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEitherWithMultipleSeeAttributes';
}

getProperties(): Array<StaticProperty<AspectWithEitherWithMultipleSeeAttributes, any>> {
return [MetaAspectWithEitherWithMultipleSeeAttributes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEitherWithMultipleSeeAttributes, any>> {
        return this.getProperties();
}




    }


