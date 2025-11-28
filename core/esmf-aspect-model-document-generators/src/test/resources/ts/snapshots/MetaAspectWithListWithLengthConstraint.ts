













import { AspectWithListWithLengthConstraint,} from './AspectWithListWithLengthConstraint';
import { DefaultLengthConstraint,DefaultList,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithListWithLengthConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithListWithLengthConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithListWithLengthConstraint implements StaticMetaClass<AspectWithListWithLengthConstraint>, PropertyContainer<AspectWithListWithLengthConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithListWithLengthConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithListWithLengthConstraint();


 public static readonly  TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT = 
                
        new (class extends StaticContainerProperty<AspectWithListWithLengthConstraint, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithListWithLengthConstraint';
    }

        getContainedType(): string {
            return 'string';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithListWithLengthConstraint',
    'testPropertyCollectionLengthConstraint',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestLengthConstraintWithCollection',
'TestLengthConstraintWithCollection',
(() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultList.isAnonymousNode = true;
 return defaultList; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
1,1,)
lengthConstraint.isAnonymousNode = true;
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestLengthConstraintWithCollection';
trait.addPreferredName('en' , 'Test Length Constraint with collection');
trait.addDescription('en' , 'Test Length Constraint with collection');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyCollectionLengthConstraint',
    false,
    );




getModelClass(): string {
return 'AspectWithListWithLengthConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithListWithLengthConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithListWithLengthConstraint';
}

getProperties(): Array<StaticProperty<AspectWithListWithLengthConstraint, any>> {
return [MetaAspectWithListWithLengthConstraint.TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT];
}

getAllProperties(): Array<StaticProperty<AspectWithListWithLengthConstraint, any>> {
        return this.getProperties();
}




    }


