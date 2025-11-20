













import { AspectWithNumericRegularExpressionConstraint,} from './AspectWithNumericRegularExpressionConstraint';
import { DefaultCharacteristic,DefaultRegularExpressionConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithNumericRegularExpressionConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNumericRegularExpressionConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


