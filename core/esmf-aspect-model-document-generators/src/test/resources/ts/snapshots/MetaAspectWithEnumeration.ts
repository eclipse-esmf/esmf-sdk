import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumeration,} from './AspectWithEnumeration';
import { DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';



    

/*
* Generated class MetaAspectWithEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEnumeration implements StaticMetaClass<AspectWithEnumeration>, PropertyContainer<AspectWithEnumeration> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumeration';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumeration();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumeration, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumeration';
    }


    getValue( object : AspectWithEnumeration) : ReplacedAspectArtifact {
        return object.testProperty;
    }

        setValue( object : AspectWithEnumeration, value : ReplacedAspectArtifact ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumeration',
    'testProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'1'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'2'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'3')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addPreferredName('en' , 'Test Enumeration');
defaultEnumeration.addDescription('en' , 'This is a test for enumeration.');
defaultEnumeration.addSeeReference('http:\/\/example.com\/');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEnumeration';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumeration .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEnumeration';
}

getProperties(): Array<StaticProperty<AspectWithEnumeration, any>> {
return [MetaAspectWithEnumeration.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumeration, any>> {
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


