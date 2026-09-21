













import { AspectWithCollection,} from './AspectWithCollection';
import { DefaultCollection,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCollection implements StaticMetaClass<AspectWithCollection>, PropertyContainer<AspectWithCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollection();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollection, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithCollection';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithCollection) : string[] {
        return object.testProperty;
    }

        setValue( object : AspectWithCollection, value : string[] ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCollection',
    'testProperty',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestCollection',
'TestCollection',
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
return KnownVersion.getLatest()
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


