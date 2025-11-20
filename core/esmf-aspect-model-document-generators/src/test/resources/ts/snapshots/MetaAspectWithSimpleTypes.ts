













import { AspectWithSimpleTypes,} from './AspectWithSimpleTypes';
import { DefaultCharacteristic,DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './aspect-meta-model';
import { DefaultStaticProperty,StaticUnitProperty,} from './core/staticConstraintProperty';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithSimpleTypes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimpleTypes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSimpleTypes implements StaticMetaClass<AspectWithSimpleTypes>, PropertyContainer<AspectWithSimpleTypes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSimpleTypes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSimpleTypes();


 public static readonly  ANY_URI_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#anyURI" ))
defaultCharacteristic.isAnonymousNode = true;
defaultCharacteristic.addDescription('en' , 'This is an anyURI characteristic.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'anyUriProperty',
    false,
    );




 public static readonly  BASE64_BINARY_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#base64Binary" ))
defaultCharacteristic.isAnonymousNode = true;
defaultCharacteristic.addDescription('en' , 'This is a base64Binary characteristic.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'base64BinaryProperty',
    false,
    );




 public static readonly  BOOLEAN_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#boolean" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Boolean';
defaultCharacteristic.addPreferredName('en' , 'Boolean');
defaultCharacteristic.addDescription('en' , 'Represents a boolean value (i.e. a \"flag\").');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'booleanProperty',
    false,
    );




 public static readonly  BYTE_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#byte" ))
defaultCharacteristic.isAnonymousNode = true;
defaultCharacteristic.addDescription('en' , 'This is a byteProperty characteristic.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'byteProperty',
    false,
    );




 public static readonly  CURIE_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#UnitReference';
defaultCharacteristic.addPreferredName('en' , 'Unit Reference');
defaultCharacteristic.addDescription('en' , 'Describes a Property containing a reference to one of the units in the Unit Catalog.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'curieProperty',
    false,
    );




 public static readonly  DATE_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'dateProperty',
    false,
    );




 public static readonly  DATE_TIME_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Timestamp';
defaultCharacteristic.addPreferredName('en' , 'Timestamp');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains the date and time with an optional timezone.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'dateTimeProperty',
    false,
    );




 public static readonly  DATE_TIME_STAMP_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTimeStamp" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'dateTimeStampProperty',
    false,
    );




 public static readonly  DAY_TIME_DURATION = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#dayTimeDuration" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'dayTimeDuration',
    false,
    );




 public static readonly  DECIMAL_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'decimalProperty',
    false,
    );




 public static readonly  DOUBLE_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'doubleProperty',
    false,
    );




 public static readonly  DURATION_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#duration" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'durationProperty',
    false,
    );




 public static readonly  FLOAT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'floatProperty',
    false,
    );




 public static readonly  G_DAY_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#gDay" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'gDayProperty',
    false,
    );




 public static readonly  G_MONTH_DAY_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonthDay" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'gMonthDayProperty',
    false,
    );




 public static readonly  G_MONTH_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonth" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'gMonthProperty',
    false,
    );




 public static readonly  G_YEAR_MONTH_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYearMonth" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'gYearMonthProperty',
    false,
    );




 public static readonly  G_YEAR_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYear" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'gYearProperty',
    false,
    );




 public static readonly  HEX_BINARY_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#hexBinary" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'hexBinaryProperty',
    false,
    );




 public static readonly  INT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'intProperty',
    false,
    );




 public static readonly  INTEGER_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'integerProperty',
    false,
    );




 public static readonly  LANG_STRING_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, LangString>{

    
    getPropertyType(): string {
                return 'LangString';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#MultiLanguageText';
defaultCharacteristic.addPreferredName('en' , 'Multi-Language Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text in multiple languages. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'langStringProperty',
    false,
    );




 public static readonly  LONG_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#long" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'longProperty',
    false,
    );




 public static readonly  NEGATIVE_INTEGER_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#negativeInteger" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'negativeIntegerProperty',
    false,
    );




 public static readonly  NON_NEGATIVE_INTEGER_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'nonNegativeIntegerProperty',
    false,
    );




 public static readonly  NON_POSITIVE_INTEGER = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonPositiveInteger" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'nonPositiveInteger',
    false,
    );




 public static readonly  POSITIVE_INTEGER_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#positiveInteger" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'positiveIntegerProperty',
    false,
    );




 public static readonly  SHORT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#short" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'shortProperty',
    false,
    );




 public static readonly  STRING_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
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
        'stringProperty',
    false,
    );




 public static readonly  TIME_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#time" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'timeProperty',
    false,
    );




 public static readonly  UNSIGNED_BYTE_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedByte" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'unsignedByteProperty',
    false,
    );




 public static readonly  UNSIGNED_INT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedInt" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'unsignedIntProperty',
    false,
    );




 public static readonly  UNSIGNED_LONG_PROPERTY = 
                
        new (class extends StaticUnitProperty<AspectWithSimpleTypes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultQuantifiable = new DefaultQuantifiable(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedLong" ),(() => { const defaultUnit = new DefaultUnit(null, 
null, 
null, 
'm','MTR',undefined,'m',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'(instantaneous) sound particle displacement')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'Bohr radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'half-thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'diffusion coefficient for neutron flux density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'London penetration depth')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'focal distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'breadth')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'particle position vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'migration length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'length of path')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'mean free path of phonons or electrons')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'coherence length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'electron radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'mean free path')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'Compton wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'image distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'displacement vector of ion or atom')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'fundamental lattice vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'slowing-down length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'half-value thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'diffusion coefficient for neutron fluence rate')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'radius of curvature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'diffusion length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'object distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'height')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'nuclear radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'cartesian coordinates')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'diameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'mean linear range')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'lattice vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'equilibrium position vector of ion or atom')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metre';
defaultUnit.addPreferredName('en' , 'metre');
 return defaultUnit; })())
