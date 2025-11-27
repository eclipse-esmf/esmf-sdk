import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumAndOptionalEnumProperties,} from './AspectWithEnumAndOptionalEnumProperties';
import { DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';



    

/*
* Generated class MetaAspectWithEnumAndOptionalEnumProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumAndOptionalEnumProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEnumAndOptionalEnumProperties implements StaticMetaClass<AspectWithEnumAndOptionalEnumProperties>, PropertyContainer<AspectWithEnumAndOptionalEnumProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumAndOptionalEnumProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumAndOptionalEnumProperties();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumAndOptionalEnumProperties, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumAndOptionalEnumProperties';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumAndOptionalEnumProperties',
    'testProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'1'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'2'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'3')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




 public static readonly  OPTIONAL_TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithEnumAndOptionalEnumProperties, string, ReplacedAspectArtifact> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumAndOptionalEnumProperties';
    }

        getContainedType(): string {
            return 'AspectWithEnumAndOptionalEnumProperties';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumAndOptionalEnumProperties',
    'optionalTestProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'1'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'2'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'3')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEnumeration; })()
,
    false,
    true,
    undefined,
        'optionalTestProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEnumAndOptionalEnumProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumAndOptionalEnumProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEnumAndOptionalEnumProperties';
}

getProperties(): Array<StaticProperty<AspectWithEnumAndOptionalEnumProperties, any>> {
return [MetaAspectWithEnumAndOptionalEnumProperties.TEST_PROPERTY, MetaAspectWithEnumAndOptionalEnumProperties.OPTIONAL_TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumAndOptionalEnumProperties, any>> {
        return this.getProperties();
}




    }


