import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithComplexSet,} from './AspectWithComplexSet';
import { DefaultEntity,DefaultLengthConstraint,DefaultSet,DefaultTrait,} from './esmf/aspect-meta-model';

import { KnownVersion,} from './esmf/shared/known-version';

import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithComplexSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithComplexSet implements StaticMetaClass<AspectWithComplexSet>, PropertyContainer<AspectWithComplexSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithComplexSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithComplexSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithComplexSet, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithComplexSet';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithComplexSet',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestTrait',
'TestTrait',
(() => { const defaultSet = new DefaultSet(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestSet',
'TestSet',
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Unique ReplacedAspectArtifactentifier');
 return defaultEntityReplacedAspectArtifact; })())
defaultSet.addAspectModelUrn = this.NAMESPACE + 'TestSet';
defaultSet.addPreferredName('en' , 'Test Set');
defaultSet.addDescription('en' , 'This is a test set.');
defaultSet.addSeeReference('http:\/\/example.com\/');
 return defaultSet; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestSetConstraint',
'TestSetConstraint',
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
return KnownVersion.getLatest()
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


