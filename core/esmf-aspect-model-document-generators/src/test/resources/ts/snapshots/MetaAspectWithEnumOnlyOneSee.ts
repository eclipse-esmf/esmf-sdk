import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumOnlyOneSee,} from './AspectWithEnumOnlyOneSee';
import { DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithEnumOnlyOneSee (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumOnlyOneSee).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEnumOnlyOneSee implements StaticMetaClass<AspectWithEnumOnlyOneSee>, PropertyContainer<AspectWithEnumOnlyOneSee> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumOnlyOneSee';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumOnlyOneSee();


 public static readonly  PROP1 = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumOnlyOneSee, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumOnlyOneSee';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumOnlyOneSee',
    'prop1',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'a'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'b')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'prop1',
    false,
    );




 public static readonly  PROP2 = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumOnlyOneSee, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumOnlyOneSee';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumOnlyOneSee',
    'prop2',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'1'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'2')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addSeeReference('https:\/\/test.com');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'prop2',
    false,
    );




getModelClass(): string {
return 'AspectWithEnumOnlyOneSee';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumOnlyOneSee .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEnumOnlyOneSee';
}

getProperties(): Array<StaticProperty<AspectWithEnumOnlyOneSee, any>> {
return [MetaAspectWithEnumOnlyOneSee.PROP1, MetaAspectWithEnumOnlyOneSee.PROP2];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumOnlyOneSee, any>> {
        return this.getProperties();
}




    }


