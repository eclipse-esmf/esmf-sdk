













import { AspectWithConstraints,} from './AspectWithConstraints';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultLengthConstraint,DefaultList,DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultRegularExpressionConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithConstraints implements StaticMetaClass<AspectWithConstraints>, PropertyContainer<AspectWithConstraints> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraints';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstraints();


 public static readonly  TEST_PROPERTY_WITH_REGULAR_EXPRESSION = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : string {
        return object.testPropertyWithRegularExpression;
    }

        setValue( object : AspectWithConstraints, value : string ) {
            object.testPropertyWithRegularExpression = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyWithRegularExpression',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestRegularExpressionConstraint',
'TestRegularExpressionConstraint',
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
'^[a-zA-Z]\\.[0-9]')
regularExpressionConstraint.isAnonymousNode = true;
 return regularExpressionConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestRegularExpressionConstraint';
trait.addPreferredName('en' , 'Test Regular Expression Constraint');
trait.addDescription('en' , 'Test Regular Expression Constraint');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithRegularExpression',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_DECIMAL_MIN_DECIMAL_MAX_RANGE_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : string {
        return object.testPropertyWithDecimalMinDecimalMaxRangeConstraint;
    }

        setValue( object : AspectWithConstraints, value : string ) {
            object.testPropertyWithDecimalMinDecimalMaxRangeConstraint = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyWithDecimalMinDecimalMaxRangeConstraint',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestWithDecimalMinDecimalMaxRangeConstraint',
'TestWithDecimalMinDecimalMaxRangeConstraint',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementDecimal',
'MeasurementDecimal',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond',
'metrePerSecond',
'm\/s','MTS',undefined,'m\/s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronSpeed',
'neutronSpeed',
'neutron speed')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#groupVelocity',
'groupVelocity',
'group velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseSpeedOfElectromagneticWaves',
'phaseSpeedOfElectromagneticWaves',
'phase speed of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocity (speed) on propagation of electromagnetic waves in vacuo')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocityOfSoundPhaseVelocity',
'velocityOfSoundPhaseVelocity',
'velocity of sound (phase velocity)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocity',
'velocity',
'velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocityOfElectromagneticWaves',
'phaseVelocityOfElectromagneticWaves',
'phase velocity of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleVelocity',
'instantaneousSoundParticleVelocity',
'(instantaneous) sound particle velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocity',
'phaseVelocity',
'phase velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond';
defaultUnit.addPreferredName('en' , 'metre per second');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDecimal';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),'2.3'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),'10.5'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestWithDecimalMinDecimalMaxRangeConstraint';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithDecimalMinDecimalMaxRangeConstraint',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_DECIMAL_MAX_RANGE_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : string {
        return object.testPropertyWithDecimalMaxRangeConstraint;
    }

        setValue( object : AspectWithConstraints, value : string ) {
            object.testPropertyWithDecimalMaxRangeConstraint = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyWithDecimalMaxRangeConstraint',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestWithDecimalMaxRangeConstraint',
'TestWithDecimalMaxRangeConstraint',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementDecimal',
'MeasurementDecimal',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond',
'metrePerSecond',
'm\/s','MTS',undefined,'m\/s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronSpeed',
'neutronSpeed',
'neutron speed')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#groupVelocity',
'groupVelocity',
'group velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseSpeedOfElectromagneticWaves',
'phaseSpeedOfElectromagneticWaves',
'phase speed of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocity (speed) on propagation of electromagnetic waves in vacuo')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocityOfSoundPhaseVelocity',
'velocityOfSoundPhaseVelocity',
'velocity of sound (phase velocity)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocity',
'velocity',
'velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocityOfElectromagneticWaves',
'phaseVelocityOfElectromagneticWaves',
'phase velocity of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleVelocity',
'instantaneousSoundParticleVelocity',
'(instantaneous) sound particle velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocity',
'phaseVelocity',
'phase velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond';
defaultUnit.addPreferredName('en' , 'metre per second');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDecimal';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.OPEN,undefined,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),'10.5'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestWithDecimalMaxRangeConstraint';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithDecimalMaxRangeConstraint',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_MIN_MAX_RANGE_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : number {
        return object.testPropertyWithMinMaxRangeConstraint;
    }

        setValue( object : AspectWithConstraints, value : number ) {
            object.testPropertyWithMinMaxRangeConstraint = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyWithMinMaxRangeConstraint',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestWithMinMaxRangeConstraint',
'TestWithMinMaxRangeConstraint',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Measurement',
'Measurement',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond',
'metrePerSecond',
'm\/s','MTS',undefined,'m\/s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronSpeed',
'neutronSpeed',
'neutron speed')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#groupVelocity',
'groupVelocity',
'group velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseSpeedOfElectromagneticWaves',
'phaseSpeedOfElectromagneticWaves',
'phase speed of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocity (speed) on propagation of electromagnetic waves in vacuo')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocityOfSoundPhaseVelocity',
'velocityOfSoundPhaseVelocity',
'velocity of sound (phase velocity)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocity',
'velocity',
'velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocityOfElectromagneticWaves',
'phaseVelocityOfElectromagneticWaves',
'phase velocity of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleVelocity',
'instantaneousSoundParticleVelocity',
'(instantaneous) sound particle velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocity',
'phaseVelocity',
'phase velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond';
defaultUnit.addPreferredName('en' , 'metre per second');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'Measurement';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'1'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'10'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestWithMinMaxRangeConstraint';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithMinMaxRangeConstraint',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_MIN_RANGE_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : number {
        return object.testPropertyWithMinRangeConstraint;
    }

        setValue( object : AspectWithConstraints, value : number ) {
            object.testPropertyWithMinRangeConstraint = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyWithMinRangeConstraint',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestWithMinRangeConstraint',
'TestWithMinRangeConstraint',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Measurement',
'Measurement',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond',
'metrePerSecond',
'm\/s','MTS',undefined,'m\/s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronSpeed',
'neutronSpeed',
'neutron speed')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#groupVelocity',
'groupVelocity',
'group velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseSpeedOfElectromagneticWaves',
'phaseSpeedOfElectromagneticWaves',
'phase speed of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocity (speed) on propagation of electromagnetic waves in vacuo')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocityOfSoundPhaseVelocity',
'velocityOfSoundPhaseVelocity',
'velocity of sound (phase velocity)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocity',
'velocity',
'velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocityOfElectromagneticWaves',
'phaseVelocityOfElectromagneticWaves',
'phase velocity of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleVelocity',
'instantaneousSoundParticleVelocity',
'(instantaneous) sound particle velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocity',
'phaseVelocity',
'phase velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond';
defaultUnit.addPreferredName('en' , 'metre per second');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'Measurement';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.OPEN,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'1'),undefined,)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestWithMinRangeConstraint';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithMinRangeConstraint',
    false,
    );




 public static readonly  TEST_PROPERTY_RANGE_CONSTRAINT_WITH_FLOAT_TYPE = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : number {
        return object.testPropertyRangeConstraintWithFloatType;
    }

        setValue( object : AspectWithConstraints, value : number ) {
            object.testPropertyRangeConstraintWithFloatType = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyRangeConstraintWithFloatType',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestRangeConstraintWithFloatType',
'TestRangeConstraintWithFloatType',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementWithFloatType',
'MeasurementWithFloatType',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond',
'metrePerSecond',
'm\/s','MTS',undefined,'m\/s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronSpeed',
'neutronSpeed',
'neutron speed')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#groupVelocity',
'groupVelocity',
'group velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseSpeedOfElectromagneticWaves',
'phaseSpeedOfElectromagneticWaves',
'phase speed of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocity (speed) on propagation of electromagnetic waves in vacuo')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocityOfSoundPhaseVelocity',
'velocityOfSoundPhaseVelocity',
'velocity of sound (phase velocity)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocity',
'velocity',
'velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocityOfElectromagneticWaves',
'phaseVelocityOfElectromagneticWaves',
'phase velocity of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleVelocity',
'instantaneousSoundParticleVelocity',
'(instantaneous) sound particle velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocity',
'phaseVelocity',
'phase velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond';
defaultUnit.addPreferredName('en' , 'metre per second');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementWithFloatType';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'1.0'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'10.0'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestRangeConstraintWithFloatType';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyRangeConstraintWithFloatType',
    false,
    );




 public static readonly  TEST_PROPERTY_RANGE_CONSTRAINT_WITH_DOUBLE_TYPE = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : number {
        return object.testPropertyRangeConstraintWithDoubleType;
    }

        setValue( object : AspectWithConstraints, value : number ) {
            object.testPropertyRangeConstraintWithDoubleType = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyRangeConstraintWithDoubleType',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestRangeConstraintWithDoubleType',
'TestRangeConstraintWithDoubleType',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementWithDoubleType',
'MeasurementWithDoubleType',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond',
'metrePerSecond',
'm\/s','MTS',undefined,'m\/s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronSpeed',
'neutronSpeed',
'neutron speed')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#groupVelocity',
'groupVelocity',
'group velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseSpeedOfElectromagneticWaves',
'phaseSpeedOfElectromagneticWaves',
'phase speed of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocitySpeedOnPropagationOfElectromagneticWavesInVacuo',
'velocity (speed) on propagation of electromagnetic waves in vacuo')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocityOfSoundPhaseVelocity',
'velocityOfSoundPhaseVelocity',
'velocity of sound (phase velocity)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#velocity',
'velocity',
'velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocityOfElectromagneticWaves',
'phaseVelocityOfElectromagneticWaves',
'phase velocity of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleVelocity',
'instantaneousSoundParticleVelocity',
'(instantaneous) sound particle velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#phaseVelocity',
'phaseVelocity',
'phase velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond';
defaultUnit.addPreferredName('en' , 'metre per second');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementWithDoubleType';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),'1.0'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),'10.0'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestRangeConstraintWithDoubleType';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyRangeConstraintWithDoubleType',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_MIN_MAX_LENGTH_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : string {
        return object.testPropertyWithMinMaxLengthConstraint;
    }

        setValue( object : AspectWithConstraints, value : string ) {
            object.testPropertyWithMinMaxLengthConstraint = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyWithMinMaxLengthConstraint',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestLengthConstraint',
'TestLengthConstraint',
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
1,1,)
lengthConstraint.isAnonymousNode = true;
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestLengthConstraint';
trait.addPreferredName('en' , 'Test Length Constraint');
trait.addDescription('en' , 'Test Length Constraint');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithMinMaxLengthConstraint',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_MIN_LENGTH_CONSTRAINT = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }


    getValue( object : AspectWithConstraints) : string {
        return object.testPropertyWithMinLengthConstraint;
    }

        setValue( object : AspectWithConstraints, value : string ) {
            object.testPropertyWithMinLengthConstraint = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyWithMinLengthConstraint',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestLengthConstraintOnlyMin',
'TestLengthConstraintOnlyMin',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
1,1,)
lengthConstraint.isAnonymousNode = true;
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestLengthConstraintOnlyMin';
trait.addPreferredName('en' , 'Test Length Constraint');
trait.addDescription('en' , 'Test Length Constraint');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithMinLengthConstraint',
    false,
    );




 public static readonly  TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT = 
                
        new (class extends StaticContainerProperty<AspectWithConstraints, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraints';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithConstraints) : string[] {
        return object.testPropertyCollectionLengthConstraint;
    }

        setValue( object : AspectWithConstraints, value : string[] ) {
            object.testPropertyCollectionLengthConstraint = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraints',
    'testPropertyCollectionLengthConstraint',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestLengthConstraintWithCollection',
'TestLengthConstraintWithCollection',
(() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultList.isAnonymousNode = true;
 return defaultList; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
1,1,)
lengthConstraint.isAnonymousNode = true;
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestLengthConstraintWithCollection';
trait.addPreferredName('en' , 'Test Length Constraint with collection');
trait.addDescription('en' , 'Test Length Constraint with collection');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyCollectionLengthConstraint',
    false,
    );




getModelClass(): string {
return 'AspectWithConstraints';
}

getAspectModelUrn(): string {
return MetaAspectWithConstraints .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithConstraints';
}

getProperties(): Array<StaticProperty<AspectWithConstraints, any>> {
return [MetaAspectWithConstraints.TEST_PROPERTY_WITH_REGULAR_EXPRESSION, MetaAspectWithConstraints.TEST_PROPERTY_WITH_DECIMAL_MIN_DECIMAL_MAX_RANGE_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_WITH_DECIMAL_MAX_RANGE_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_WITH_MIN_MAX_RANGE_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_WITH_MIN_RANGE_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_RANGE_CONSTRAINT_WITH_FLOAT_TYPE, MetaAspectWithConstraints.TEST_PROPERTY_RANGE_CONSTRAINT_WITH_DOUBLE_TYPE, MetaAspectWithConstraints.TEST_PROPERTY_WITH_MIN_MAX_LENGTH_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_WITH_MIN_LENGTH_CONSTRAINT, MetaAspectWithConstraints.TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT];
}

getAllProperties(): Array<StaticProperty<AspectWithConstraints, any>> {
        return this.getProperties();
}




    }


