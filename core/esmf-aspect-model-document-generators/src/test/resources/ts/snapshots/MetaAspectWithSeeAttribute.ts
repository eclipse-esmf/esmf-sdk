













import { AspectWithSeeAttribute,} from './AspectWithSeeAttribute';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSeeAttribute implements StaticMetaClass<AspectWithSeeAttribute>, PropertyContainer<AspectWithSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSeeAttribute();


getModelClass(): string {
return 'AspectWithSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithSeeAttribute';
}

                        getProperties(): Array<StaticProperty<AspectWithSeeAttribute, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithSeeAttribute, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Test Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('This is a test Aspect.', 'en'),
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


