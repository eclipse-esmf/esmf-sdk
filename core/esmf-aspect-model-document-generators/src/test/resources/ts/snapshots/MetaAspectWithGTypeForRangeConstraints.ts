













import { AspectWithGTypeForRangeConstraints,} from './AspectWithGTypeForRangeConstraints';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithGTypeForRangeConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithGTypeForRangeConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithGTypeForRangeConstraints implements StaticMetaClass<AspectWithGTypeForRangeConstraints>, PropertyContainer<AspectWithGTypeForRangeConstraints> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithGTypeForRangeConstraints';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithGTypeForRangeConstraints();


 public static readonly  TEST_PROPERTY_WITH_G_YEAR = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithGTypeForRangeConstraints',
    'testPropertyWithGYear',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGYear',
'testWithGregorianCalendarMinGregorianCalendarMaxGYear',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementGYear',
'MeasurementGYear',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#year',
'year',
'y','ANN','secondUnitOfTime','3.15576 × 10⁷ s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#time',
'time',
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#year';
defaultUnit.addPreferredName('en' , 'year');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYear" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementGYear';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYear" ),'2000'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYear" ),'2001'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGYear';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithGYear',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_G_MONTH = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithGTypeForRangeConstraints',
    'testPropertyWithGMonth',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGMonth',
'testWithGregorianCalendarMinGregorianCalendarMaxGMonth',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementGMonth',
'MeasurementGMonth',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#month',
'month',
'mo','MON','secondUnitOfTime','2.629800 × 10⁶ s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#time',
'time',
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#month';
defaultUnit.addPreferredName('en' , 'month');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonth" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementGMonth';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonth" ),'--04'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonth" ),'--05'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGMonth';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithGMonth',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_G_DAY = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithGTypeForRangeConstraints',
    'testPropertyWithGDay',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGDay',
'testWithGregorianCalendarMinGregorianCalendarMaxGDay',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementGDay',
'MeasurementGDay',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#day',
'day',
'd','DAY','secondUnitOfTime','86400 s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#time',
'time',
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#day';
defaultUnit.addPreferredName('en' , 'day');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#gDay" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementGDay';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gDay" ),'---04'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gDay" ),'---05'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGDay';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithGDay',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_G_YEAR_MONTH = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithGTypeForRangeConstraints',
    'testPropertyWithGYearMonth',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGYearMonth',
