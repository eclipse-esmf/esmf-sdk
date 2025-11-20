













import { AspectWithQuantifiableWithoutUnit,} from './AspectWithQuantifiableWithoutUnit';
import { DefaultQuantifiable,DefaultScalar,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithQuantifiableWithoutUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableWithoutUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithQuantifiableWithoutUnit implements StaticMetaClass<AspectWithQuantifiableWithoutUnit>, PropertyContainer<AspectWithQuantifiableWithoutUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithQuantifiableWithoutUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithQuantifiableWithoutUnit();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithQuantifiableWithoutUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithQuantifiableWithoutUnit';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultQuantifiable = new DefaultQuantifiable(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),undefined)
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
return 'AspectWithQuantifiableWithoutUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithQuantifiableWithoutUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithQuantifiableWithoutUnit';
}

                        getProperties(): Array<StaticProperty<AspectWithQuantifiableWithoutUnit, any>> {
return [MetaAspectWithQuantifiableWithoutUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithQuantifiableWithoutUnit, any>> {
    return this.getProperties();
}




    }


