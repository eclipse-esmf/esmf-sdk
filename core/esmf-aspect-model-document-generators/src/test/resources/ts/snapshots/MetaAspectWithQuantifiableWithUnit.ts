













import { AspectWithQuantifiableWithUnit,} from './AspectWithQuantifiableWithUnit';
import { DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './aspect-meta-model';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { StaticUnitProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithQuantifiableWithUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableWithUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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
'%','P1',undefined,'1 × 10⁻²',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


