













import { AspectWithBinary,} from './AspectWithBinary';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithBinary (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithBinary).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithBinary implements StaticMetaClass<AspectWithBinary>, PropertyContainer<AspectWithBinary> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithBinary';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithBinary();


 public static readonly  TEST_BINARY = 
                
        new (class extends DefaultStaticProperty<AspectWithBinary, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithBinary';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#hexBinary" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'BinaryReplacedAspectArtifact';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testBinary',
    false,
    );




getModelClass(): string {
return 'AspectWithBinary';
}

getAspectModelUrn(): string {
return MetaAspectWithBinary .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithBinary';
}

                        getProperties(): Array<StaticProperty<AspectWithBinary, any>> {
return [MetaAspectWithBinary.TEST_BINARY];
}

getAllProperties(): Array<StaticProperty<AspectWithBinary, any>> {
    return this.getProperties();
}




    }


