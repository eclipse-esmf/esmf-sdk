import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithStringEnumeration,} from './AspectWithStringEnumeration';
import { DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';



    

/*
* Generated class MetaAspectWithStringEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithStringEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithStringEnumeration implements StaticMetaClass<AspectWithStringEnumeration>, PropertyContainer<AspectWithStringEnumeration> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithStringEnumeration';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithStringEnumeration();


 public static readonly  ENUMERATION_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithStringEnumeration, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithStringEnumeration';
    }


    getValue( object : AspectWithStringEnumeration) : ReplacedAspectArtifact {
        return object.enumerationProperty;
    }

        setValue( object : AspectWithStringEnumeration, value : ReplacedAspectArtifact ) {
            object.enumerationProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithStringEnumeration',
    'enumerationProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'bar'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'foo')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.isAnonymousNode = true;
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'enumerationProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithStringEnumeration';
}

getAspectModelUrn(): string {
return MetaAspectWithStringEnumeration .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithStringEnumeration';
}

getProperties(): Array<StaticProperty<AspectWithStringEnumeration, any>> {
return [MetaAspectWithStringEnumeration.ENUMERATION_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithStringEnumeration, any>> {
        return this.getProperties();
}




    }