'testWithGregorianCalendarMinGregorianCalendarMaxGYearMonth',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementGYearMonth',
'MeasurementGYearMonth',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#one',
'one',
'1','C62',undefined,'1',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#effectiveMultiplicationFactor',
'effectiveMultiplicationFactor',
'effective multiplication factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralLuminousEfficiency',
'spectralLuminousEfficiency',
'spectral luminous efficiency')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardAbsoluteActivityOfBInALiquidOrASolidMixture',
'standardAbsoluteActivityOfBInALiquidOrASolidMixture',
'standard absolute activity of B (in a liquid or a solid mixture)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grandPartitionFunction',
'grandPartitionFunction',
'grand partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#ratioOfTheMassicHeatCapacity',
'ratioOfTheMassicHeatCapacity',
'ratio of the massic heat capacity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#shortRangeOrderParameter',
'shortRangeOrderParameter',
'short-range order parameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#lewisNumber',
'lewisNumber',
'Lewis number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#activityOfSolventA',
'activityOfSolventA',
'activity of solvent A')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeActivityOfSolventAEspeciallyInADiluteSolution',
'relativeActivityOfSolventAEspeciallyInADiluteSolution',
'relative activity of solvent A (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#knudsenNumber',
'knudsenNumber',
'Knudsen number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#bindingFraction',
'bindingFraction',
'binding fraction')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#activityCoefficientOfBInALiquidAsASolidMixture',
'activityCoefficientOfBInALiquidAsASolidMixture',
'activity coefficient of B (in a liquid as a solid mixture)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#activityCoefficientOfSoluteBEspeciallyInADiluteSolution',
'activityCoefficientOfSoluteBEspeciallyInADiluteSolution',
'activity coefficient of solute B (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hartmannNumber',
'hartmannNumber',
'Hartmann number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hyperfineStructureQuantumNumber',
'hyperfineStructureQuantumNumber',
'hyperfine structure quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#weberNumber',
'weberNumber',
'Weber number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#orderOfReflexion',
'orderOfReflexion',
'order of reflexion')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#infiniteMediumMultiplicationFactor',
'infiniteMediumMultiplicationFactor',
'infinite medium multiplication factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#numberOfPhases',
'numberOfPhases',
'number of phases')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#stantonNumberForMassTransfer',
'stantonNumberForMassTransfer',
'Stanton number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#numberOfTurnsInAWinding',
'numberOfTurnsInAWinding',
'number of turns in a winding')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#dissipance',
'dissipance',
'dissipance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralTransmissionFactor',
'spectralTransmissionFactor',
'spectral transmission factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#luminousEfficiencyAtASpecifiedWavelength',
'luminousEfficiencyAtASpecifiedWavelength',
'luminous efficiency at a specified wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#protonNumber',
'protonNumber',
'proton number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#couplingCoefficient',
'couplingCoefficient',
'coupling coefficient')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#leakageCoefficient',
'leakageCoefficient',
'leakage coefficient')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#cowlingNumber',
'cowlingNumber',
'Cowling number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#transmissionFactor',
'transmissionFactor',
'transmission factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#prandtlNumber',
'prandtlNumber',
'Prandtl number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralRadianceFactor',
'spectralRadianceFactor',
'spectral radiance factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#alfvenNumber',
'alfvenNumber',
'Alfven number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#poissonRatio',
'poissonRatio',
'poisson ratio')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralTransmittance',
'spectralTransmittance',
'spectral transmittance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativePermittivity',
'relativePermittivity',
'relative permittivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#electricSusceptibility',
'electricSusceptibility',
'electric susceptibility')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralAbsorptionFactor',
'spectralAbsorptionFactor',
'spectral absorption factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#froudeNumber',
'froudeNumber',
'Froude number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#poissonNumber',
'poissonNumber',
'poisson number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#magneticQuantumNumber',
'magneticQuantumNumber',
'magnetic quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spinAngularMomentumQuantumNumber',
'spinAngularMomentumQuantumNumber',
'spin angular momentum quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#numberOfMoleculesOrOtherElementaryEntities',
'numberOfMoleculesOrOtherElementaryEntities',
'number of molecules or other elementary entities')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#restMassOfProton',
'restMassOfProton',
'(rest) mass of proton')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grandCanonicalPartitionFunction',
'grandCanonicalPartitionFunction',
'grand-canonical partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#moleFractionOfB',
'moleFractionOfB',
'mole fraction of B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#directionalSpectralEmissivity',
'directionalSpectralEmissivity',
'directional spectral emissivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#absorptionFactor',
'absorptionFactor',
'absorption factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#principleQuantumNumber',
'principleQuantumNumber',
'principle quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reactivity',
'reactivity',
'reactivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeMassDefect',
'relativeMassDefect',
'relative mass defect')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#molecularPartitionFunction',
'molecularPartitionFunction',
'molecular partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#landauGinzburgNumber',
'landauGinzburgNumber',
'Landau-Ginzburg number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#canonicalPartitionFunction',
'canonicalPartitionFunction',
'canonical partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#orbitalAngularMomentumQuantumNumber',
'orbitalAngularMomentumQuantumNumber',
'orbital angular momentum quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nusseltNumberForMassTransfer',
'nusseltNumberForMassTransfer',
'Nusselt number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fastFissionFactor',
'fastFissionFactor',
'fast fission factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#schmidtNumber',
'schmidtNumber',
'Schmidt number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#magneticSusceptibility',
'magneticSusceptibility',
'magnetic susceptibility')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thermalDiffusionRatio',
'thermalDiffusionRatio',
'thermal diffusion ratio')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#pecletNumber',
'pecletNumber',
'Peclet number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#transportNumberOfIonB',
'transportNumberOfIonB',
'transport number of ion B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#rayleighNumber',
'rayleighNumber',
'Rayleigh number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#stoichiometricNumberOfB',
'stoichiometricNumberOfB',
'stoichiometric number of B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralAbsorptance',
'spectralAbsorptance',
'spectral absorptance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#strouhalNumber',
'strouhalNumber',
'Strouhal number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#opticalDensity',
'opticalDensity',
'optical density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#shearStrain',
'shearStrain',
'shear strain')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nusseltNumber',
'nusseltNumber',
'Nusselt number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralReflectance',
'spectralReflectance',
'spectral reflectance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#restMassOfNeutron',
'restMassOfNeutron',
'(rest) mass of neutron')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fourierNumberForMassTransfer',
'fourierNumberForMassTransfer',
'Fourier number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralEmissivity',
'spectralEmissivity',
'spectral emissivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeMassExcess',
'relativeMassExcess',
'relative mass excess')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronYieldPerFission',
'neutronYieldPerFission',
'neutron yield per fission')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#linearStrain',
'linearStrain',
'linear strain')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#totalAngularMomentumQuantumNumber',
'totalAngularMomentumQuantumNumber',
'total angular momentum quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralReflectionfactor',
'spectralReflectionfactor',
'spectral reflectionfactor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#isentropicExponent',
'isentropicExponent',
'isentropic exponent')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#moleRatioOfSoluteB',
'moleRatioOfSoluteB',
'mole ratio of solute B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reynoldsNumber',
'reynoldsNumber',
'Reynolds number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#stantonNumber',
'stantonNumber',
'Stanton number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#restMassOfElectron',
'restMassOfElectron',
'(rest) mass of electron')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardAbsoluteActivityOfSolventAEspeciallyInADiluteSolution',
'standardAbsoluteActivityOfSolventAEspeciallyInADiluteSolution',
'standard absolute activity of solvent A (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#partitionFunctionOfAMolecule',
'partitionFunctionOfAMolecule',
'partition function of a molecule')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#machNumber',
'machNumber',
'Mach number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#numberOfPairsOfPoles',
'numberOfPairsOfPoles',
'number of pairs of poles')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grashofNumber',
'grashofNumber',
'Grashof number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#averageLogarithmicEnergyDecrement',
'averageLogarithmicEnergyDecrement',
'average logarithmic energy decrement')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fineStructureConstant',
'fineStructureConstant',
'fine structure constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#mobilityRatio',
'mobilityRatio',
'mobility ratio')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#dissipationFactor',
'dissipationFactor',
'dissipation factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronNumber',
'neutronNumber',
'neutron number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#internalConversionFactor',
'internalConversionFactor',
'internal conversion factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reflectionFactor',
'reflectionFactor',
'reflection factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#frictionFactor',
'frictionFactor',
'friction factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thermalUtilizationFactor',
'thermalUtilizationFactor',
'thermal utilization factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#atomicNumber',
'atomicNumber',
'atomic number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#transmittance',
'transmittance',
'transmittance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#lethargy',
'lethargy',
'lethargy')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#longRangeOrderParameter',
'longRangeOrderParameter',
'long-range order parameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grueneisenParameter',
'grueneisenParameter',
'Grüneisen parameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grashofNumberForMassTransfer',
'grashofNumberForMassTransfer',
'Grashof number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#pecletNumberForMassTransfer',
'pecletNumberForMassTransfer',
'Peclet number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#volumeOrBulkStrain',
'volumeOrBulkStrain',
'volume or bulk strain')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#currentFractionOfIonB',
'currentFractionOfIonB',
'current fraction of ion B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reflectance',
'reflectance',
'reflectance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#osmoticCoefficientOfTheSolventAEspeciallyInADiluteSolution',
'osmoticCoefficientOfTheSolventAEspeciallyInADiluteSolution',
'osmotic coefficient of the solvent A (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#multiplicationFactor',
'multiplicationFactor',
'multiplication factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nonLeakageProbability',
'nonLeakageProbability',
'non leakage probability')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#statisticalWeight',
'statisticalWeight',
'statistical weight')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#emissivityAtASpecifiedWavelength',
'emissivityAtASpecifiedWavelength',
'emissivity at a specified wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#emissivity',
'emissivity',
'emissivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fourierNumber',
'fourierNumber',
'Fourier number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#coefficientOfFriction',
'coefficientOfFriction',
'coefficient of friction')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#massNumber',
'massNumber',
'mass number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeMassDensity',
'relativeMassDensity',
'relative mass density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thermalDiffusionFactor',
'thermalDiffusionFactor',
'thermal diffusion factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeElongation',
'relativeElongation',
'relative elongation')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#coordinatesTrichromatic',
'coordinatesTrichromatic',
'coordinates trichromatic')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nucleonNumber',
'nucleonNumber',
'nucleon number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#luminousEfficiency',
'luminousEfficiency',
'luminous efficiency')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeDensity',
'relativeDensity',
'relative density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#chargeNumberOfIon',
'chargeNumberOfIon',
'charge number of ion')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#debyeWalleFactor',
'debyeWalleFactor',
'Debye-Walle factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardAbsoluteActivityOfSoluteBEspeciallyInADiluteSolution',
'standardAbsoluteActivityOfSoluteBEspeciallyInADiluteSolution',
'standard absolute activity of solute B (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#absoluteActivity',
'absoluteActivity',
'absolute activity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#magneticReynoldsNumber',
'magneticReynoldsNumber',
'magnetic Reynolds number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeAtomicMass',
'relativeAtomicMass',
'relative atomic mass')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#ratioOfTheSpecificHeatCapacities',
'ratioOfTheSpecificHeatCapacities',
'ratio of the specific heat capacities')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativePermeability',
'relativePermeability',
'relative permeability')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#packingFraction',
'packingFraction',
'packing fraction')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardAbsoluteActivityOfBInAGaseousMixture',
'standardAbsoluteActivityOfBInAGaseousMixture',
'standard absolute activity of B (in a gaseous mixture)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronYieldPerAbsorption',
'neutronYieldPerAbsorption',
'neutron yield per absorption')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#gFactorOfAtomOrElectron',
'gFactorOfAtomOrElectron',
'g-factor of atom or electron')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#gFactorOfNucleus',
'gFactorOfNucleus',
'g-factor of nucleus')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeMolecularMass',
'relativeMolecularMass',
'relative molecular mass')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#madelungConstant',
'madelungConstant',
'Madelung constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#refractiveIndex',
'refractiveIndex',
'refractive index')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#microcanonicalPartitionFunction',
'microcanonicalPartitionFunction',
'microcanonical partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#eulerNumber',
'eulerNumber',
'Euler number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#resonanceEscapeProbability',
'resonanceEscapeProbability',
'resonance escape probability')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nuclearSpinQuantumNumber',
'nuclearSpinQuantumNumber',
'nuclear spin quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#absorbance',
'absorbance',
'absorbance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#degreeOfDissociation',
'degreeOfDissociation',
'degree of dissociation')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#cieColorimetricFunctions',
'cieColorimetricFunctions',
'CIE colorimetric functions')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardEquilibriumConstant',
'standardEquilibriumConstant',
'standard equilibrium constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#one';
defaultUnit.addPreferredName('en' , 'one');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYearMonth" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementGYearMonth';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYearMonth" ),'2000-01'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYearMonth" ),'2000-02'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGYearMonth';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithGYearMonth',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_G_MONTH_YEAR = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithGTypeForRangeConstraints',
    'testPropertyWithGMonthYear',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGMonthYear',
