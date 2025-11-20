













import { AspectWithoutLanguageTags,} from './AspectWithoutLanguageTags';


    

/*
* Generated class MetaAspectWithoutLanguageTags (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithoutLanguageTags).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithoutLanguageTags implements StaticMetaClass<AspectWithoutLanguageTags>, PropertyContainer<AspectWithoutLanguageTags> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithoutLanguageTags';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithoutLanguageTags();


getModelClass(): string {
return 'AspectWithoutLanguageTags';
}

getAspectModelUrn(): string {
return MetaAspectWithoutLanguageTags .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithoutLanguageTags';
}

                        getProperties(): Array<StaticProperty<AspectWithoutLanguageTags, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithoutLanguageTags, any>> {
    return this.getProperties();
}




    }


