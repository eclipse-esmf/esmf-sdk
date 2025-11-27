













import { AspectWithTwoLists,} from './AspectWithTwoLists';
import { DefaultList,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithTwoLists (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTwoLists).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithTwoLists implements StaticMetaClass<AspectWithTwoLists>, PropertyContainer<AspectWithTwoLists> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTwoLists';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithTwoLists();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithTwoLists, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithTwoLists';
    }

        getContainedType(): string {
            return 'AspectWithTwoLists';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoLists',
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




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends StaticContainerProperty<AspectWithTwoLists, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithTwoLists';
    }

        getContainedType(): string {
            return 'AspectWithTwoLists';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoLists',
    'testPropertyTwo',
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
        'testPropertyTwo',
    false,
    );




getModelClass(): string {
return 'AspectWithTwoLists';
}

getAspectModelUrn(): string {
return MetaAspectWithTwoLists .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithTwoLists';
}

getProperties(): Array<StaticProperty<AspectWithTwoLists, any>> {
return [MetaAspectWithTwoLists.TEST_PROPERTY, MetaAspectWithTwoLists.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithTwoLists, any>> {
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


