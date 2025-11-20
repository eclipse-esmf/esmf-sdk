













import { AspectWithEncodingConstraint,} from './AspectWithEncodingConstraint';
import { DefaultCharacteristic,DefaultEncodingConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithEncodingConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEncodingConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEncodingConstraint implements StaticMetaClass<AspectWithEncodingConstraint>, PropertyContainer<AspectWithEncodingConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEncodingConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEncodingConstraint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEncodingConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithEncodingConstraint';
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
 return defaultCharacteristic; })(),[(() => { const encodingConstraint = new DefaultEncodingConstraint(null, 
null, 
null, 
'UTF-8')
encodingConstraint.isAnonymousNode = true;
encodingConstraint.addPreferredName('en' , 'Test Encoding Constraint');
encodingConstraint.addDescription('en' , 'This is a test encoding constraint.');
encodingConstraint.addSeeReference('http:\/\/example.com\/');
 return encodingConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestEncodingConstraint';
 return trait; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEncodingConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithEncodingConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEncodingConstraint';
}

                        getProperties(): Array<StaticProperty<AspectWithEncodingConstraint, any>> {
return [MetaAspectWithEncodingConstraint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEncodingConstraint, any>> {
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


