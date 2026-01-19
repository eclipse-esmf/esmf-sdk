













import { AspectWithCustomUnit,} from './AspectWithCustomUnit';
import { DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,StaticUnitProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Units,} from './esmf/./shared/units';


    

/*
* Generated class MetaAspectWithCustomUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCustomUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCustomUnit implements StaticMetaClass<AspectWithCustomUnit>, PropertyContainer<AspectWithCustomUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCustomUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCustomUnit();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticUnitProperty<AspectWithCustomUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithCustomUnit';
    }


    getValue( object : AspectWithCustomUnit) : number {
        return object.testProperty;
    }

        setValue( object : AspectWithCustomUnit, value : number ) {
            object.testProperty = value;
        }

            
            getUnit(): any {
            return Units.fromName('DefaultQuantifiable[unit=Optional[DefaultUnit[symbol=Optional[nl/min], code=Optional.empty, referenceUnit=Optional.empty, conversionFactor=Optional.empty, quantityKinds=[volume flow rate]]]]')
            }
    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCustomUnit',
    'testProperty',
    (() => { const defaultQuantifiable = new DefaultQuantifiable(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestQuantifiable',
'TestQuantifiable',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'normLitrePerMinute',
'normLitrePerMinute',
'nl\/min',undefined,undefined,undefined,[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#volumeFlowRate',
'volumeFlowRate',
'volume flow rate')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = this.NAMESPACE + 'normLitrePerMinute';
defaultUnit.addPreferredName('en' , 'norm litre per minute');
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
return 'AspectWithCustomUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithCustomUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCustomUnit';
}

getProperties(): Array<StaticProperty<AspectWithCustomUnit, any>> {
return [MetaAspectWithCustomUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCustomUnit, any>> {
        return this.getProperties();
}




    }


