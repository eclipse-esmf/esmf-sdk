













import { AspectWithUmlautDescription,} from './AspectWithUmlautDescription';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithUmlautDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUmlautDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithUmlautDescription implements StaticMetaClass<AspectWithUmlautDescription>, PropertyContainer<AspectWithUmlautDescription> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUmlautDescription';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUmlautDescription();


getModelClass(): string {
return 'AspectWithUmlautDescription';
}

getAspectModelUrn(): string {
return MetaAspectWithUmlautDescription .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithUmlautDescription';
}

getProperties(): Array<StaticProperty<AspectWithUmlautDescription, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithUmlautDescription, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect mit Umlauten in der Beschreibung', language: 'de'},
            {value: 'Test Aspect with Umlauts within description', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'Im Wort Entität ist ein Umlaut', language: 'de'},
            {value: 'This is a test description', language: 'en'},
        ];
        }


    }


