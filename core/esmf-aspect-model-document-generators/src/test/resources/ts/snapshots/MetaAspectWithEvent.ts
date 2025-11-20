













import { AspectWithEvent,} from './AspectWithEvent';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithEvent (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEvent).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEvent implements StaticMetaClass<AspectWithEvent>, PropertyContainer<AspectWithEvent> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEvent';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEvent();


getModelClass(): string {
return 'AspectWithEvent';
}

getAspectModelUrn(): string {
return MetaAspectWithEvent .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEvent';
}

                        getProperties(): Array<StaticProperty<AspectWithEvent, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithEvent, any>> {
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


