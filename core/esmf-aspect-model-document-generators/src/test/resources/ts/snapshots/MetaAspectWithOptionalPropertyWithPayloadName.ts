













import { AspectWithOptionalPropertyWithPayloadName,} from './AspectWithOptionalPropertyWithPayloadName';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithOptionalPropertyWithPayloadName (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertyWithPayloadName).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalPropertyWithPayloadName implements StaticMetaClass<AspectWithOptionalPropertyWithPayloadName>, PropertyContainer<AspectWithOptionalPropertyWithPayloadName> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertyWithPayloadName';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalPropertyWithPayloadName();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertyWithPayloadName, string, string> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertyWithPayloadName';
    }

        getContainedType(): string {
            return 'AspectWithOptionalPropertyWithPayloadName';
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
return KnownVersionUtils.getLatest()
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


