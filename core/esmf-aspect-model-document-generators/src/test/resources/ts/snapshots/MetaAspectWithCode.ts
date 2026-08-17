













import { AspectWithCode,} from './AspectWithCode';
import { DefaultCode,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithCode (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCode).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCode implements StaticMetaClass<AspectWithCode>, PropertyContainer<AspectWithCode> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCode';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCode();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithCode, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithCode';
    }


    getValue( object : AspectWithCode) : number {
        return object.testProperty;
    }

        setValue( object : AspectWithCode, value : number ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCode',
    'testProperty',
    (() => { const defaultCode = new DefaultCode(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestCode',
'TestCode',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ))
defaultCode.addAspectModelUrn = this.NAMESPACE + 'TestCode';
defaultCode.addPreferredName('en' , 'Test Code');
defaultCode.addDescription('en' , 'This is a test code.');
defaultCode.addSeeReference('http:\/\/example.com\/');
 return defaultCode; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCode';
}

getAspectModelUrn(): string {
return MetaAspectWithCode .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCode';
}

getProperties(): Array<StaticProperty<AspectWithCode, any>> {
return [MetaAspectWithCode.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCode, any>> {
        return this.getProperties();
}




    }


