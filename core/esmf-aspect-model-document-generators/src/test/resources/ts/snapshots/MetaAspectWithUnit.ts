













import { AspectWithUnit,} from './AspectWithUnit';
import { DefaultQuantifiable,DefaultScalar,} from './aspect-meta-model';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { StaticUnitProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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
return KnownVersionUtils.getLatest()
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


