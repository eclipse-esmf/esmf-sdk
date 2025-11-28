













import { AspectWithCustomUnitAndQuantityKind,} from './AspectWithCustomUnitAndQuantityKind';
import { DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,StaticUnitProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Units,} from './esmf/./shared/units';


    

/*
* Generated class MetaAspectWithCustomUnitAndQuantityKind (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCustomUnitAndQuantityKind).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCustomUnitAndQuantityKind implements StaticMetaClass<AspectWithCustomUnitAndQuantityKind>, PropertyContainer<AspectWithCustomUnitAndQuantityKind> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCustomUnitAndQuantityKind';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCustomUnitAndQuantityKind();


 public static readonly  EMISSIONS = 
                
        new (class extends StaticUnitProperty<AspectWithCustomUnitAndQuantityKind, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithCustomUnitAndQuantityKind';
    }


    getValue( object : AspectWithCustomUnitAndQuantityKind) : number {
        return object.emissions;
    }

        setValue( object : AspectWithCustomUnitAndQuantityKind, value : number ) {
            object.emissions = value;
        }

            
            getUnit(): any {
            return Units.fromName('DefaultQuantifiable[unit=Optional[DefaultUnit[symbol=Optional[gCO₂eq/kWh], code=Optional.empty, referenceUnit=Optional.empty, conversionFactor=Optional.empty, quantityKinds=[DefaultQuantityKind[]]]]]')
            }
    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCustomUnitAndQuantityKind',
    'emissions',
    (() => { const defaultQuantifiable = new DefaultQuantifiable(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Emissions',
'Emissions',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'gCO2eqPerkWh',
'gCO2eqPerkWh',
'gCO₂eq\/kWh',undefined,undefined,undefined,[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'greenhouseGasEmissions',
'greenhouseGasEmissions',
'greenhouse gas emissions')
defaultQuantityKind.addAspectModelUrn = this.NAMESPACE + 'greenhouseGasEmissions';
defaultQuantityKind.addPreferredName('en' , 'greenhouse gas emissions');
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = this.NAMESPACE + 'gCO2eqPerkWh';
defaultUnit.addPreferredName('en' , 'gram CO₂ equivalent per kWh');
 return defaultUnit; })())
defaultQuantifiable.addAspectModelUrn = this.NAMESPACE + 'Emissions';
 return defaultQuantifiable; })()
,
    false,
    false,
    undefined,
        'emissions',
    false,
    );




getModelClass(): string {
return 'AspectWithCustomUnitAndQuantityKind';
}

getAspectModelUrn(): string {
return MetaAspectWithCustomUnitAndQuantityKind .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCustomUnitAndQuantityKind';
}

getProperties(): Array<StaticProperty<AspectWithCustomUnitAndQuantityKind, any>> {
return [MetaAspectWithCustomUnitAndQuantityKind.EMISSIONS];
}

getAllProperties(): Array<StaticProperty<AspectWithCustomUnitAndQuantityKind, any>> {
        return this.getProperties();
}




    }


