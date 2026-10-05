













import { AspectWithAllBaseAttributes,} from './AspectWithAllBaseAttributes';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithAllBaseAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAllBaseAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithAllBaseAttributes) : boolean {
        return object.testBoolean;
    }

        setValue( object : AspectWithAllBaseAttributes, value : boolean ) {
            object.testBoolean = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithAllBaseAttributes',
    'testBoolean',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'BooleanReplacedAspectArtifact',
'BooleanReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Aspekt Mit Boolean', language: 'de'},
            {value: 'Aspect With Boolean', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'Test Beschreibung', language: 'de'},
            {value: 'Test Description', language: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


