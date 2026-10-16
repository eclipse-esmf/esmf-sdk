













import { AspectWithSeeToElement,} from './AspectWithSeeToElement';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithSeeToElement (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSeeToElement).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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
return KnownVersion.getLatest()
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
            'urn:samm:org.eclipse.test:1.0.0#NonExistingElement',
        ];
        }

    }


