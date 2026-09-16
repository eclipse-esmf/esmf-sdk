import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithSimplePropertiesAndState,} from './AspectWithSimplePropertiesAndState';

import { DefaultCharacteristic,DefaultScalar,DefaultState,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithSimplePropertiesAndState (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimplePropertiesAndState).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithSimplePropertiesAndState implements StaticMetaClass<AspectWithSimplePropertiesAndState>, PropertyContainer<AspectWithSimplePropertiesAndState> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSimplePropertiesAndState';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSimplePropertiesAndState();


 public static readonly  TEST_STRING = 
                
        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimplePropertiesAndState';
    }


    getValue( object : AspectWithSimplePropertiesAndState) : string {
        return object.testString;
    }

        setValue( object : AspectWithSimplePropertiesAndState, value : string ) {
            object.testString = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimplePropertiesAndState',
    'testString',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Example Value Test'),
        'testString',
    false,
    );




 public static readonly  TEST_INT = 
                
        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimplePropertiesAndState';
    }


    getValue( object : AspectWithSimplePropertiesAndState) : number {
        return object.testInt;
    }

        setValue( object : AspectWithSimplePropertiesAndState, value : number ) {
            object.testInt = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimplePropertiesAndState',
    'testInt',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Int',
'Int',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'Int';
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'3'),
        'testInt',
    false,
    );




 public static readonly  TEST_FLOAT = 
                
        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimplePropertiesAndState';
    }


    getValue( object : AspectWithSimplePropertiesAndState) : number {
        return object.testFloat;
    }

        setValue( object : AspectWithSimplePropertiesAndState, value : number ) {
            object.testFloat = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimplePropertiesAndState',
    'testFloat',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Float',
'Float',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'Float';
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'2.25'),
        'testFloat',
    false,
    );




 public static readonly  TEST_LOCAL_DATE_TIME = 
                
        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimplePropertiesAndState';
    }


    getValue( object : AspectWithSimplePropertiesAndState) : string {
        return object.testLocalDateTime;
    }

        setValue( object : AspectWithSimplePropertiesAndState, value : string ) {
            object.testLocalDateTime = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimplePropertiesAndState',
    'testLocalDateTime',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'LocalDateTime',
'LocalDateTime',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'LocalDateTime';
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ),new Date( '2018-02-28T14:23:32.918' )),
        'testLocalDateTime',
    false,
    );




 public static readonly  RANDOM_VALUE = 
                
        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimplePropertiesAndState';
    }


    getValue( object : AspectWithSimplePropertiesAndState) : string {
        return object.randomValue;
    }

        setValue( object : AspectWithSimplePropertiesAndState, value : string ) {
            object.randomValue = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimplePropertiesAndState',
    'randomValue',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'randomValue',
    false,
    );




 public static readonly  AUTOMATION_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithSimplePropertiesAndState';
    }


    getValue( object : AspectWithSimplePropertiesAndState) : ReplacedAspectArtifact {
        return object.automationProperty;
    }

        setValue( object : AspectWithSimplePropertiesAndState, value : ReplacedAspectArtifact ) {
            object.automationProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimplePropertiesAndState',
    'automationProperty',
    (() => { const defaultState = new DefaultState(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'ReplacedAspectArtifact Default Prop'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'ReplacedAspectArtifact2'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'ReplacedAspectArtifact3')],new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'ReplacedAspectArtifact Default Prop'),new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultState.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultState.addDescription('en' , 'Return status for the Set Configuration Operation');
 return defaultState; })()
,
    false,
    false,
    undefined,
        'automationProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithSimplePropertiesAndState';
}

getAspectModelUrn(): string {
return MetaAspectWithSimplePropertiesAndState .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithSimplePropertiesAndState';
}

getProperties(): Array<StaticProperty<AspectWithSimplePropertiesAndState, any>> {
return [MetaAspectWithSimplePropertiesAndState.TEST_STRING, MetaAspectWithSimplePropertiesAndState.TEST_INT, MetaAspectWithSimplePropertiesAndState.TEST_FLOAT, MetaAspectWithSimplePropertiesAndState.TEST_LOCAL_DATE_TIME, MetaAspectWithSimplePropertiesAndState.RANDOM_VALUE, MetaAspectWithSimplePropertiesAndState.AUTOMATION_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithSimplePropertiesAndState, any>> {
        return this.getProperties();
}




    }


