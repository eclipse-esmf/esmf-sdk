













import { AspectWithSimpleProperties,} from './AspectWithSimpleProperties';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithSimpleProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimpleProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSimpleProperties implements StaticMetaClass<AspectWithSimpleProperties>, PropertyContainer<AspectWithSimpleProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSimpleProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSimpleProperties();


 public static readonly  TEST_STRING = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleProperties';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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
                
        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleProperties';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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
                
        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleProperties';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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
                
        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleProperties';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'LocalDateTime';
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ),new Date( ''2018-02-28T14:23:32.918'' )),
        'testLocalDateTime',
    false,
    );




 public static readonly  TEST_LOCAL_DATE_TIME_WITHOUT_EXAMPLE = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleProperties';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'LocalDateTime';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testLocalDateTimeWithoutExample',
    false,
    );




 public static readonly  TEST_DURATION_WITHOUT_EXAMPLE = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithSimpleProperties';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#duration" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'Duration';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testDurationWithoutExample',
    false,
    );




 public static readonly  RANDOM_VALUE = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleProperties';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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




getModelClass(): string {
return 'AspectWithSimpleProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithSimpleProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithSimpleProperties';
}

                        getProperties(): Array<StaticProperty<AspectWithSimpleProperties, any>> {
return [MetaAspectWithSimpleProperties.TEST_STRING, MetaAspectWithSimpleProperties.TEST_INT, MetaAspectWithSimpleProperties.TEST_FLOAT, MetaAspectWithSimpleProperties.TEST_LOCAL_DATE_TIME, MetaAspectWithSimpleProperties.TEST_LOCAL_DATE_TIME_WITHOUT_EXAMPLE, MetaAspectWithSimpleProperties.TEST_DURATION_WITHOUT_EXAMPLE, MetaAspectWithSimpleProperties.RANDOM_VALUE];
}

getAllProperties(): Array<StaticProperty<AspectWithSimpleProperties, any>> {
    return this.getProperties();
}




    }


