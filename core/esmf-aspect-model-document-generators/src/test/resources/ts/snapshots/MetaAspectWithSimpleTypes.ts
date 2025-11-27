













import { AspectWithSimpleTypes,} from './AspectWithSimpleTypes';
import { DefaultCharacteristic,DefaultQuantifiable,DefaultQuantityKind,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,StaticUnitProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithSimpleTypes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimpleTypes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'anyUriProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'base64BinaryProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'booleanProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Boolean',
'Boolean',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'byteProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'curieProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#UnitReference',
'UnitReference',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'dateProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'dateTimeProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Timestamp',
'Timestamp',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'dateTimeStampProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'dayTimeDuration',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'decimalProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'doubleProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'durationProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'floatProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'gDayProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'gMonthDayProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'gMonthProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'gYearMonthProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'gYearProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'hexBinaryProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'intProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'integerProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
                
        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, MultiLanguageText>{

    
    getPropertyType(): string {
                return 'MultiLanguageText';
    }

    getContainingType(): string {
        return 'AspectWithSimpleTypes';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'langStringProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#MultiLanguageText',
'MultiLanguageText',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'longProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'negativeIntegerProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'nonNegativeIntegerProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'nonPositiveInteger',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'positiveIntegerProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'shortProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'stringProperty',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'timeProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'unsignedByteProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'unsignedIntProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'unsignedLongProperty',
    (() => { const defaultQuantifiable = new DefaultQuantifiable(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedLong" ),(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metre',
'metre',
'm','MTR',undefined,'m',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#fundamentalLatticeVector',
'fundamentalLatticeVector',
'fundamental lattice vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#halfThickness',
'halfThickness',
'half-thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#radiusOfCurvature',
'radiusOfCurvature',
'radius of curvature')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#distance',
'distance',
'distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#equilibriumPositionVectorOfIonOrAtom',
'equilibriumPositionVectorOfIonOrAtom',
'equilibrium position vector of ion or atom')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#slowingDownLength',
'slowingDownLength',
'slowing-down length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#height',
'height',
'height')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#halfValueThickness',
'halfValueThickness',
'half-value thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#diffusionCoefficientForNeutronFluenceRate',
'diffusionCoefficientForNeutronFluenceRate',
'diffusion coefficient for neutron fluence rate')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#meanFreePathOfPhononsOrElectrons',
'meanFreePathOfPhononsOrElectrons',
'mean free path of phonons or electrons')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#bohrRadius',
'bohrRadius',
'Bohr radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#instantaneousSoundParticleDisplacement',
'instantaneousSoundParticleDisplacement',
'(instantaneous) sound particle displacement')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#electronRadius',
'electronRadius',
'electron radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#lengthOfPath',
'lengthOfPath',
'length of path')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#coherenceLength',
'coherenceLength',
'coherence length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#diffusionCoefficientForNeutronFluxDensity',
'diffusionCoefficientForNeutronFluxDensity',
'diffusion coefficient for neutron flux density')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#migrationLength',
'migrationLength',
'migration length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#meanLinearRange',
'meanLinearRange',
'mean linear range')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#radius',
'radius',
'radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#displacementVectorOfIonOrAtom',
'displacementVectorOfIonOrAtom',
'displacement vector of ion or atom')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#latticeVector',
'latticeVector',
'lattice vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#meanFreePath',
'meanFreePath',
'mean free path')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#length',
'length',
'length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#breadth',
'breadth',
'breadth')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#londonPenetrationDepth',
'londonPenetrationDepth',
'London penetration depth')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#nuclearRadius',
'nuclearRadius',
'nuclear radius')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#comptonWavelength',
'comptonWavelength',
'Compton wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#particlePositionVector',
'particlePositionVector',
'particle position vector')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#diffusionLength',
'diffusionLength',
'diffusion length')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#focalDistance',
'focalDistance',
'focal distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#cartesianCoordinates',
'cartesianCoordinates',
'cartesian coordinates')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#thickness',
'thickness',
'thickness')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#wavelength',
'wavelength',
'wavelength')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#diameter',
'diameter',
'diameter')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#imageDistance',
'imageDistance',
'image distance')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#objectDistance',
'objectDistance',
'object distance')
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'unsignedShortProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithSimpleTypes',
    'yearMonthDurationProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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


