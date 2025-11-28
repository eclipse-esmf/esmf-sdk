













import { AspectWithOptionalPropertyWithPayloadName,} from './AspectWithOptionalPropertyWithPayloadName';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithOptionalPropertyWithPayloadName (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertyWithPayloadName).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOptionalPropertyWithPayloadName implements StaticMetaClass<AspectWithOptionalPropertyWithPayloadName>, PropertyContainer<AspectWithOptionalPropertyWithPayloadName> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertyWithPayloadName';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalPropertyWithPayloadName();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertyWithPayloadName, string, string> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertyWithPayloadName';
    }

        getContainedType(): string {
            return 'string';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithOptionalPropertyWithPayloadName',
    'testProperty',
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
    true,
    undefined,
        'test',
    false,
    );




getModelClass(): string {
return 'AspectWithOptionalPropertyWithPayloadName';
}

getAspectModelUrn(): string {
return MetaAspectWithOptionalPropertyWithPayloadName .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOptionalPropertyWithPayloadName';
}

getProperties(): Array<StaticProperty<AspectWithOptionalPropertyWithPayloadName, any>> {
return [MetaAspectWithOptionalPropertyWithPayloadName.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithOptionalPropertyWithPayloadName, any>> {
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


