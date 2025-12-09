













import { AspectWithPropertyWithPreferredNames,} from './AspectWithPropertyWithPreferredNames';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithPropertyWithPreferredNames (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithPreferredNames).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithPropertyWithPreferredNames implements StaticMetaClass<AspectWithPropertyWithPreferredNames>, PropertyContainer<AspectWithPropertyWithPreferredNames> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPropertyWithPreferredNames';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPropertyWithPreferredNames();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPropertyWithPreferredNames, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPropertyWithPreferredNames';
    }


    getValue( object : AspectWithPropertyWithPreferredNames) : boolean {
        return object.testBoolean;
    }

        setValue( object : AspectWithPropertyWithPreferredNames, value : boolean ) {
            object.testBoolean = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithPropertyWithPreferredNames',
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
return 'AspectWithPropertyWithPreferredNames';
}

getAspectModelUrn(): string {
return MetaAspectWithPropertyWithPreferredNames .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithPropertyWithPreferredNames';
}

getProperties(): Array<StaticProperty<AspectWithPropertyWithPreferredNames, any>> {
return [MetaAspectWithPropertyWithPreferredNames.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPropertyWithPreferredNames, any>> {
        return this.getProperties();
}




    }


