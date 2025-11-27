import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithCurieEnumeration,} from './AspectWithCurieEnumeration';
import { DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';



    

/*
* Generated class MetaAspectWithCurieEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCurieEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCurieEnumeration implements StaticMetaClass<AspectWithCurieEnumeration>, PropertyContainer<AspectWithCurieEnumeration> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCurieEnumeration';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCurieEnumeration();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithCurieEnumeration, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithCurieEnumeration';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCurieEnumeration',
    'testProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ),'unit:gram'),
new DefaultScalarValue(new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ),'unit:hectopascal')],new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addPreferredName('en' , 'Test Enumeration');
defaultEnumeration.addDescription('en' , 'This is a test for enumeration.');
 return defaultEnumeration; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ),'unit:hectopascal'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCurieEnumeration';
}

getAspectModelUrn(): string {
return MetaAspectWithCurieEnumeration .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCurieEnumeration';
}

getProperties(): Array<StaticProperty<AspectWithCurieEnumeration, any>> {
return [MetaAspectWithCurieEnumeration.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCurieEnumeration, any>> {
        return this.getProperties();
}




    }


