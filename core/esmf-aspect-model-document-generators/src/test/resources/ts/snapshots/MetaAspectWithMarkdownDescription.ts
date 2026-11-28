













import { AspectWithMarkdownDescription,} from './AspectWithMarkdownDescription';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithMarkdownDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMarkdownDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMarkdownDescription implements StaticMetaClass<AspectWithMarkdownDescription>, PropertyContainer<AspectWithMarkdownDescription> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMarkdownDescription';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMarkdownDescription();


 public static readonly  MY_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithMarkdownDescription, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithMarkdownDescription';
    }


    getValue( object : AspectWithMarkdownDescription) : string {
        return object.myProperty;
    }

        setValue( object : AspectWithMarkdownDescription, value : string ) {
            object.myProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMarkdownDescription',
    'myProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'myProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithMarkdownDescription';
}

getAspectModelUrn(): string {
return MetaAspectWithMarkdownDescription .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMarkdownDescription';
}

getProperties(): Array<StaticProperty<AspectWithMarkdownDescription, any>> {
return [MetaAspectWithMarkdownDescription.MY_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithMarkdownDescription, any>> {
        return this.getProperties();
}




    }


