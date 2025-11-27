













import { AspectWithConstrainedSet,} from './AspectWithConstrainedSet';
import { DefaultLengthConstraint,DefaultScalar,DefaultSet,DefaultTrait,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithConstrainedSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstrainedSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithConstrainedSet implements StaticMetaClass<AspectWithConstrainedSet>, PropertyContainer<AspectWithConstrainedSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstrainedSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstrainedSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithConstrainedSet, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstrainedSet';
    }

        getContainedType(): string {
            return 'AspectWithConstrainedSet';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstrainedSet',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestTrait',
'TestTrait',
(() => { const defaultSet = new DefaultSet(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestSet',
'TestSet',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultSet.addAspectModelUrn = this.NAMESPACE + 'TestSet';
defaultSet.addPreferredName('en' , 'Test Set');
defaultSet.addDescription('en' , 'This is a test set.');
defaultSet.addSeeReference('http:\/\/example.com\/');
 return defaultSet; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestSetConstraint',
'TestSetConstraint',
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
return KnownVersion.getLatest()
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


