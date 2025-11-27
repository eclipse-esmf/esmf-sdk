













import { AspectWithPropertyWithDescriptions,} from './AspectWithPropertyWithDescriptions';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithPropertyWithDescriptions (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithDescriptions).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithPropertyWithDescriptions implements StaticMetaClass<AspectWithPropertyWithDescriptions>, PropertyContainer<AspectWithPropertyWithDescriptions> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPropertyWithDescriptions';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPropertyWithDescriptions();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPropertyWithDescriptions, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPropertyWithDescriptions';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithPropertyWithDescriptions',
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
return 'AspectWithPropertyWithDescriptions';
}

getAspectModelUrn(): string {
return MetaAspectWithPropertyWithDescriptions .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithPropertyWithDescriptions';
}

getProperties(): Array<StaticProperty<AspectWithPropertyWithDescriptions, any>> {
return [MetaAspectWithPropertyWithDescriptions.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPropertyWithDescriptions, any>> {
        return this.getProperties();
}




    }


