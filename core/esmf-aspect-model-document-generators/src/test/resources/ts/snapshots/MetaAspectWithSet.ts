













import { AspectWithSet,} from './AspectWithSet';
import { DefaultScalar,DefaultSet,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSet implements StaticMetaClass<AspectWithSet>, PropertyContainer<AspectWithSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithSet, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithSet';
    }

        getContainedType(): string {
            return 'AspectWithSet';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultSet = new DefaultSet(null, 
null, 
null, 
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultSet.addAspectModelUrn = this.NAMESPACE + 'TestSet';
defaultSet.addPreferredName('en' , 'Test Set');
defaultSet.addDescription('en' , 'This is a test set.');
defaultSet.addSeeReference('http:\/\/example.com\/');
 return defaultSet; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithSet';
}

getAspectModelUrn(): string {
return MetaAspectWithSet .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithSet';
}

                        getProperties(): Array<StaticProperty<AspectWithSet, any>> {
return [MetaAspectWithSet.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithSet, any>> {
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


