













import { AspectWithMeasurement,} from './AspectWithMeasurement';
import { DefaultMeasurement,DefaultQuantityKind,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,StaticUnitProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Units,} from './esmf/./shared/units';


    

/*
* Generated class MetaAspectWithMeasurement (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMeasurement).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMeasurement implements StaticMetaClass<AspectWithMeasurement>, PropertyContainer<AspectWithMeasurement> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMeasurement';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMeasurement();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticUnitProperty<AspectWithMeasurement, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithMeasurement';
    }


    getValue( object : AspectWithMeasurement) : number {
        return object.testProperty;
    }

        setValue( object : AspectWithMeasurement, value : number ) {
            object.testProperty = value;
        }

            
            getUnit(): any {
            return Units.fromName('DefaultMeasurement[]')
            }
    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMeasurement',
    'testProperty',
    (() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestMeasurement',
'TestMeasurement',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#kelvin',
'kelvin',
'K','KEL',undefined,'K',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thermodynamic',
'thermodynamic',
'thermodynamic')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#neelTemperature',
'neelTemperature',
'Néel temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#superConductorTransitionTemperature',
'superConductorTransitionTemperature',
'Super conductor transition temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#debyeTemperature',
'debyeTemperature',
'Debye temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#curieTemperature',
'curieTemperature',
'Curie temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fermiTemperature',
'fermiTemperature',
'Fermi temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#kelvin';
defaultUnit.addPreferredName('en' , 'kelvin');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'TestMeasurement';
defaultMeasurement.addPreferredName('en' , 'Test Measurement');
defaultMeasurement.addDescription('en' , 'This is a test Measurement');
defaultMeasurement.addSeeReference('http:\/\/example.com\/');
 return defaultMeasurement; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithMeasurement';
}

getAspectModelUrn(): string {
return MetaAspectWithMeasurement .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMeasurement';
}

getProperties(): Array<StaticProperty<AspectWithMeasurement, any>> {
return [MetaAspectWithMeasurement.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithMeasurement, any>> {
        return this.getProperties();
}




    }


