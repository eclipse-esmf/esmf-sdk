













import { AspectWithConstrainedSet,} from './AspectWithConstrainedSet';
import { DefaultLengthConstraint,DefaultScalar,DefaultSet,DefaultTrait,} from './aspect-meta-model';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithConstrainedSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstrainedSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstrainedSet implements StaticMetaClass<AspectWithConstrainedSet>, PropertyContainer<AspectWithConstrainedSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstrainedSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstrainedSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithConstrainedSet, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithConstrainedSet';
    }

        getContainedType(): string {
            return 'AspectWithConstrainedSet';
        }

                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
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
 return defaultSet; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(null, 
null, 
null, 
1,1,)
lengthConstraint.addAspectModelUrn = this.NAMESPACE + 'TestSetConstraint';
lengthConstraint.addPreferredName('en' , 'TestSet Constraint');
lengthConstraint.addDescription('en' , 'Constraint for defining a non-empty set of identifiers.');
lengthConstraint.addSeeReference('http:\/\/example.com\/');
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestTrait';
 return trait; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithConstrainedSet';
}

getAspectModelUrn(): string {
return MetaAspectWithConstrainedSet .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithConstrainedSet';
}

                        getProperties(): Array<StaticProperty<AspectWithConstrainedSet, any>> {
return [MetaAspectWithConstrainedSet.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithConstrainedSet, any>> {
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


