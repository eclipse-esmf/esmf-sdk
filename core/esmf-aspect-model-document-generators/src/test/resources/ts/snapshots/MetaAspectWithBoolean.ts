













import { AspectWithBoolean,} from './AspectWithBoolean';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithBoolean (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithBoolean).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithBoolean implements StaticMetaClass<AspectWithBoolean>, PropertyContainer<AspectWithBoolean> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithBoolean';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithBoolean();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithBoolean, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithBoolean';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithBoolean',
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
return 'AspectWithBoolean';
}

getAspectModelUrn(): string {
return MetaAspectWithBoolean .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithBoolean';
}

getProperties(): Array<StaticProperty<AspectWithBoolean, any>> {
return [MetaAspectWithBoolean.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithBoolean, any>> {
        return this.getProperties();
}




    }


