













import { AspectWithMultilanguageExampleValue,} from './AspectWithMultilanguageExampleValue';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithMultilanguageExampleValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultilanguageExampleValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultilanguageExampleValue implements StaticMetaClass<AspectWithMultilanguageExampleValue>, PropertyContainer<AspectWithMultilanguageExampleValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultilanguageExampleValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultilanguageExampleValue();


 public static readonly  PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithMultilanguageExampleValue, MultiLanguageText>{

    
    getPropertyType(): string {
                return 'MultiLanguageText';
    }

    getContainingType(): string {
        return 'AspectWithMultilanguageExampleValue';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultilanguageExampleValue',
    'prop',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#MultiLanguageText',
'MultiLanguageText',
new DefaultScalar("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#MultiLanguageText';
defaultCharacteristic.addPreferredName('en' , 'Multi-Language Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text in multiple languages. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString" ),{ value: 'Multilanguage example value.', language: 'de' }),
        'prop',
    false,
    );




getModelClass(): string {
return 'AspectWithMultilanguageExampleValue';
}

getAspectModelUrn(): string {
return MetaAspectWithMultilanguageExampleValue .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultilanguageExampleValue';
}

getProperties(): Array<StaticProperty<AspectWithMultilanguageExampleValue, any>> {
return [MetaAspectWithMultilanguageExampleValue.PROP];
}

getAllProperties(): Array<StaticProperty<AspectWithMultilanguageExampleValue, any>> {
        return this.getProperties();
}




    }


