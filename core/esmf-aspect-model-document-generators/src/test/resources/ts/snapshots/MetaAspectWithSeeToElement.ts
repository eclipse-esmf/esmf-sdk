













import { AspectWithSeeToElement,} from './AspectWithSeeToElement';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithSeeToElement (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSeeToElement).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSeeToElement implements StaticMetaClass<AspectWithSeeToElement>, PropertyContainer<AspectWithSeeToElement> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSeeToElement';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSeeToElement();


getModelClass(): string {
return 'AspectWithSeeToElement';
}

getAspectModelUrn(): string {
return MetaAspectWithSeeToElement .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithSeeToElement';
}

                        getProperties(): Array<StaticProperty<AspectWithSeeToElement, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithSeeToElement, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Test Aspect', languageTag: 'en'},
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            {value: 'This is a test Aspect.', languageTag: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            "urn:samm:org.eclipse.test:1.0.0#NonExistingElement",
        ];
        }

    }


