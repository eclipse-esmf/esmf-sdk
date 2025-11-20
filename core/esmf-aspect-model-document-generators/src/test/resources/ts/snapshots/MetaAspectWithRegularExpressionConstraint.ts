













import { AspectWithRegularExpressionConstraint,} from './AspectWithRegularExpressionConstraint';
import { DefaultCharacteristic,DefaultRegularExpressionConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithRegularExpressionConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRegularExpressionConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRegularExpressionConstraint implements StaticMetaClass<AspectWithRegularExpressionConstraint>, PropertyContainer<AspectWithRegularExpressionConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRegularExpressionConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRegularExpressionConstraint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRegularExpressionConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithRegularExpressionConstraint';
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
'^[0-9]*$')
regularExpressionConstraint.isAnonymousNode = true;
regularExpressionConstraint.addPreferredName('en' , 'Test Regular Expression Constraint');
regularExpressionConstraint.addDescription('en' , 'This is a test regular expression constraint.');
regularExpressionConstraint.addSeeReference('http:\/\/example.com\/');
 return regularExpressionConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestRegularExpressionConstraint';
 return trait; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'3'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithRegularExpressionConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithRegularExpressionConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRegularExpressionConstraint';
}

                        getProperties(): Array<StaticProperty<AspectWithRegularExpressionConstraint, any>> {
return [MetaAspectWithRegularExpressionConstraint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRegularExpressionConstraint, any>> {
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


