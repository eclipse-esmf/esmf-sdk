













import { AspectWithRangeConstraintOnConstrainedNumericType,} from './AspectWithRangeConstraintOnConstrainedNumericType';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithRangeConstraintOnConstrainedNumericType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintOnConstrainedNumericType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithRangeConstraintOnConstrainedNumericType implements StaticMetaClass<AspectWithRangeConstraintOnConstrainedNumericType>, PropertyContainer<AspectWithRangeConstraintOnConstrainedNumericType> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintOnConstrainedNumericType';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintOnConstrainedNumericType();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintOnConstrainedNumericType, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintOnConstrainedNumericType';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithRangeConstraintOnConstrainedNumericType',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestRangeConstraint',
'TestRangeConstraint',
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
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#short" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'Measurement';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.OPEN,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#short" ),'5'),undefined,)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestRangeConstraint';
trait.addPreferredName('en' , 'Test Range Constraint');
trait.addDescription('en' , 'This is a test range constraint.');
trait.addSeeReference('http:\/\/example.com\/');
 return trait; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithRangeConstraintOnConstrainedNumericType';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintOnConstrainedNumericType .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintOnConstrainedNumericType';
}

getProperties(): Array<StaticProperty<AspectWithRangeConstraintOnConstrainedNumericType, any>> {
return [MetaAspectWithRangeConstraintOnConstrainedNumericType.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintOnConstrainedNumericType, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This is a test description', language: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


