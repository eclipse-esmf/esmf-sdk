













import { AspectWithUsedAndUnusedConstraint,} from './AspectWithUsedAndUnusedConstraint';
import { DefaultCharacteristic,DefaultLengthConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithUsedAndUnusedConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithUsedAndUnusedConstraint implements StaticMetaClass<AspectWithUsedAndUnusedConstraint>, PropertyContainer<AspectWithUsedAndUnusedConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUsedAndUnusedConstraint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithUsedAndUnusedConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithUsedAndUnusedConstraint';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithUsedAndUnusedConstraint',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'UsedTestTrait',
'UsedTestTrait',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
5,5,)
lengthConstraint.isAnonymousNode = true;
lengthConstraint.addPreferredName('en' , 'Used Test Constraint');
lengthConstraint.addDescription('en' , 'Used Test Constraint');
lengthConstraint.addSeeReference('http:\/\/example.com\/');
lengthConstraint.addSeeReference('http:\/\/example.com\/me');
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'UsedTestTrait';
 return trait; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithUsedAndUnusedConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithUsedAndUnusedConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithUsedAndUnusedConstraint';
}

getProperties(): Array<StaticProperty<AspectWithUsedAndUnusedConstraint, any>> {
return [MetaAspectWithUsedAndUnusedConstraint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUsedAndUnusedConstraint, any>> {
        return this.getProperties();
}




    }


