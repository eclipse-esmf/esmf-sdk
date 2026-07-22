













import { AspectWithCharacteristicWithSeeAttribute,} from './AspectWithCharacteristicWithSeeAttribute';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithCharacteristicWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCharacteristicWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCharacteristicWithSeeAttribute implements StaticMetaClass<AspectWithCharacteristicWithSeeAttribute>, PropertyContainer<AspectWithCharacteristicWithSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCharacteristicWithSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCharacteristicWithSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithCharacteristicWithSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCharacteristicWithSeeAttribute';
    }


    getValue( object : AspectWithCharacteristicWithSeeAttribute) : string {
        return object.testProperty;
    }

        setValue( object : AspectWithCharacteristicWithSeeAttribute, value : string ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCharacteristicWithSeeAttribute',
    'testProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultCharacteristic.addPreferredName('en' , 'Test Characteristic');
defaultCharacteristic.addDescription('en' , 'Test Characteristic');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithCharacteristicWithSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCharacteristicWithSeeAttribute';
    }


    getValue( object : AspectWithCharacteristicWithSeeAttribute) : string {
        return object.testPropertyTwo;
    }

        setValue( object : AspectWithCharacteristicWithSeeAttribute, value : string ) {
            object.testPropertyTwo = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCharacteristicWithSeeAttribute',
    'testPropertyTwo',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifactTwo',
'ReplacedAspectArtifactTwo',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactTwo';
defaultCharacteristic.addPreferredName('en' , 'Test Characteristic Two');
defaultCharacteristic.addDescription('en' , 'Test Characteristic Two');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/me');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testPropertyTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithCharacteristicWithSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithCharacteristicWithSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCharacteristicWithSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithCharacteristicWithSeeAttribute, any>> {
return [MetaAspectWithCharacteristicWithSeeAttribute.TEST_PROPERTY, MetaAspectWithCharacteristicWithSeeAttribute.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithCharacteristicWithSeeAttribute, any>> {
        return this.getProperties();
}




    }


