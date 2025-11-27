import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithAnyValueDeclarations,} from './AspectWithAnyValueDeclarations';
import { DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';



    

/*
* Generated class MetaAspectWithAnyValueDeclarations (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAnyValueDeclarations).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithAnyValueDeclarations implements StaticMetaClass<AspectWithAnyValueDeclarations>, PropertyContainer<AspectWithAnyValueDeclarations> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithAnyValueDeclarations';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithAnyValueDeclarations();


 public static readonly  PROPERTY1 = 
                
        new (class extends DefaultStaticProperty<AspectWithAnyValueDeclarations, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithAnyValueDeclarations';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithAnyValueDeclarations',
    'property1',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'green'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'red'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'yellow')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addPreferredName('en' , 'Warning Level');
defaultEnumeration.addDescription('en' , 'Represents if speed of position change is within specification (green), within tolerance (yellow), or outside specification (red).');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'property1',
    false,
    );




getModelClass(): string {
return 'AspectWithAnyValueDeclarations';
}

getAspectModelUrn(): string {
return MetaAspectWithAnyValueDeclarations .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithAnyValueDeclarations';
}

getProperties(): Array<StaticProperty<AspectWithAnyValueDeclarations, any>> {
return [MetaAspectWithAnyValueDeclarations.PROPERTY1];
}

getAllProperties(): Array<StaticProperty<AspectWithAnyValueDeclarations, any>> {
        return this.getProperties();
}




    }


