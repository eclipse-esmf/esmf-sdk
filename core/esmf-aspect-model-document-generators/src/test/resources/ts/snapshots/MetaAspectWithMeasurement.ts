













import { AspectWithMeasurement,} from './AspectWithMeasurement';
import { DefaultMeasurement,DefaultQuantityKind,DefaultScalar,} from './aspect-meta-model';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { StaticUnitProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithMeasurement (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMeasurement).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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


                                        })(

        null,
    null,
    null,
    (() => { const defaultMeasurement = new DefaultMeasurement(null, 
null, 
null, 
(() => { const defaultUnit = new DefaultUnit(null, 
null, 
null, 
'K','KEL',undefined,'K',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'Debye temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'Néel temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'Curie temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'thermodynamic')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'Super conductor transition temperature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


