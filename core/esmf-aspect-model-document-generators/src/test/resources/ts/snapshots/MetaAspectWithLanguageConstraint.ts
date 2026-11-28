













import { AspectWithLanguageConstraint,} from './AspectWithLanguageConstraint';
import { DefaultCharacteristic,DefaultLanguageConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithLanguageConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithLanguageConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithLanguageConstraint) : string {
        return object.testProperty;
    }

        setValue( object : AspectWithLanguageConstraint, value : string ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithLanguageConstraint',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestLanguageConstraint',
'TestLanguageConstraint',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const defaultLanguageConstraint = new DefaultLanguageConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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


