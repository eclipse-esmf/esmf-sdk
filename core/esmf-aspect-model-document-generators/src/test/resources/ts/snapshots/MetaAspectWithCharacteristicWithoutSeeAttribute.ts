













import { AspectWithCharacteristicWithoutSeeAttribute,} from './AspectWithCharacteristicWithoutSeeAttribute';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithCharacteristicWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCharacteristicWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCharacteristicWithoutSeeAttribute implements StaticMetaClass<AspectWithCharacteristicWithoutSeeAttribute>, PropertyContainer<AspectWithCharacteristicWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCharacteristicWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCharacteristicWithoutSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithCharacteristicWithoutSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCharacteristicWithoutSeeAttribute';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCharacteristicWithoutSeeAttribute',
    'testProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultCharacteristic.addPreferredName('en' , 'Test Characteristic');
defaultCharacteristic.addDescription('en' , 'Test Characteristic');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCharacteristicWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithCharacteristicWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCharacteristicWithoutSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithCharacteristicWithoutSeeAttribute, any>> {
return [MetaAspectWithCharacteristicWithoutSeeAttribute.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCharacteristicWithoutSeeAttribute, any>> {
        return this.getProperties();
}




    }


