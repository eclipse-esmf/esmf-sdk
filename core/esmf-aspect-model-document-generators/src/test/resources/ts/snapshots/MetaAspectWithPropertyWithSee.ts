













import { AspectWithPropertyWithSee,} from './AspectWithPropertyWithSee';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithPropertyWithSee (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithSee).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithPropertyWithSee implements StaticMetaClass<AspectWithPropertyWithSee>, PropertyContainer<AspectWithPropertyWithSee> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPropertyWithSee';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPropertyWithSee();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPropertyWithSee, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPropertyWithSee';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithPropertyWithSee',
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
return 'AspectWithPropertyWithSee';
}

getAspectModelUrn(): string {
return MetaAspectWithPropertyWithSee .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithPropertyWithSee';
}

getProperties(): Array<StaticProperty<AspectWithPropertyWithSee, any>> {
return [MetaAspectWithPropertyWithSee.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPropertyWithSee, any>> {
        return this.getProperties();
}




    }


