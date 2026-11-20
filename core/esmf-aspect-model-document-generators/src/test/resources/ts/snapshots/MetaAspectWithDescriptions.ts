













import { AspectWithDescriptions,} from './AspectWithDescriptions';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithDescriptions (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDescriptions).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithDescriptions implements StaticMetaClass<AspectWithDescriptions>, PropertyContainer<AspectWithDescriptions> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDescriptions';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDescriptions();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithDescriptions, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithDescriptions';
    }


    getValue( object : AspectWithDescriptions) : boolean {
        return object.testBoolean;
    }

        setValue( object : AspectWithDescriptions, value : boolean ) {
            object.testBoolean = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithDescriptions',
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
return 'AspectWithDescriptions';
}

getAspectModelUrn(): string {
return MetaAspectWithDescriptions .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithDescriptions';
}

getProperties(): Array<StaticProperty<AspectWithDescriptions, any>> {
return [MetaAspectWithDescriptions.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithDescriptions, any>> {
        return this.getProperties();
}


        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'Test Beschreibung', language: 'de'},
            {value: 'Test Description', language: 'en'},
        ];
        }


    }


