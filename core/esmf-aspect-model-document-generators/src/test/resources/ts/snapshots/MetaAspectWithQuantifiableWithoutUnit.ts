













import { AspectWithQuantifiableWithoutUnit,} from './AspectWithQuantifiableWithoutUnit';
import { DefaultQuantifiable,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithQuantifiableWithoutUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableWithoutUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithQuantifiableWithoutUnit',
    'testProperty',
    (() => { const defaultQuantifiable = new DefaultQuantifiable(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestQuantifiable',
'TestQuantifiable',
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
return KnownVersion.getLatest()
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


