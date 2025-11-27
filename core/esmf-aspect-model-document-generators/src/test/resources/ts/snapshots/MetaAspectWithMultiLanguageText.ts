













import { AspectWithMultiLanguageText,} from './AspectWithMultiLanguageText';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithMultiLanguageText (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultiLanguageText).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultiLanguageText implements StaticMetaClass<AspectWithMultiLanguageText>, PropertyContainer<AspectWithMultiLanguageText> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultiLanguageText';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultiLanguageText();


 public static readonly  PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithMultiLanguageText, MultiLanguageText>{

    
    getPropertyType(): string {
                return 'MultiLanguageText';
    }

    getContainingType(): string {
        return 'AspectWithMultiLanguageText';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMultiLanguageText',
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
return KnownVersion.getLatest()
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


