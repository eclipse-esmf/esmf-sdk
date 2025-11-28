













import { AspectWithFixedPoint,} from './AspectWithFixedPoint';
import { DefaultFixedPointConstraint,DefaultMeasurement,DefaultQuantityKind,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithFixedPoint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithFixedPoint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithFixedPoint implements StaticMetaClass<AspectWithFixedPoint>, PropertyContainer<AspectWithFixedPoint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithFixedPoint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithFixedPoint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithFixedPoint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithFixedPoint';
    }


    getValue( object : AspectWithFixedPoint) : string {
        return object.testProperty;
    }

        setValue( object : AspectWithFixedPoint, value : string ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithFixedPoint',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestFixedPoint',
'TestFixedPoint',
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
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'Measurement';
 return defaultMeasurement; })(),[(() => { const defaultFixedPointConstraint = new DefaultFixedPointConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
5,
3)
defaultFixedPointConstraint.isAnonymousNode = true;
defaultFixedPointConstraint.addPreferredName('en' , 'Test Fixed Point');
defaultFixedPointConstraint.addDescription('en' , 'This is a test fixed point constraint.');
defaultFixedPointConstraint.addSeeReference('http:\/\/example.com\/');
 return defaultFixedPointConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestFixedPoint';
 return trait; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithFixedPoint';
}

getAspectModelUrn(): string {
return MetaAspectWithFixedPoint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithFixedPoint';
}

getProperties(): Array<StaticProperty<AspectWithFixedPoint, any>> {
return [MetaAspectWithFixedPoint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithFixedPoint, any>> {
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


