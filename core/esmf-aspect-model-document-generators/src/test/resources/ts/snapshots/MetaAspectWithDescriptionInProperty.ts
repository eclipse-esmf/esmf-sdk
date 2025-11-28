













import { AspectWithDescriptionInProperty,} from './AspectWithDescriptionInProperty';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithDescriptionInProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDescriptionInProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithDescriptionInProperty implements StaticMetaClass<AspectWithDescriptionInProperty>, PropertyContainer<AspectWithDescriptionInProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDescriptionInProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDescriptionInProperty();


 public static readonly  ENABLED = 
                
        new (class extends DefaultStaticProperty<AspectWithDescriptionInProperty, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithDescriptionInProperty';
    }


    getValue( object : AspectWithDescriptionInProperty) : boolean {
        return object.enabled;
    }

        setValue( object : AspectWithDescriptionInProperty, value : boolean ) {
            object.enabled = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithDescriptionInProperty',
    'enabled',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Boolean',
'Boolean',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#boolean" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Boolean';
defaultCharacteristic.addPreferredName('en' , 'Boolean');
defaultCharacteristic.addDescription('en' , 'Represents a boolean value (i.e. a \"flag\").');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'enabled',
    false,
    );




getModelClass(): string {
return 'AspectWithDescriptionInProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithDescriptionInProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithDescriptionInProperty';
}

getProperties(): Array<StaticProperty<AspectWithDescriptionInProperty, any>> {
return [MetaAspectWithDescriptionInProperty.ENABLED];
}

getAllProperties(): Array<StaticProperty<AspectWithDescriptionInProperty, any>> {
        return this.getProperties();
}




    }


