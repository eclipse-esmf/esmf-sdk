import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumerationWithSeeAttribute,} from './AspectWithEnumerationWithSeeAttribute';
import { DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithEnumerationWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumerationWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEnumerationWithSeeAttribute implements StaticMetaClass<AspectWithEnumerationWithSeeAttribute>, PropertyContainer<AspectWithEnumerationWithSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumerationWithSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumerationWithSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumerationWithSeeAttribute, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumerationWithSeeAttribute';
    }


    getValue( object : AspectWithEnumerationWithSeeAttribute) : ReplacedAspectArtifact {
        return object.testProperty;
    }

        setValue( object : AspectWithEnumerationWithSeeAttribute, value : ReplacedAspectArtifact ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumerationWithSeeAttribute',
    'testProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'bar'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'foo')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addPreferredName('en' , 'Test Enumeration');
defaultEnumeration.addDescription('en' , 'Test Enumeration');
defaultEnumeration.addSeeReference('http:\/\/example.com\/');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumerationWithSeeAttribute, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumerationWithSeeAttribute';
    }


    getValue( object : AspectWithEnumerationWithSeeAttribute) : ReplacedAspectArtifact {
        return object.testPropertyTwo;
    }

        setValue( object : AspectWithEnumerationWithSeeAttribute, value : ReplacedAspectArtifact ) {
            object.testPropertyTwo = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumerationWithSeeAttribute',
    'testPropertyTwo',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'bar'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'foo')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addPreferredName('en' , 'Test Enumeration Two');
defaultEnumeration.addDescription('en' , 'Test Enumeration Two');
defaultEnumeration.addSeeReference('http:\/\/example.com\/');
defaultEnumeration.addSeeReference('http:\/\/example.com\/me');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testPropertyTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithEnumerationWithSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumerationWithSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEnumerationWithSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithEnumerationWithSeeAttribute, any>> {
return [MetaAspectWithEnumerationWithSeeAttribute.TEST_PROPERTY, MetaAspectWithEnumerationWithSeeAttribute.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumerationWithSeeAttribute, any>> {
        return this.getProperties();
}




    }


