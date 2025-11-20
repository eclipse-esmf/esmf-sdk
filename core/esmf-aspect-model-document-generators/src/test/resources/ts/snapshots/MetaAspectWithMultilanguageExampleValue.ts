













import { AspectWithMultilanguageExampleValue,} from './AspectWithMultilanguageExampleValue';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithMultilanguageExampleValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultilanguageExampleValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultilanguageExampleValue implements StaticMetaClass<AspectWithMultilanguageExampleValue>, PropertyContainer<AspectWithMultilanguageExampleValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultilanguageExampleValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultilanguageExampleValue();


 public static readonly  PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithMultilanguageExampleValue, LangString>{

    
    getPropertyType(): string {
                return 'LangString';
    }

    getContainingType(): string {
        return 'AspectWithMultilanguageExampleValue';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#MultiLanguageText';
defaultCharacteristic.addPreferredName('en' , 'Multi-Language Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text in multiple languages. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString" ),new LangString('Multilanguage example value.', 'de')),
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
return KnownVersionUtils.getLatest()
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


