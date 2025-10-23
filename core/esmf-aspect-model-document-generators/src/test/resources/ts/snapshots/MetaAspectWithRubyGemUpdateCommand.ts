













import { AspectWithRubyGemUpdateCommand,} from './AspectWithRubyGemUpdateCommand';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithRubyGemUpdateCommand (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRubyGemUpdateCommand).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRubyGemUpdateCommand implements StaticMetaClass<AspectWithRubyGemUpdateCommand>, PropertyContainer<AspectWithRubyGemUpdateCommand> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRubyGemUpdateCommand';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRubyGemUpdateCommand();


getModelClass(): string {
return 'AspectWithRubyGemUpdateCommand';
}

getAspectModelUrn(): string {
return MetaAspectWithRubyGemUpdateCommand .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRubyGemUpdateCommand';
}

                        getProperties(): Array<StaticProperty<AspectWithRubyGemUpdateCommand, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithRubyGemUpdateCommand, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'gem update --system', languageTag: 'en'},
        ];
        }



    }


