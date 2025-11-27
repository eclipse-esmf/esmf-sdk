













import { AspectWithSortedSet,} from './AspectWithSortedSet';
import { DefaultScalar,DefaultSortedSet,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithSortedSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSortedSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithSortedSet implements StaticMetaClass<AspectWithSortedSet>, PropertyContainer<AspectWithSortedSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSortedSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSortedSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithSortedSet, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSortedSet';
    }

        getContainedType(): string {
            return 'AspectWithSortedSet';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSortedSet',
    'testProperty',
    (() => { const defaultSortedSet = new DefaultSortedSet(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestSortedSet',
'TestSortedSet',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultSortedSet.addAspectModelUrn = this.NAMESPACE + 'TestSortedSet';
defaultSortedSet.addPreferredName('en' , 'Test Sorted Set');
defaultSortedSet.addDescription('en' , 'This is a test sorted set.');
defaultSortedSet.addSeeReference('http:\/\/example.com\/');
 return defaultSortedSet; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithSortedSet';
}

getAspectModelUrn(): string {
return MetaAspectWithSortedSet .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithSortedSet';
}

getProperties(): Array<StaticProperty<AspectWithSortedSet, any>> {
return [MetaAspectWithSortedSet.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithSortedSet, any>> {
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


