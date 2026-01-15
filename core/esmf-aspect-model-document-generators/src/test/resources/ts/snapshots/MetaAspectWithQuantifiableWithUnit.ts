













import { AspectWithQuantifiableWithUnit,} from './AspectWithQuantifiableWithUnit';
import { DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,StaticUnitProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Units,} from './esmf/./shared/units';


    

/*
* Generated class MetaAspectWithQuantifiableWithUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableWithUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithQuantifiableWithUnit implements StaticMetaClass<AspectWithQuantifiableWithUnit>, PropertyContainer<AspectWithQuantifiableWithUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithQuantifiableWithUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithQuantifiableWithUnit();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticUnitProperty<AspectWithQuantifiableWithUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithQuantifiableWithUnit';
    }


    getValue( object : AspectWithQuantifiableWithUnit) : number {
        return object.testProperty;
    }

        setValue( object : AspectWithQuantifiableWithUnit, value : number ) {
            object.testProperty = value;
        }

            
            getUnit(): any {
            return Units.fromName('DefaultQuantifiable[unit=Optional[DefaultUnit[symbol=Optional[%], code=Optional[P1], referenceUnit=Optional.empty, conversionFactor=Optional[1 × 10⁻²], quantityKinds=[dimensionless]]]]')
            }
    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithQuantifiableWithUnit',
    'testProperty',
    (() => { const defaultQuantifiable = new DefaultQuantifiable(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestQuantifiable',
'TestQuantifiable',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
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
return 'AspectWithQuantifiableWithUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithQuantifiableWithUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithQuantifiableWithUnit';
}

getProperties(): Array<StaticProperty<AspectWithQuantifiableWithUnit, any>> {
return [MetaAspectWithQuantifiableWithUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithQuantifiableWithUnit, any>> {
        return this.getProperties();
}




    }


