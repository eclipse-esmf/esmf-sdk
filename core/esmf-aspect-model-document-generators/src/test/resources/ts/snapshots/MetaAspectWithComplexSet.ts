import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithComplexSet,} from './AspectWithComplexSet';
import { DefaultEntity,DefaultLengthConstraint,DefaultSet,DefaultTrait,} from './aspect-meta-model';

import { LangString,} from './core/langString';

import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithComplexSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithComplexSet implements StaticMetaClass<AspectWithComplexSet>, PropertyContainer<AspectWithComplexSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithComplexSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithComplexSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithComplexSet, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithComplexSet';
    }

        getContainedType(): string {
            return 'AspectWithComplexSet';
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
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Unique ReplacedAspectArtifactentifier');
 return defaultEntityReplacedAspectArtifact; })())
defaultSet.addAspectModelUrn = this.NAMESPACE + 'TestSet';
defaultSet.addPreferredName('en' , 'Test Set');
defaultSet.addDescription('en' , 'This is a test set.');
defaultSet.addSeeReference('http:\/\/example.com\/');
 return defaultSet; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(null, 
null, 
null, 
2,2,)
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
return 'AspectWithComplexSet';
}

getAspectModelUrn(): string {
return MetaAspectWithComplexSet .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithComplexSet';
}

                        getProperties(): Array<StaticProperty<AspectWithComplexSet, any>> {
return [MetaAspectWithComplexSet.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithComplexSet, any>> {
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


