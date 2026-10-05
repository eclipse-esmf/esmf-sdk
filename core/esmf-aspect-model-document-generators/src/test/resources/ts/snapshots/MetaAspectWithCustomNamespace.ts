













import { AspectWithCustomNamespace,} from './AspectWithCustomNamespace';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithCustomNamespace (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCustomNamespace).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCustomNamespace implements StaticMetaClass<AspectWithCustomNamespace>, PropertyContainer<AspectWithCustomNamespace> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCustomNamespace';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCustomNamespace();


getModelClass(): string {
return 'AspectWithCustomNamespace';
}

getAspectModelUrn(): string {
return MetaAspectWithCustomNamespace .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCustomNamespace';
}

getProperties(): Array<StaticProperty<AspectWithCustomNamespace, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithCustomNamespace, any>> {
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


    }


