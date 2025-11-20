













import { AspectWithCustomNamespace,} from './AspectWithCustomNamespace';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithCustomNamespace (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCustomNamespace).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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
return KnownVersionUtils.getLatest()
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


    }


