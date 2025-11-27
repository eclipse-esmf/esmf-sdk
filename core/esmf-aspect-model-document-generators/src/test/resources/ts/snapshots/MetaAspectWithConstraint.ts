













import { AspectWithConstraint,} from './AspectWithConstraint';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultLengthConstraint,DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultRegularExpressionConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithConstraint implements StaticMetaClass<AspectWithConstraint>, PropertyContainer<AspectWithConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstraint();


 public static readonly  STRING_LC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraint',
    'stringLcProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StringLengthConstraint',
'StringLengthConstraint',
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
20,20,)
lengthConstraint.isAnonymousNode = true;
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'StringLengthConstraint';
trait.addPreferredName('en' , 'Used Test Constraint');
trait.addDescription('en' , 'Used Test Constraint');
trait.addSeeReference('http:\/\/example.com\/');
trait.addSeeReference('http:\/\/example.com\/me');
 return trait; })()
,
    false,
    false,
    undefined,
        'stringLcProperty',
    false,
    );




 public static readonly  DOUBLE_RC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraint',
    'doubleRcProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'DoubleRangeConstraint',
'DoubleRangeConstraint',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'DoubleMeasurement',
'DoubleMeasurement',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecondSquared',
'metrePerSecondSquared',
'm\/s²','MSK',undefined,'m\/s²',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#accelerationDueToGravity',
'accelerationDueToGravity',
'acceleration due to gravity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleAcceleration',
'instantaneousSoundParticleAcceleration',
'(instantaneous) sound particle acceleration')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#acceleration',
'acceleration',
'acceleration')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#accelerationOfFreeFall',
'accelerationOfFreeFall',
'acceleration of free fall')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecondSquared';
defaultUnit.addPreferredName('en' , 'metre per second squared');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'DoubleMeasurement';
defaultMeasurement.addDescription('en' , 'The acceleration');
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),'-0.1'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),'0.2'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'DoubleRangeConstraint';
trait.addPreferredName('en' , 'Test Constraint');
trait.addDescription('en' , 'Test Constraint');
 return trait; })()
,
    false,
    false,
    undefined,
        'doubleRcProperty',
    false,
    );




 public static readonly  INT_RC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraint',
    'intRcProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'IntegerRangeConstraint',
'IntegerRangeConstraint',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'IntegerMeasurement',
'IntegerMeasurement',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecondSquared',
'metrePerSecondSquared',
'm\/s²','MSK',undefined,'m\/s²',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#accelerationDueToGravity',
'accelerationDueToGravity',
'acceleration due to gravity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleAcceleration',
'instantaneousSoundParticleAcceleration',
'(instantaneous) sound particle acceleration')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#acceleration',
'acceleration',
'acceleration')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#accelerationOfFreeFall',
'accelerationOfFreeFall',
'acceleration of free fall')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecondSquared';
defaultUnit.addPreferredName('en' , 'metre per second squared');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'IntegerMeasurement';
defaultMeasurement.addDescription('en' , 'The acceleration');
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'-1'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'-1'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'IntegerRangeConstraint';
trait.addPreferredName('en' , 'Test Constraint');
trait.addDescription('en' , 'Test Constraint');
 return trait; })()
,
    false,
    false,
    undefined,
        'intRcProperty',
    false,
    );




 public static readonly  BIG_INT_RC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraint',
    'bigIntRcProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'BigIntegerRangeConstraint',
'BigIntegerRangeConstraint',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'BigIntegerMeasurement',
'BigIntegerMeasurement',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecondSquared',
'metrePerSecondSquared',
'm\/s²','MSK',undefined,'m\/s²',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#accelerationDueToGravity',
'accelerationDueToGravity',
'acceleration due to gravity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleAcceleration',
'instantaneousSoundParticleAcceleration',
'(instantaneous) sound particle acceleration')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#acceleration',
'acceleration',
'acceleration')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#accelerationOfFreeFall',
'accelerationOfFreeFall',
'acceleration of free fall')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecondSquared';
defaultUnit.addPreferredName('en' , 'metre per second squared');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'BigIntegerMeasurement';
defaultMeasurement.addDescription('en' , 'The acceleration');
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'10'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'15'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'BigIntegerRangeConstraint';
trait.addPreferredName('en' , 'Test Constraint');
trait.addDescription('en' , 'Test Constraint');
 return trait; })()
,
    false,
    false,
    undefined,
        'bigIntRcProperty',
    false,
    );




 public static readonly  FLOAT_RC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraint',
    'floatRcProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'FloatRangeConstraint',
'FloatRangeConstraint',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'FloatMeasurement',
'FloatMeasurement',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecondSquared',
'metrePerSecondSquared',
'm\/s²','MSK',undefined,'m\/s²',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#accelerationDueToGravity',
'accelerationDueToGravity',
'acceleration due to gravity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleAcceleration',
'instantaneousSoundParticleAcceleration',
'(instantaneous) sound particle acceleration')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#acceleration',
'acceleration',
'acceleration')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#accelerationOfFreeFall',
'accelerationOfFreeFall',
'acceleration of free fall')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecondSquared';
defaultUnit.addPreferredName('en' , 'metre per second squared');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'FloatMeasurement';
defaultMeasurement.addDescription('en' , 'The acceleration');
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'100.0'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'112.0'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'FloatRangeConstraint';
trait.addPreferredName('en' , 'Test Constraint');
trait.addDescription('en' , 'Test Constraint');
 return trait; })()
,
    false,
    false,
    undefined,
        'floatRcProperty',
    false,
    );




 public static readonly  STRING_REGEXC_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraint';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraint',
    'stringRegexcProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'RegularExpressionConstraint',
'RegularExpressionConstraint',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
'[a-zA-Z]')
regularExpressionConstraint.isAnonymousNode = true;
 return regularExpressionConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'RegularExpressionConstraint';
trait.addPreferredName('en' , 'Test Regular Expression Constraint');
trait.addDescription('en' , 'Test Regular Expression Constraint');
 return trait; })()
,
    false,
    false,
    undefined,
        'stringRegexcProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithConstraint';
}

getProperties(): Array<StaticProperty<AspectWithConstraint, any>> {
return [MetaAspectWithConstraint.STRING_LC_PROPERTY, MetaAspectWithConstraint.DOUBLE_RC_PROPERTY, MetaAspectWithConstraint.INT_RC_PROPERTY, MetaAspectWithConstraint.BIG_INT_RC_PROPERTY, MetaAspectWithConstraint.FLOAT_RC_PROPERTY, MetaAspectWithConstraint.STRING_REGEXC_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithConstraint, any>> {
        return this.getProperties();
}




    }


