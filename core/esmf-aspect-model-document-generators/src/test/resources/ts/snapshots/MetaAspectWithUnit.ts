













import { AspectWithUnit,} from './AspectWithUnit';
import { DefaultQuantifiable,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,StaticUnitProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Units,} from './esmf/./shared/units';


    

/*
* Generated class MetaAspectWithUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithUnit implements StaticMetaClass<AspectWithUnit>, PropertyContainer<AspectWithUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUnit();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticUnitProperty<AspectWithUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithUnit';
    }


    getValue( object : AspectWithUnit) : number {
        return object.testProperty;
    }

        setValue( object : AspectWithUnit, value : number ) {
            object.testProperty = value;
        }

            
            getUnit(): any {
            return Units.fromName('DefaultQuantifiable[unit=Optional[DefaultUnit[symbol=Optional[bit/s], code=Optional[B10], referenceUnit=Optional.empty, conversionFactor=Optional.empty, quantityKinds=[]]]]')
            }
    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithUnit',
    'testProperty',
    (() => { const defaultQuantifiable = new DefaultQuantifiable(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestQuantifiable',
'TestQuantifiable',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#bitPerSecond',
'bitPerSecond',
'bit\/s','B10',undefined,undefined,[  ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#bitPerSecond';
defaultUnit.addPreferredName('en' , 'bit per second');
 return defaultUnit; })())
defaultQuantifiable.addAspectModelUrn = this.NAMESPACE + 'TestQuantifiable';
 return defaultQuantifiable; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithUnit';
}

getProperties(): Array<StaticProperty<AspectWithUnit, any>> {
return [MetaAspectWithUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUnit, any>> {
        return this.getProperties();
}




    }


