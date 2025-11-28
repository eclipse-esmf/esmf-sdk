













import { AspectWithEnglishAndGermanDescription,} from './AspectWithEnglishAndGermanDescription';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithEnglishAndGermanDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnglishAndGermanDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithEnglishAndGermanDescription) : string {
        return object.testString;
    }

        setValue( object : AspectWithEnglishAndGermanDescription, value : string ) {
            object.testString = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnglishAndGermanDescription',
    'testString',
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
return KnownVersion.getLatest()
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

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Testaspekt', language: 'de'},
            {value: 'Test Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'Aspekt mit mehrsprachigen Beschreibungen', language: 'de'},
            {value: 'Aspect With Multilingual Descriptions', language: 'en'},
        ];
        }


    }


