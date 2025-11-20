













import { AspectWithSortedSet,} from './AspectWithSortedSet';
import { DefaultScalar,DefaultSortedSet,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithSortedSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSortedSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSortedSet implements StaticMetaClass<AspectWithSortedSet>, PropertyContainer<AspectWithSortedSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSortedSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSortedSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithSortedSet, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithSortedSet';
    }

        getContainedType(): string {
            return 'AspectWithSortedSet';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultSortedSet = new DefaultSortedSet(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


