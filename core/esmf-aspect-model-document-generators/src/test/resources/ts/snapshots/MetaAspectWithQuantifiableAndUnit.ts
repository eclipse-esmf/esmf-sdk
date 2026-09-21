













import { AspectWithQuantifiableAndUnit,} from './AspectWithQuantifiableAndUnit';
import { DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,StaticUnitProperty,} from './esmf/aspect-meta-model/staticProperty';
import { Units,} from './esmf/./shared/units';


    

/*
* Generated class MetaAspectWithQuantifiableAndUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableAndUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithQuantifiableAndUnit implements StaticMetaClass<AspectWithQuantifiableAndUnit>, PropertyContainer<AspectWithQuantifiableAndUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithQuantifiableAndUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithQuantifiableAndUnit();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticUnitProperty<AspectWithQuantifiableAndUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithQuantifiableAndUnit';
    }


    getValue( object : AspectWithQuantifiableAndUnit) : number {
        return object.testProperty;
    }

        setValue( object : AspectWithQuantifiableAndUnit, value : number ) {
            object.testProperty = value;
        }

            
            getUnit(): any {
            return Units.fromName('DefaultQuantifiable[unit=Optional[DefaultUnit[symbol=Optional[Hz], code=Optional[HTZ], referenceUnit=Optional.empty, conversionFactor=Optional[Hz], quantityKinds=[frequency]]]]')
            }
    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithQuantifiableAndUnit',
    'testProperty',
    (() => { const defaultQuantifiable = new DefaultQuantifiable(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestQuantifiable',
'TestQuantifiable',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hertz',
'hertz',
'Hz','HTZ',undefined,'Hz',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#frequency',
'frequency',
'frequency')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hertz';
defaultUnit.addPreferredName('en' , 'hertz');
 return defaultUnit; })())
defaultQuantifiable.addAspectModelUrn = this.NAMESPACE + 'TestQuantifiable';
defaultQuantifiable.addPreferredName('en' , 'Test Quantifiable');
defaultQuantifiable.addDescription('en' , 'This is a test Quantifiable');
defaultQuantifiable.addSeeReference('http:\/\/example.com\/');
 return defaultQuantifiable; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithQuantifiableAndUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithQuantifiableAndUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithQuantifiableAndUnit';
}

getProperties(): Array<StaticProperty<AspectWithQuantifiableAndUnit, any>> {
return [MetaAspectWithQuantifiableAndUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithQuantifiableAndUnit, any>> {
        return this.getProperties();
}




    }


