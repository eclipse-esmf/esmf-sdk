













import { AspectWithNumericRegularExpressionConstraint,} from './AspectWithNumericRegularExpressionConstraint';
import { DefaultCharacteristic,DefaultRegularExpressionConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithNumericRegularExpressionConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNumericRegularExpressionConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithNumericRegularExpressionConstraint implements StaticMetaClass<AspectWithNumericRegularExpressionConstraint>, PropertyContainer<AspectWithNumericRegularExpressionConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNumericRegularExpressionConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithNumericRegularExpressionConstraint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithNumericRegularExpressionConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithNumericRegularExpressionConstraint';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithNumericRegularExpressionConstraint',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestTrait',
'TestTrait',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
'\\d*|x')
regularExpressionConstraint.isAnonymousNode = true;
 return regularExpressionConstraint; })()])
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
return 'AspectWithNumericRegularExpressionConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithNumericRegularExpressionConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithNumericRegularExpressionConstraint';
}

getProperties(): Array<StaticProperty<AspectWithNumericRegularExpressionConstraint, any>> {
return [MetaAspectWithNumericRegularExpressionConstraint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithNumericRegularExpressionConstraint, any>> {
        return this.getProperties();
}




    }


