













import { AspectWithMeasurementWithUnit,} from './AspectWithMeasurementWithUnit';
import { DefaultMeasurement,DefaultQuantityKind,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,StaticUnitProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithMeasurementWithUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMeasurementWithUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithMeasurementWithUnit implements StaticMetaClass<AspectWithMeasurementWithUnit>, PropertyContainer<AspectWithMeasurementWithUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMeasurementWithUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMeasurementWithUnit();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticUnitProperty<AspectWithMeasurementWithUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithMeasurementWithUnit';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithMeasurementWithUnit',
    'testProperty',
    (() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestMeasurement',
'TestMeasurement',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#percent',
'percent',
'%','P1',undefined,'1 × 10⁻²',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#dimensionless',
'dimensionless',
'dimensionless')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#percent';
defaultUnit.addPreferredName('en' , 'percent');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'TestMeasurement';
 return defaultMeasurement; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithMeasurementWithUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithMeasurementWithUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithMeasurementWithUnit';
}

getProperties(): Array<StaticProperty<AspectWithMeasurementWithUnit, any>> {
return [MetaAspectWithMeasurementWithUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithMeasurementWithUnit, any>> {
        return this.getProperties();
}




    }


