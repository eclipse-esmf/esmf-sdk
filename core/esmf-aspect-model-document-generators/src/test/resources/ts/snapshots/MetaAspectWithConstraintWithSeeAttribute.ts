













import { AspectWithConstraintWithSeeAttribute,} from './AspectWithConstraintWithSeeAttribute';
import { DefaultCharacteristic,DefaultLengthConstraint,DefaultRegularExpressionConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithConstraintWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraintWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithConstraintWithSeeAttribute implements StaticMetaClass<AspectWithConstraintWithSeeAttribute>, PropertyContainer<AspectWithConstraintWithSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraintWithSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstraintWithSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraintWithSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraintWithSeeAttribute';
    }


    getValue( object : AspectWithConstraintWithSeeAttribute) : string {
        return object.testProperty;
    }

        setValue( object : AspectWithConstraintWithSeeAttribute, value : string ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraintWithSeeAttribute',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestTrait',
'TestTrait',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
5,5,)
lengthConstraint.isAnonymousNode = true;
lengthConstraint.addPreferredName('en' , 'Test Constraint');
lengthConstraint.addDescription('en' , 'Test Constraint');
lengthConstraint.addSeeReference('http:\/\/example.com\/');
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestTrait';
 return trait; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraintWithSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraintWithSeeAttribute';
    }


    getValue( object : AspectWithConstraintWithSeeAttribute) : string {
        return object.testPropertyTwo;
    }

        setValue( object : AspectWithConstraintWithSeeAttribute, value : string ) {
            object.testPropertyTwo = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraintWithSeeAttribute',
    'testPropertyTwo',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestTraitTwo',
'TestTraitTwo',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifactTwo',
'ReplacedAspectArtifactTwo',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactTwo';
defaultCharacteristic.addPreferredName('en' , 'Test Characteristic Two');
defaultCharacteristic.addDescription('en' , 'Test Characteristic Two');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/me2');
 return defaultCharacteristic; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
'^[A-Z][A-Z][A-Z]$')
regularExpressionConstraint.isAnonymousNode = true;
regularExpressionConstraint.addPreferredName('en' , 'Test Constraint Two');
regularExpressionConstraint.addDescription('en' , 'Test Constraint Two');
regularExpressionConstraint.addSeeReference('http:\/\/example.com\/me');
 return regularExpressionConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestTraitTwo';
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithConstraintWithSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithConstraintWithSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithConstraintWithSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithConstraintWithSeeAttribute, any>> {
return [MetaAspectWithConstraintWithSeeAttribute.TEST_PROPERTY, MetaAspectWithConstraintWithSeeAttribute.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithConstraintWithSeeAttribute, any>> {
        return this.getProperties();
}




    }


