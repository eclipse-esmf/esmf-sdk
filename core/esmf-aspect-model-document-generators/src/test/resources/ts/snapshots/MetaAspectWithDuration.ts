













import { AspectWithDuration,} from './AspectWithDuration';
import { DefaultDuration,DefaultQuantityKind,DefaultScalar,} from './aspect-meta-model';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { StaticUnitProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithDuration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDuration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDuration implements StaticMetaClass<AspectWithDuration>, PropertyContainer<AspectWithDuration> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDuration';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDuration();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticUnitProperty<AspectWithDuration, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithDuration';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultDuration = new DefaultDuration(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),(() => { const defaultUnit = new DefaultUnit(null, 
null, 
null, 
'ks','B52','secondUnitOfTime','10³ s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#kilosecond';
defaultUnit.addPreferredName('en' , 'kilosecond');
 return defaultUnit; })())
defaultDuration.addAspectModelUrn = this.NAMESPACE + 'TestDuration';
defaultDuration.addPreferredName('en' , 'Test Duration');
defaultDuration.addDescription('en' , 'This is a test Duration');
defaultDuration.addSeeReference('http:\/\/example.com\/');
 return defaultDuration; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithDuration';
}

getAspectModelUrn(): string {
return MetaAspectWithDuration .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithDuration';
}

                        getProperties(): Array<StaticProperty<AspectWithDuration, any>> {
return [MetaAspectWithDuration.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithDuration, any>> {
    return this.getProperties();
}




    }


