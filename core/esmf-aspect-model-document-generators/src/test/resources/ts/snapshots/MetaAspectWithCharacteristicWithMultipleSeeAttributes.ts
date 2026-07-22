













import { AspectWithCharacteristicWithMultipleSeeAttributes,} from './AspectWithCharacteristicWithMultipleSeeAttributes';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithCharacteristicWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCharacteristicWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCharacteristicWithMultipleSeeAttributes implements StaticMetaClass<AspectWithCharacteristicWithMultipleSeeAttributes>, PropertyContainer<AspectWithCharacteristicWithMultipleSeeAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCharacteristicWithMultipleSeeAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCharacteristicWithMultipleSeeAttributes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithCharacteristicWithMultipleSeeAttributes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCharacteristicWithMultipleSeeAttributes';
    }


    getValue( object : AspectWithCharacteristicWithMultipleSeeAttributes) : string {
        return object.testProperty;
    }

        setValue( object : AspectWithCharacteristicWithMultipleSeeAttributes, value : string ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCharacteristicWithMultipleSeeAttributes',
    'testProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultCharacteristic.addPreferredName('en' , 'Test Characteristic');
defaultCharacteristic.addDescription('en' , 'Test Characteristic');
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
return 'AspectWithCharacteristicWithMultipleSeeAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithCharacteristicWithMultipleSeeAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCharacteristicWithMultipleSeeAttributes';
}

getProperties(): Array<StaticProperty<AspectWithCharacteristicWithMultipleSeeAttributes, any>> {
return [MetaAspectWithCharacteristicWithMultipleSeeAttributes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCharacteristicWithMultipleSeeAttributes, any>> {
        return this.getProperties();
}




    }


