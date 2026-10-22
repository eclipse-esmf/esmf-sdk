













import { AspectWithStructuredValue,} from './AspectWithStructuredValue';
import { DefaultCharacteristic,DefaultScalar,DefaultStructuredValue,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithStructuredValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithStructuredValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithStructuredValue implements StaticMetaClass<AspectWithStructuredValue>, PropertyContainer<AspectWithStructuredValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithStructuredValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithStructuredValue();


 public static readonly  YEAR = 
                
        new (class extends StaticContainerProperty<AspectWithStructuredValue, string, string> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithStructuredValue';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithStructuredValue) : string {
        return object.year;
    }

        setValue( object : AspectWithStructuredValue, value : string ) {
            object.year = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithStructuredValue',
    'year',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Year',
'Year',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYear" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'Year';
 return defaultCharacteristic; })()
,
    false,
    true,
    undefined,
        'year',
    false,
    );




 public static readonly  MONTH = 
                
        new (class extends StaticContainerProperty<AspectWithStructuredValue, string, string> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithStructuredValue';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithStructuredValue) : string {
        return object.month;
    }

        setValue( object : AspectWithStructuredValue, value : string ) {
            object.month = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithStructuredValue',
    'month',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Month',
'Month',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonth" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'Month';
 return defaultCharacteristic; })()
,
    false,
    true,
    undefined,
        'month',
    false,
    );




 public static readonly  DAY = 
                
        new (class extends StaticContainerProperty<AspectWithStructuredValue, string, string> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithStructuredValue';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithStructuredValue) : string {
        return object.day;
    }

        setValue( object : AspectWithStructuredValue, value : string ) {
            object.day = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithStructuredValue',
    'day',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Day',
'Day',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonthDay" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'Day';
 return defaultCharacteristic; })()
,
    false,
    true,
    undefined,
        'day',
    false,
    );




 public static readonly  DATE = 
                
        new (class extends DefaultStaticProperty<AspectWithStructuredValue, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithStructuredValue';
    }


    getValue( object : AspectWithStructuredValue) : string {
        return object.date;
    }

        setValue( object : AspectWithStructuredValue, value : string ) {
            object.date = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithStructuredValue',
    'date',
    (() => { const defaultStructuredValue = new DefaultStructuredValue(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StructuredDate',
'StructuredDate',
'(\\d{4})-(\\d{2})-(\\d{2})',[this.YEAR,
'-',
this.MONTH,
'-',
this.DAY],new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultStructuredValue.addAspectModelUrn = this.NAMESPACE + 'StructuredDate';
 return defaultStructuredValue; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( '2019-09-27' )),
        'date',
    false,
    );




getModelClass(): string {
return 'AspectWithStructuredValue';
}

getAspectModelUrn(): string {
return MetaAspectWithStructuredValue .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithStructuredValue';
}

getProperties(): Array<StaticProperty<AspectWithStructuredValue, any>> {
return [MetaAspectWithStructuredValue.DATE];
}

getAllProperties(): Array<StaticProperty<AspectWithStructuredValue, any>> {
        return this.getProperties();
}




    }


