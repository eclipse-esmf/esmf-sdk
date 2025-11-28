













import { AspectWithCollectionWithElementConstraint,} from './AspectWithCollectionWithElementConstraint';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCollection,DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithCollectionWithElementConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithElementConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCollectionWithElementConstraint implements StaticMetaClass<AspectWithCollectionWithElementConstraint>, PropertyContainer<AspectWithCollectionWithElementConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithElementConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionWithElementConstraint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithElementConstraint, number, number[]> {

    
    getPropertyType(): string {
            return 'number';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithElementConstraint';
    }

        getContainedType(): string {
            return 'number';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollectionWithElementConstraint',
    'testProperty',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestCollection',
'TestCollection',
true, false, (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'Measurement';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'2.3'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'10.5'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.isAnonymousNode = true;
 return trait; })(),
new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollection';
defaultCollection.addPreferredName('en' , 'Test Collection');
defaultCollection.addDescription('en' , 'This is a test collection.');
defaultCollection.addSeeReference('http:\/\/example.com\/');
 return defaultCollection; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'5.0'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCollectionWithElementConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionWithElementConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCollectionWithElementConstraint';
}

getProperties(): Array<StaticProperty<AspectWithCollectionWithElementConstraint, any>> {
return [MetaAspectWithCollectionWithElementConstraint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionWithElementConstraint, any>> {
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


