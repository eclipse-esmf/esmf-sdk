













import { AspectWithCustomUnit,} from './AspectWithCustomUnit';
import { DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './aspect-meta-model';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { StaticUnitProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCustomUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCustomUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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
'nl\/min',undefined,undefined,undefined,[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


