













import { AspectWithMultiLanguageText,} from './AspectWithMultiLanguageText';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithMultiLanguageText (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultiLanguageText).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultiLanguageText implements StaticMetaClass<AspectWithMultiLanguageText>, PropertyContainer<AspectWithMultiLanguageText> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultiLanguageText';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultiLanguageText();


 public static readonly  PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithMultiLanguageText, LangString>{

    
    getPropertyType(): string {
                return 'LangString';
    }

    getContainingType(): string {
        return 'AspectWithMultiLanguageText';
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
    undefined,
        'prop',
    false,
    );




getModelClass(): string {
return 'AspectWithMultiLanguageText';
}

getAspectModelUrn(): string {
return MetaAspectWithMultiLanguageText .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultiLanguageText';
}

                        getProperties(): Array<StaticProperty<AspectWithMultiLanguageText, any>> {
return [MetaAspectWithMultiLanguageText.PROP];
}

getAllProperties(): Array<StaticProperty<AspectWithMultiLanguageText, any>> {
    return this.getProperties();
}




    }


