













import { AspectWithUmlautDescription,} from './AspectWithUmlautDescription';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithUmlautDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUmlautDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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
return KnownVersionUtils.getLatest()
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

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Test Aspect mit Umlauten in der Beschreibung', languageTag: 'de'},
            {value: 'Test Aspect with Umlauts within description', languageTag: 'en'},
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            {value: 'Im Wort Entität ist ein Umlaut', languageTag: 'de'},
            {value: 'This is a test description', languageTag: 'en'},
        ];
        }


    }


