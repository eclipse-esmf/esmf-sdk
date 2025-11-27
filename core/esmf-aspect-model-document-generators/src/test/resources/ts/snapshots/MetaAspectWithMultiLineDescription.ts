













import { AspectWithMultiLineDescription,} from './AspectWithMultiLineDescription';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithMultiLineDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultiLineDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMultiLineDescription implements StaticMetaClass<AspectWithMultiLineDescription>, PropertyContainer<AspectWithMultiLineDescription> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultiLineDescription';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultiLineDescription();


getModelClass(): string {
return 'AspectWithMultiLineDescription';
}

getAspectModelUrn(): string {
return MetaAspectWithMultiLineDescription .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMultiLineDescription';
}

getProperties(): Array<StaticProperty<AspectWithMultiLineDescription, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithMultiLineDescription, any>> {
        return this.getProperties();
}


        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This\nis\na\ntest', language: 'en'},
        ];
        }


    }


