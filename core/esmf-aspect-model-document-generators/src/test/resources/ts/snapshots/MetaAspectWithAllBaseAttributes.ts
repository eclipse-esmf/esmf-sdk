













import { AspectWithAllBaseAttributes,} from './AspectWithAllBaseAttributes';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithAllBaseAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAllBaseAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithAllBaseAttributes implements StaticMetaClass<AspectWithAllBaseAttributes>, PropertyContainer<AspectWithAllBaseAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithAllBaseAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithAllBaseAttributes();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithAllBaseAttributes, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithAllBaseAttributes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#boolean" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'BooleanReplacedAspectArtifact';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testBoolean',
    false,
    );




getModelClass(): string {
return 'AspectWithAllBaseAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithAllBaseAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithAllBaseAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithAllBaseAttributes, any>> {
return [MetaAspectWithAllBaseAttributes.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithAllBaseAttributes, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Aspekt Mit Boolean', 'de'),
            new LangString('Aspect With Boolean', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('Test Beschreibung', 'de'),
            new LangString('Test Description', 'en'),
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


