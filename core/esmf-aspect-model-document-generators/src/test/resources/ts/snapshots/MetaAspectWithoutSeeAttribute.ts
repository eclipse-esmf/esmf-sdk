













import { AspectWithoutSeeAttribute,} from './AspectWithoutSeeAttribute';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithoutSeeAttribute implements StaticMetaClass<AspectWithoutSeeAttribute>, PropertyContainer<AspectWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithoutSeeAttribute();


getModelClass(): string {
return 'AspectWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithoutSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithoutSeeAttribute, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithoutSeeAttribute, any>> {
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


    }


