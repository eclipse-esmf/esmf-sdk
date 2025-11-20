













import { AspectWithQuantifiableAndUnit,} from './AspectWithQuantifiableAndUnit';
import { DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './aspect-meta-model';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { StaticUnitProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithQuantifiableAndUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableAndUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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


                                        })(

        null,
    null,
    null,
    (() => { const defaultQuantifiable = new DefaultQuantifiable(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),(() => { const defaultUnit = new DefaultUnit(null, 
null, 
null, 
'Hz','HTZ',undefined,'Hz',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