'testWithGregorianCalendarMinGregorianCalendarMaxGMonthYear',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementGMonthYear',
'MeasurementGMonthYear',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#one',
'one',
'1','C62',undefined,'1',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#effectiveMultiplicationFactor',
'effectiveMultiplicationFactor',
'effective multiplication factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralLuminousEfficiency',
'spectralLuminousEfficiency',
'spectral luminous efficiency')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardAbsoluteActivityOfBInALiquidOrASolidMixture',
'standardAbsoluteActivityOfBInALiquidOrASolidMixture',
'standard absolute activity of B (in a liquid or a solid mixture)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grandPartitionFunction',
'grandPartitionFunction',
'grand partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#ratioOfTheMassicHeatCapacity',
'ratioOfTheMassicHeatCapacity',
'ratio of the massic heat capacity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#shortRangeOrderParameter',
'shortRangeOrderParameter',
'short-range order parameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#lewisNumber',
'lewisNumber',
'Lewis number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#activityOfSolventA',
'activityOfSolventA',
'activity of solvent A')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeActivityOfSolventAEspeciallyInADiluteSolution',
'relativeActivityOfSolventAEspeciallyInADiluteSolution',
'relative activity of solvent A (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#knudsenNumber',
'knudsenNumber',
'Knudsen number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#bindingFraction',
'bindingFraction',
'binding fraction')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#activityCoefficientOfBInALiquidAsASolidMixture',
'activityCoefficientOfBInALiquidAsASolidMixture',
'activity coefficient of B (in a liquid as a solid mixture)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#activityCoefficientOfSoluteBEspeciallyInADiluteSolution',
'activityCoefficientOfSoluteBEspeciallyInADiluteSolution',
'activity coefficient of solute B (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hartmannNumber',
'hartmannNumber',
'Hartmann number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hyperfineStructureQuantumNumber',
'hyperfineStructureQuantumNumber',
'hyperfine structure quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#weberNumber',
'weberNumber',
'Weber number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#orderOfReflexion',
'orderOfReflexion',
'order of reflexion')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#infiniteMediumMultiplicationFactor',
'infiniteMediumMultiplicationFactor',
'infinite medium multiplication factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#numberOfPhases',
'numberOfPhases',
'number of phases')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#stantonNumberForMassTransfer',
'stantonNumberForMassTransfer',
'Stanton number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#numberOfTurnsInAWinding',
'numberOfTurnsInAWinding',
'number of turns in a winding')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#dissipance',
'dissipance',
'dissipance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralTransmissionFactor',
'spectralTransmissionFactor',
'spectral transmission factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#luminousEfficiencyAtASpecifiedWavelength',
'luminousEfficiencyAtASpecifiedWavelength',
'luminous efficiency at a specified wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#protonNumber',
'protonNumber',
'proton number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#couplingCoefficient',
'couplingCoefficient',
'coupling coefficient')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#leakageCoefficient',
'leakageCoefficient',
'leakage coefficient')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#cowlingNumber',
'cowlingNumber',
'Cowling number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#transmissionFactor',
'transmissionFactor',
'transmission factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#prandtlNumber',
'prandtlNumber',
'Prandtl number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralRadianceFactor',
'spectralRadianceFactor',
'spectral radiance factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#alfvenNumber',
'alfvenNumber',
'Alfven number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#poissonRatio',
'poissonRatio',
'poisson ratio')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralTransmittance',
'spectralTransmittance',
'spectral transmittance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativePermittivity',
'relativePermittivity',
'relative permittivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#electricSusceptibility',
'electricSusceptibility',
'electric susceptibility')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralAbsorptionFactor',
'spectralAbsorptionFactor',
'spectral absorption factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#froudeNumber',
'froudeNumber',
'Froude number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#poissonNumber',
'poissonNumber',
'poisson number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#magneticQuantumNumber',
'magneticQuantumNumber',
'magnetic quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spinAngularMomentumQuantumNumber',
'spinAngularMomentumQuantumNumber',
'spin angular momentum quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#numberOfMoleculesOrOtherElementaryEntities',
'numberOfMoleculesOrOtherElementaryEntities',
'number of molecules or other elementary entities')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#restMassOfProton',
'restMassOfProton',
'(rest) mass of proton')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grandCanonicalPartitionFunction',
'grandCanonicalPartitionFunction',
'grand-canonical partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#moleFractionOfB',
'moleFractionOfB',
'mole fraction of B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#directionalSpectralEmissivity',
'directionalSpectralEmissivity',
'directional spectral emissivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#absorptionFactor',
'absorptionFactor',
'absorption factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#principleQuantumNumber',
'principleQuantumNumber',
'principle quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reactivity',
'reactivity',
'reactivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeMassDefect',
'relativeMassDefect',
'relative mass defect')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#molecularPartitionFunction',
'molecularPartitionFunction',
'molecular partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#landauGinzburgNumber',
'landauGinzburgNumber',
'Landau-Ginzburg number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#canonicalPartitionFunction',
'canonicalPartitionFunction',
'canonical partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#orbitalAngularMomentumQuantumNumber',
'orbitalAngularMomentumQuantumNumber',
'orbital angular momentum quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nusseltNumberForMassTransfer',
'nusseltNumberForMassTransfer',
'Nusselt number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fastFissionFactor',
'fastFissionFactor',
'fast fission factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#schmidtNumber',
'schmidtNumber',
'Schmidt number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#magneticSusceptibility',
'magneticSusceptibility',
'magnetic susceptibility')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thermalDiffusionRatio',
'thermalDiffusionRatio',
'thermal diffusion ratio')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#pecletNumber',
'pecletNumber',
'Peclet number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#transportNumberOfIonB',
'transportNumberOfIonB',
'transport number of ion B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#rayleighNumber',
'rayleighNumber',
'Rayleigh number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#stoichiometricNumberOfB',
'stoichiometricNumberOfB',
'stoichiometric number of B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralAbsorptance',
'spectralAbsorptance',
'spectral absorptance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#strouhalNumber',
'strouhalNumber',
'Strouhal number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#opticalDensity',
'opticalDensity',
'optical density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#shearStrain',
'shearStrain',
'shear strain')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nusseltNumber',
'nusseltNumber',
'Nusselt number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralReflectance',
'spectralReflectance',
'spectral reflectance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#restMassOfNeutron',
'restMassOfNeutron',
'(rest) mass of neutron')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fourierNumberForMassTransfer',
'fourierNumberForMassTransfer',
'Fourier number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralEmissivity',
'spectralEmissivity',
'spectral emissivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeMassExcess',
'relativeMassExcess',
'relative mass excess')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronYieldPerFission',
'neutronYieldPerFission',
'neutron yield per fission')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#linearStrain',
'linearStrain',
'linear strain')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#totalAngularMomentumQuantumNumber',
'totalAngularMomentumQuantumNumber',
'total angular momentum quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#spectralReflectionfactor',
'spectralReflectionfactor',
'spectral reflectionfactor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#isentropicExponent',
'isentropicExponent',
'isentropic exponent')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#moleRatioOfSoluteB',
'moleRatioOfSoluteB',
'mole ratio of solute B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reynoldsNumber',
'reynoldsNumber',
'Reynolds number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#stantonNumber',
'stantonNumber',
'Stanton number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#restMassOfElectron',
'restMassOfElectron',
'(rest) mass of electron')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardAbsoluteActivityOfSolventAEspeciallyInADiluteSolution',
'standardAbsoluteActivityOfSolventAEspeciallyInADiluteSolution',
'standard absolute activity of solvent A (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#partitionFunctionOfAMolecule',
'partitionFunctionOfAMolecule',
'partition function of a molecule')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#machNumber',
'machNumber',
'Mach number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#numberOfPairsOfPoles',
'numberOfPairsOfPoles',
'number of pairs of poles')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grashofNumber',
'grashofNumber',
'Grashof number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#averageLogarithmicEnergyDecrement',
'averageLogarithmicEnergyDecrement',
'average logarithmic energy decrement')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fineStructureConstant',
'fineStructureConstant',
'fine structure constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#mobilityRatio',
'mobilityRatio',
'mobility ratio')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#dissipationFactor',
'dissipationFactor',
'dissipation factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronNumber',
'neutronNumber',
'neutron number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#internalConversionFactor',
'internalConversionFactor',
'internal conversion factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reflectionFactor',
'reflectionFactor',
'reflection factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#frictionFactor',
'frictionFactor',
'friction factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thermalUtilizationFactor',
'thermalUtilizationFactor',
'thermal utilization factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#atomicNumber',
'atomicNumber',
'atomic number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#transmittance',
'transmittance',
'transmittance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#lethargy',
'lethargy',
'lethargy')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#longRangeOrderParameter',
'longRangeOrderParameter',
'long-range order parameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grueneisenParameter',
'grueneisenParameter',
'Grüneisen parameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#grashofNumberForMassTransfer',
'grashofNumberForMassTransfer',
'Grashof number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#pecletNumberForMassTransfer',
'pecletNumberForMassTransfer',
'Peclet number for mass transfer')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#volumeOrBulkStrain',
'volumeOrBulkStrain',
'volume or bulk strain')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#currentFractionOfIonB',
'currentFractionOfIonB',
'current fraction of ion B')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reflectance',
'reflectance',
'reflectance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#osmoticCoefficientOfTheSolventAEspeciallyInADiluteSolution',
'osmoticCoefficientOfTheSolventAEspeciallyInADiluteSolution',
'osmotic coefficient of the solvent A (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#multiplicationFactor',
'multiplicationFactor',
'multiplication factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nonLeakageProbability',
'nonLeakageProbability',
'non leakage probability')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#statisticalWeight',
'statisticalWeight',
'statistical weight')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#emissivityAtASpecifiedWavelength',
'emissivityAtASpecifiedWavelength',
'emissivity at a specified wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#emissivity',
'emissivity',
'emissivity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fourierNumber',
'fourierNumber',
'Fourier number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#coefficientOfFriction',
'coefficientOfFriction',
'coefficient of friction')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#massNumber',
'massNumber',
'mass number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeMassDensity',
'relativeMassDensity',
'relative mass density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thermalDiffusionFactor',
'thermalDiffusionFactor',
'thermal diffusion factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeElongation',
'relativeElongation',
'relative elongation')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#coordinatesTrichromatic',
'coordinatesTrichromatic',
'coordinates trichromatic')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nucleonNumber',
'nucleonNumber',
'nucleon number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#luminousEfficiency',
'luminousEfficiency',
'luminous efficiency')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeDensity',
'relativeDensity',
'relative density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#chargeNumberOfIon',
'chargeNumberOfIon',
'charge number of ion')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#debyeWalleFactor',
'debyeWalleFactor',
'Debye-Walle factor')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardAbsoluteActivityOfSoluteBEspeciallyInADiluteSolution',
'standardAbsoluteActivityOfSoluteBEspeciallyInADiluteSolution',
'standard absolute activity of solute B (especially in a dilute solution)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#absoluteActivity',
'absoluteActivity',
'absolute activity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#magneticReynoldsNumber',
'magneticReynoldsNumber',
'magnetic Reynolds number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeAtomicMass',
'relativeAtomicMass',
'relative atomic mass')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#ratioOfTheSpecificHeatCapacities',
'ratioOfTheSpecificHeatCapacities',
'ratio of the specific heat capacities')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativePermeability',
'relativePermeability',
'relative permeability')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#packingFraction',
'packingFraction',
'packing fraction')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardAbsoluteActivityOfBInAGaseousMixture',
'standardAbsoluteActivityOfBInAGaseousMixture',
'standard absolute activity of B (in a gaseous mixture)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neutronYieldPerAbsorption',
'neutronYieldPerAbsorption',
'neutron yield per absorption')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#gFactorOfAtomOrElectron',
'gFactorOfAtomOrElectron',
'g-factor of atom or electron')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#gFactorOfNucleus',
'gFactorOfNucleus',
'g-factor of nucleus')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relativeMolecularMass',
'relativeMolecularMass',
'relative molecular mass')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#madelungConstant',
'madelungConstant',
'Madelung constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#refractiveIndex',
'refractiveIndex',
'refractive index')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#microcanonicalPartitionFunction',
'microcanonicalPartitionFunction',
'microcanonical partition function')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#eulerNumber',
'eulerNumber',
'Euler number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#resonanceEscapeProbability',
'resonanceEscapeProbability',
'resonance escape probability')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nuclearSpinQuantumNumber',
'nuclearSpinQuantumNumber',
'nuclear spin quantum number')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#absorbance',
'absorbance',
'absorbance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#degreeOfDissociation',
'degreeOfDissociation',
'degree of dissociation')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#cieColorimetricFunctions',
'cieColorimetricFunctions',
'CIE colorimetric functions')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#standardEquilibriumConstant',
'standardEquilibriumConstant',
'standard equilibrium constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#one';
defaultUnit.addPreferredName('en' , 'one');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonthDay" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementGMonthYear';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonthDay" ),'--01-01'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonthDay" ),'--01-02'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGMonthYear';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithGMonthYear',
    false,
    );




getModelClass(): string {
return 'AspectWithGTypeForRangeConstraints';
}

getAspectModelUrn(): string {
return MetaAspectWithGTypeForRangeConstraints .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithGTypeForRangeConstraints';
}

getProperties(): Array<StaticProperty<AspectWithGTypeForRangeConstraints, any>> {
return [MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_YEAR, MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_MONTH, MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_DAY, MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_YEAR_MONTH, MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_MONTH_YEAR];
}

getAllProperties(): Array<StaticProperty<AspectWithGTypeForRangeConstraints, any>> {
        return this.getProperties();
}




    }


