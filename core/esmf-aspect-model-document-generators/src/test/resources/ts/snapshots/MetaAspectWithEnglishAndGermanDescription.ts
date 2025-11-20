













import { AspectWithEnglishAndGermanDescription,} from './AspectWithEnglishAndGermanDescription';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithEnglishAndGermanDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnglishAndGermanDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnglishAndGermanDescription implements StaticMetaClass<AspectWithEnglishAndGermanDescription>, PropertyContainer<AspectWithEnglishAndGermanDescription> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnglishAndGermanDescription';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnglishAndGermanDescription();


 public static readonly  TEST_STRING = 
                
        new (class extends DefaultStaticProperty<AspectWithEnglishAndGermanDescription, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithEnglishAndGermanDescription';
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
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value Test'),
        'testString',
    false,
    );




getModelClass(): string {
return 'AspectWithEnglishAndGermanDescription';
}

getAspectModelUrn(): string {
return MetaAspectWithEnglishAndGermanDescription .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEnglishAndGermanDescription';
}

                        getProperties(): Array<StaticProperty<AspectWithEnglishAndGermanDescription, any>> {
return [MetaAspectWithEnglishAndGermanDescription.TEST_STRING];
}

getAllProperties(): Array<StaticProperty<AspectWithEnglishAndGermanDescription, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Testaspekt', 'de'),
            new LangString('Test Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('Aspekt mit mehrsprachigen Beschreibungen', 'de'),
            new LangString('Aspect With Multilingual Descriptions', 'en'),
        ];
        }


    }


