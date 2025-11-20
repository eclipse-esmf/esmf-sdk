













import { AspectWithCollection,} from './AspectWithCollection';
import { DefaultCollection,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollection implements StaticMetaClass<AspectWithCollection>, PropertyContainer<AspectWithCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollection();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollection, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollection';
    }

        getContainedType(): string {
            return 'AspectWithCollection';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultCollection = new DefaultCollection(null, 
null, 
null, 
true, false, undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollection';
defaultCollection.addPreferredName('en' , 'Test Collection');
defaultCollection.addDescription('en' , 'This is a test collection.');
defaultCollection.addSeeReference('http:\/\/example.com\/');
 return defaultCollection; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithCollection';
}

getAspectModelUrn(): string {
return MetaAspectWithCollection .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollection';
}

                        getProperties(): Array<StaticProperty<AspectWithCollection, any>> {
return [MetaAspectWithCollection.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCollection, any>> {
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


