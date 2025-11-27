













import { AspectWithPropertyWithAllBaseAttributes,} from './AspectWithPropertyWithAllBaseAttributes';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithPropertyWithAllBaseAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithAllBaseAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithPropertyWithAllBaseAttributes implements StaticMetaClass<AspectWithPropertyWithAllBaseAttributes>, PropertyContainer<AspectWithPropertyWithAllBaseAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPropertyWithAllBaseAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPropertyWithAllBaseAttributes();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPropertyWithAllBaseAttributes, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPropertyWithAllBaseAttributes';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithPropertyWithAllBaseAttributes',
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
return 'AspectWithPropertyWithAllBaseAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithPropertyWithAllBaseAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithPropertyWithAllBaseAttributes';
}

getProperties(): Array<StaticProperty<AspectWithPropertyWithAllBaseAttributes, any>> {
return [MetaAspectWithPropertyWithAllBaseAttributes.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPropertyWithAllBaseAttributes, any>> {
        return this.getProperties();
}




    }


