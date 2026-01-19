













import { AspectWithOptionalProperties,} from './AspectWithOptionalProperties';
import { DefaultCharacteristic,DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { Units,} from './esmf/./shared/units';


    

/*
* Generated class MetaAspectWithOptionalProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOptionalProperties implements StaticMetaClass<AspectWithOptionalProperties>, PropertyContainer<AspectWithOptionalProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalProperties();


 public static readonly  NUMBER_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalProperties, string, string> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithOptionalProperties';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithOptionalProperties) : string {
        return object.numberProperty;
    }

        setValue( object : AspectWithOptionalProperties, value : string ) {
            object.numberProperty = value;
        }

            
            getUnit(): any {
            return Units.fromName('DefaultQuantifiable[unit=Optional[DefaultUnit[symbol=Optional[m], code=Optional[MTR], referenceUnit=Optional.empty, conversionFactor=Optional[m], quantityKinds=[length, slowing-down length, diffusion coefficient for neutron fluence rate, half-thickness, London penetration depth, (instantaneous) sound particle displacement, Compton wavelength, diffusion coefficient for neutron flux density, breadth, distance, radius of curvature, mean free path, fundamental lattice vector, mean linear range, length of path, object distance, nuclear radius, half-value thickness, image distance, Bohr radius, equilibrium position vector of ion or atom, migration length, lattice vector, electron radius, cartesian coordinates, displacement vector of ion or atom, radius, thickness, diffusion length, focal distance, coherence length, particle position vector, wavelength, diameter, mean free path of phonons or electrons, height]]]]')
            }
    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithOptionalProperties',
    'numberProperty',
    (() => { const defaultQuantifiable = new DefaultQuantifiable(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedLong" ),(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metre',
'metre',
'm','MTR',undefined,'m',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fundamentalLatticeVector',
'fundamentalLatticeVector',
'fundamental lattice vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#halfThickness',
'halfThickness',
'half-thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#radiusOfCurvature',
'radiusOfCurvature',
'radius of curvature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#distance',
'distance',
'distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#equilibriumPositionVectorOfIonOrAtom',
'equilibriumPositionVectorOfIonOrAtom',
'equilibrium position vector of ion or atom')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#slowingDownLength',
'slowingDownLength',
'slowing-down length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#height',
'height',
'height')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#halfValueThickness',
'halfValueThickness',
'half-value thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#diffusionCoefficientForNeutronFluenceRate',
'diffusionCoefficientForNeutronFluenceRate',
'diffusion coefficient for neutron fluence rate')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#meanFreePathOfPhononsOrElectrons',
'meanFreePathOfPhononsOrElectrons',
'mean free path of phonons or electrons')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#bohrRadius',
'bohrRadius',
'Bohr radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleDisplacement',
'instantaneousSoundParticleDisplacement',
'(instantaneous) sound particle displacement')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#electronRadius',
'electronRadius',
'electron radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#lengthOfPath',
'lengthOfPath',
'length of path')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#coherenceLength',
'coherenceLength',
'coherence length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#diffusionCoefficientForNeutronFluxDensity',
'diffusionCoefficientForNeutronFluxDensity',
'diffusion coefficient for neutron flux density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#migrationLength',
'migrationLength',
'migration length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#meanLinearRange',
'meanLinearRange',
'mean linear range')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#radius',
'radius',
'radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#displacementVectorOfIonOrAtom',
'displacementVectorOfIonOrAtom',
'displacement vector of ion or atom')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#latticeVector',
'latticeVector',
'lattice vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#meanFreePath',
'meanFreePath',
'mean free path')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#length',
'length',
'length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#breadth',
'breadth',
'breadth')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#londonPenetrationDepth',
'londonPenetrationDepth',
'London penetration depth')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nuclearRadius',
'nuclearRadius',
'nuclear radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#comptonWavelength',
'comptonWavelength',
'Compton wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#particlePositionVector',
'particlePositionVector',
'particle position vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#diffusionLength',
'diffusionLength',
'diffusion length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#focalDistance',
'focalDistance',
'focal distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#cartesianCoordinates',
'cartesianCoordinates',
'cartesian coordinates')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thickness',
'thickness',
'thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#wavelength',
'wavelength',
'wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#diameter',
'diameter',
'diameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#imageDistance',
'imageDistance',
'image distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#objectDistance',
'objectDistance',
'object distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metre';
defaultUnit.addPreferredName('en' , 'metre');
 return defaultUnit; })())
defaultQuantifiable.isAnonymousNode = true;
 return defaultQuantifiable; })()
,
    false,
    true,
    undefined,
        'numberProperty',
    false,
    );




 public static readonly  TIMESTAMP_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithOptionalProperties, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithOptionalProperties';
    }


    getValue( object : AspectWithOptionalProperties) : string {
        return object.timestampProperty;
    }

        setValue( object : AspectWithOptionalProperties, value : string ) {
            object.timestampProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithOptionalProperties',
    'timestampProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Timestamp',
'Timestamp',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Timestamp';
defaultCharacteristic.addPreferredName('en' , 'Timestamp');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains the date and time with an optional timezone.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'timestampProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithOptionalProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithOptionalProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOptionalProperties';
}

getProperties(): Array<StaticProperty<AspectWithOptionalProperties, any>> {
return [MetaAspectWithOptionalProperties.NUMBER_PROPERTY, MetaAspectWithOptionalProperties.TIMESTAMP_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithOptionalProperties, any>> {
        return this.getProperties();
}




    }


