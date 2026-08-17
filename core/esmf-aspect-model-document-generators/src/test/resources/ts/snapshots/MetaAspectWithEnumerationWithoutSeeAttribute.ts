import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumerationWithoutSeeAttribute,} from './AspectWithEnumerationWithoutSeeAttribute';
import { DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';



    

/*
* Generated class MetaAspectWithEnumerationWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumerationWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEnumerationWithoutSeeAttribute implements StaticMetaClass<AspectWithEnumerationWithoutSeeAttribute>, PropertyContainer<AspectWithEnumerationWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumerationWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumerationWithoutSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumerationWithoutSeeAttribute, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumerationWithoutSeeAttribute';
    }


    getValue( object : AspectWithEnumerationWithoutSeeAttribute) : ReplacedAspectArtifact {
        return object.testProperty;
    }

        setValue( object : AspectWithEnumerationWithoutSeeAttribute, value : ReplacedAspectArtifact ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumerationWithoutSeeAttribute',
    'testProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'bar'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'foo')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addPreferredName('en' , 'Test Enumeration');
defaultEnumeration.addDescription('en' , 'Test Enumeration');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEnumerationWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumerationWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEnumerationWithoutSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithEnumerationWithoutSeeAttribute, any>> {
return [MetaAspectWithEnumerationWithoutSeeAttribute.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumerationWithoutSeeAttribute, any>> {
        return this.getProperties();
}




    }


