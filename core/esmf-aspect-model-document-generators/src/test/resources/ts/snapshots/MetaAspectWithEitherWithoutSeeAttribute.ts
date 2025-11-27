













import { AspectWithEitherWithoutSeeAttribute,} from './AspectWithEitherWithoutSeeAttribute';
import { DefaultCharacteristic,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Either,} from './esmf/aspect-meta-model/Either';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithEitherWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEitherWithoutSeeAttribute implements StaticMetaClass<AspectWithEitherWithoutSeeAttribute>, PropertyContainer<AspectWithEitherWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEitherWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEitherWithoutSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEitherWithoutSeeAttribute, Either<number, string>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithEitherWithoutSeeAttribute';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEitherWithoutSeeAttribute',
    'testProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestEither',
'TestEither',
undefined)
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'TestEither';
defaultCharacteristic.addPreferredName('en' , 'Test Either');
defaultCharacteristic.addDescription('en' , 'Test Either Characteristic');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEitherWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithEitherWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEitherWithoutSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithEitherWithoutSeeAttribute, any>> {
return [MetaAspectWithEitherWithoutSeeAttribute.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEitherWithoutSeeAttribute, any>> {
        return this.getProperties();
}




    }


