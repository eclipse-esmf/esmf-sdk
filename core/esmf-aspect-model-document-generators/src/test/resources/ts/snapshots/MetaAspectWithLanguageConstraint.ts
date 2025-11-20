













import { AspectWithLanguageConstraint,} from './AspectWithLanguageConstraint';
import { DefaultCharacteristic,DefaultLanguageConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithLanguageConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithLanguageConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithLanguageConstraint implements StaticMetaClass<AspectWithLanguageConstraint>, PropertyContainer<AspectWithLanguageConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithLanguageConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithLanguageConstraint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithLanguageConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithLanguageConstraint';
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
 return defaultCharacteristic; })(),[(() => { const defaultLanguageConstraint = new DefaultLanguageConstraint(null, 
null, 
null, 
'de')
defaultLanguageConstraint.isAnonymousNode = true;
defaultLanguageConstraint.addPreferredName('en' , 'Test Language Constraint');
defaultLanguageConstraint.addDescription('en' , 'This is a test language constraint.');
defaultLanguageConstraint.addSeeReference('http:\/\/example.com\/');
 return defaultLanguageConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestLanguageConstraint';
 return trait; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithLanguageConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithLanguageConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithLanguageConstraint';
}

                        getProperties(): Array<StaticProperty<AspectWithLanguageConstraint, any>> {
return [MetaAspectWithLanguageConstraint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithLanguageConstraint, any>> {
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


