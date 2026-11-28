













import { AspectWithMultipleSeeAttributes,} from './AspectWithMultipleSeeAttributes';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultipleSeeAttributes implements StaticMetaClass<AspectWithMultipleSeeAttributes>, PropertyContainer<AspectWithMultipleSeeAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleSeeAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleSeeAttributes();


getModelClass(): string {
return 'AspectWithMultipleSeeAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleSeeAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultipleSeeAttributes';
}

getProperties(): Array<StaticProperty<AspectWithMultipleSeeAttributes, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleSeeAttributes, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This is a test Aspect.', language: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
            'http:\/\/example.com\/me',
        ];
        }

    }


