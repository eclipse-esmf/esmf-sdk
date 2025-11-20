













import { AspectWithUsedAndUnusedCharacteristic,} from './AspectWithUsedAndUnusedCharacteristic';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithUsedAndUnusedCharacteristic (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedCharacteristic).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithUsedAndUnusedCharacteristic implements StaticMetaClass<AspectWithUsedAndUnusedCharacteristic>, PropertyContainer<AspectWithUsedAndUnusedCharacteristic> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedCharacteristic';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUsedAndUnusedCharacteristic();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithUsedAndUnusedCharacteristic, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithUsedAndUnusedCharacteristic';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'UsedReplacedAspectArtifact';
defaultCharacteristic.addPreferredName('en' , 'Used Test Characteristic');
defaultCharacteristic.addDescription('en' , 'Used Test Characteristic');
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
return 'AspectWithUsedAndUnusedCharacteristic';
}

getAspectModelUrn(): string {
return MetaAspectWithUsedAndUnusedCharacteristic .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithUsedAndUnusedCharacteristic';
}

                        getProperties(): Array<StaticProperty<AspectWithUsedAndUnusedCharacteristic, any>> {
return [MetaAspectWithUsedAndUnusedCharacteristic.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUsedAndUnusedCharacteristic, any>> {
    return this.getProperties();
}




    }


