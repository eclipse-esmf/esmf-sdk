













import { AspectWithPreferredNames,} from './AspectWithPreferredNames';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithPreferredNames (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPreferredNames).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithPreferredNames implements StaticMetaClass<AspectWithPreferredNames>, PropertyContainer<AspectWithPreferredNames> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPreferredNames';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPreferredNames();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPreferredNames, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPreferredNames';
    }


    getValue( object : AspectWithPreferredNames) : boolean {
        return object.testBoolean;
    }

        setValue( object : AspectWithPreferredNames, value : boolean ) {
            object.testBoolean = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithPreferredNames',
    'testBoolean',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'BooleanReplacedAspectArtifact',
'BooleanReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#boolean" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'BooleanReplacedAspectArtifact';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testBoolean',
    false,
    );




getModelClass(): string {
return 'AspectWithPreferredNames';
}

getAspectModelUrn(): string {
return MetaAspectWithPreferredNames .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithPreferredNames';
}

getProperties(): Array<StaticProperty<AspectWithPreferredNames, any>> {
return [MetaAspectWithPreferredNames.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPreferredNames, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Aspekt Mit Boolean', language: 'de'},
            {value: 'Aspect With Boolean', language: 'en'},
        ];
        }



    }


