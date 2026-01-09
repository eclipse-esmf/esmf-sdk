













import { AspectWithUsedAndUnusedEither,} from './AspectWithUsedAndUnusedEither';
import { DefaultCharacteristic,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Either,} from './esmf/aspect-meta-model/Either';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithUsedAndUnusedEither (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedEither).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithUsedAndUnusedEither implements StaticMetaClass<AspectWithUsedAndUnusedEither>, PropertyContainer<AspectWithUsedAndUnusedEither> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedEither';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUsedAndUnusedEither();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithUsedAndUnusedEither, Either<number, string>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithUsedAndUnusedEither';
    }


    getValue( object : AspectWithUsedAndUnusedEither) : Either<number, string> {
        return object.testProperty;
    }

        setValue( object : AspectWithUsedAndUnusedEither, value : Either<number, string> ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithUsedAndUnusedEither',
    'testProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'UsedTestEither',
'UsedTestEither',
undefined)
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'UsedTestEither';
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
return 'AspectWithUsedAndUnusedEither';
}

getAspectModelUrn(): string {
return MetaAspectWithUsedAndUnusedEither .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithUsedAndUnusedEither';
}

getProperties(): Array<StaticProperty<AspectWithUsedAndUnusedEither, any>> {
return [MetaAspectWithUsedAndUnusedEither.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUsedAndUnusedEither, any>> {
        return this.getProperties();
}




    }