defaultQuantifiable.isAnonymousNode = true;
 return defaultQuantifiable; })()
,
    false,
    false,
    undefined,
        'unsignedLongProperty',
    false,
    );




 public static readonly  UNSIGNED_SHORT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedShort" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'unsignedShortProperty',
    false,
    );




 public static readonly  YEAR_MONTH_DURATION_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#yearMonthDuration" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'yearMonthDurationProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithSimpleTypes';
}

getAspectModelUrn(): string {
return MetaAspectWithSimpleTypes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithSimpleTypes';
}

                        getProperties(): Array<StaticProperty<AspectWithSimpleTypes, any>> {
return [MetaAspectWithSimpleTypes.ANY_URI_PROPERTY, MetaAspectWithSimpleTypes.BASE64_BINARY_PROPERTY, MetaAspectWithSimpleTypes.BOOLEAN_PROPERTY, MetaAspectWithSimpleTypes.BYTE_PROPERTY, MetaAspectWithSimpleTypes.CURIE_PROPERTY, MetaAspectWithSimpleTypes.DATE_PROPERTY, MetaAspectWithSimpleTypes.DATE_TIME_PROPERTY, MetaAspectWithSimpleTypes.DATE_TIME_STAMP_PROPERTY, MetaAspectWithSimpleTypes.DAY_TIME_DURATION, MetaAspectWithSimpleTypes.DECIMAL_PROPERTY, MetaAspectWithSimpleTypes.DOUBLE_PROPERTY, MetaAspectWithSimpleTypes.DURATION_PROPERTY, MetaAspectWithSimpleTypes.FLOAT_PROPERTY, MetaAspectWithSimpleTypes.G_DAY_PROPERTY, MetaAspectWithSimpleTypes.G_MONTH_DAY_PROPERTY, MetaAspectWithSimpleTypes.G_MONTH_PROPERTY, MetaAspectWithSimpleTypes.G_YEAR_MONTH_PROPERTY, MetaAspectWithSimpleTypes.G_YEAR_PROPERTY, MetaAspectWithSimpleTypes.HEX_BINARY_PROPERTY, MetaAspectWithSimpleTypes.INT_PROPERTY, MetaAspectWithSimpleTypes.INTEGER_PROPERTY, MetaAspectWithSimpleTypes.LANG_STRING_PROPERTY, MetaAspectWithSimpleTypes.LONG_PROPERTY, MetaAspectWithSimpleTypes.NEGATIVE_INTEGER_PROPERTY, MetaAspectWithSimpleTypes.NON_NEGATIVE_INTEGER_PROPERTY, MetaAspectWithSimpleTypes.NON_POSITIVE_INTEGER, MetaAspectWithSimpleTypes.POSITIVE_INTEGER_PROPERTY, MetaAspectWithSimpleTypes.SHORT_PROPERTY, MetaAspectWithSimpleTypes.STRING_PROPERTY, MetaAspectWithSimpleTypes.TIME_PROPERTY, MetaAspectWithSimpleTypes.UNSIGNED_BYTE_PROPERTY, MetaAspectWithSimpleTypes.UNSIGNED_INT_PROPERTY, MetaAspectWithSimpleTypes.UNSIGNED_LONG_PROPERTY, MetaAspectWithSimpleTypes.UNSIGNED_SHORT_PROPERTY, MetaAspectWithSimpleTypes.YEAR_MONTH_DURATION_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithSimpleTypes, any>> {
    return this.getProperties();
}




    }


