













import { AspectWithRegularExpressionConstraint,} from './AspectWithRegularExpressionConstraint';
import { DefaultCharacteristic,DefaultRegularExpressionConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithRegularExpressionConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRegularExpressionConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithRegularExpressionConstraint) : string {
        return object.testProperty;
    }

        setValue( object : AspectWithRegularExpressionConstraint, value : string ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithRegularExpressionConstraint',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestRegularExpressionConstraint',
'TestRegularExpressionConstraint',
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
return KnownVersion.getLatest()
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


