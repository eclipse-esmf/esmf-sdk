import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithStringEnumeration,} from './AspectWithStringEnumeration';
import { DefaultEnumeration,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithStringEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithStringEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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


                                        })(

        null,
    null,
    null,
    (() => { const defaultEnumeration = new DefaultEnumeration(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


