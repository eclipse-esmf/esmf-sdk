import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumerationWithMultipleSeeAttributes,} from './AspectWithEnumerationWithMultipleSeeAttributes';
import { DefaultEnumeration,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithEnumerationWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumerationWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumerationWithMultipleSeeAttributes implements StaticMetaClass<AspectWithEnumerationWithMultipleSeeAttributes>, PropertyContainer<AspectWithEnumerationWithMultipleSeeAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumerationWithMultipleSeeAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumerationWithMultipleSeeAttributes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumerationWithMultipleSeeAttributes, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumerationWithMultipleSeeAttributes';
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
defaultEnumeration.addPreferredName('en' , 'Test Enumeration');
defaultEnumeration.addDescription('en' , 'Test Enumeration');
defaultEnumeration.addSeeReference('http:\/\/example.com\/');
defaultEnumeration.addSeeReference('http:\/\/example.com\/me');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEnumerationWithMultipleSeeAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumerationWithMultipleSeeAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEnumerationWithMultipleSeeAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithEnumerationWithMultipleSeeAttributes, any>> {
return [MetaAspectWithEnumerationWithMultipleSeeAttributes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumerationWithMultipleSeeAttributes, any>> {
    return this.getProperties();
}




    }


