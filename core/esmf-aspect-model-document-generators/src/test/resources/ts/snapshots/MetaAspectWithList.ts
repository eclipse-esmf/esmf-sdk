













import { AspectWithList,} from './AspectWithList';
import { DefaultList,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithList (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithList).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithList implements StaticMetaClass<AspectWithList>, PropertyContainer<AspectWithList> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithList';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithList();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithList, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithList';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithList) : string[] {
        return object.testProperty;
    }

        setValue( object : AspectWithList, value : string[] ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithList',
    'testProperty',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestList',
'TestList',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultList.addAspectModelUrn = this.NAMESPACE + 'TestList';
defaultList.addPreferredName('en' , 'Test List');
defaultList.addDescription('en' , 'This is a test list.');
defaultList.addSeeReference('http:\/\/example.com\/');
 return defaultList; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithList';
}

getAspectModelUrn(): string {
return MetaAspectWithList .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithList';
}

getProperties(): Array<StaticProperty<AspectWithList, any>> {
return [MetaAspectWithList.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithList, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This is a test description', language: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


