













import { AspectWithConstraintWithSeeAttribute,} from './AspectWithConstraintWithSeeAttribute';
import { DefaultCharacteristic,DefaultLengthConstraint,DefaultRegularExpressionConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithConstraintWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraintWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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


                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(null, 
null, 
null, 
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


                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifactTwo';
defaultCharacteristic.addPreferredName('en' , 'Test Characteristic Two');
defaultCharacteristic.addDescription('en' , 'Test Characteristic Two');
defaultCharacteristic.addSeeReference('http:\/\/example.com\/me2');
 return defaultCharacteristic; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


