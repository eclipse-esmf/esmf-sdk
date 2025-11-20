













import { AspectWithTwoLists,} from './AspectWithTwoLists';
import { DefaultList,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithTwoLists (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTwoLists).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithTwoLists implements StaticMetaClass<AspectWithTwoLists>, PropertyContainer<AspectWithTwoLists> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTwoLists';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithTwoLists();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithTwoLists, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithTwoLists';
    }

        getContainedType(): string {
            return 'AspectWithTwoLists';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultList = new DefaultList(null, 
null, 
null, 
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
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithTwoLists';
    }

        getContainedType(): string {
            return 'AspectWithTwoLists';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultList = new DefaultList(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Test Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('This is a test description', 'en'),
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


