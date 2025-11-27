













import { AspectWithListAndAdditionalProperty,} from './AspectWithListAndAdditionalProperty';
import { DefaultCharacteristic,DefaultList,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithListAndAdditionalProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithListAndAdditionalProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithListAndAdditionalProperty implements StaticMetaClass<AspectWithListAndAdditionalProperty>, PropertyContainer<AspectWithListAndAdditionalProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithListAndAdditionalProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithListAndAdditionalProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithListAndAdditionalProperty, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithListAndAdditionalProperty';
    }

        getContainedType(): string {
            return 'AspectWithListAndAdditionalProperty';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithListAndAdditionalProperty',
    'testProperty',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestList',
'TestList',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultList.addAspectModelUrn = this.NAMESPACE + 'TestList';
defaultList.addPreferredName('en' , 'Test List');
defaultList.addDescription('en' , 'This is a test list.');
defaultList.addSeeReference('http:\/\/example.com\/');
 return defaultList; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value'),
        'testProperty',
    false,
    );




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithListAndAdditionalProperty, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithListAndAdditionalProperty';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithListAndAdditionalProperty',
    'testPropertyTwo',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value'),
        'testPropertyTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithListAndAdditionalProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithListAndAdditionalProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithListAndAdditionalProperty';
}

getProperties(): Array<StaticProperty<AspectWithListAndAdditionalProperty, any>> {
return [MetaAspectWithListAndAdditionalProperty.TEST_PROPERTY, MetaAspectWithListAndAdditionalProperty.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithListAndAdditionalProperty, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This is a test description', language: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


