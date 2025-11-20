













import { AspectWithCustomUnitAndQuantityKind,} from './AspectWithCustomUnitAndQuantityKind';
import { DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './aspect-meta-model';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { StaticUnitProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCustomUnitAndQuantityKind (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCustomUnitAndQuantityKind).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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


                                        })(

        null,
    null,
    null,
    (() => { const defaultQuantifiable = new DefaultQuantifiable(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),(() => { const defaultUnit = new DefaultUnit(null, 
null, 
null, 
'gCO₂eq\/kWh',undefined,undefined,undefined,[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


