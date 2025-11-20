













import { AspectWithListAndAdditionalProperty,} from './AspectWithListAndAdditionalProperty';
import { DefaultCharacteristic,DefaultList,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,StaticContainerProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithListAndAdditionalProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithListAndAdditionalProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithListAndAdditionalProperty implements StaticMetaClass<AspectWithListAndAdditionalProperty>, PropertyContainer<AspectWithListAndAdditionalProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithListAndAdditionalProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithListAndAdditionalProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithListAndAdditionalProperty, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithListAndAdditionalProperty';
    }

        getContainedType(): string {
            return 'AspectWithListAndAdditionalProperty';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultList = new DefaultList(null, 
null, 
null, 
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

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Test Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('This is a test description', 'en'),
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


