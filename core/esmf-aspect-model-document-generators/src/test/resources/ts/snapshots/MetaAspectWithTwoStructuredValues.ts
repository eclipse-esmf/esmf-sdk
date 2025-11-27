













import { AspectWithTwoStructuredValues,} from './AspectWithTwoStructuredValues';
import { DefaultCharacteristic,DefaultScalar,DefaultStructuredValue,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithTwoStructuredValues (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTwoStructuredValues).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithTwoStructuredValues implements StaticMetaClass<AspectWithTwoStructuredValues>, PropertyContainer<AspectWithTwoStructuredValues> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTwoStructuredValues';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithTwoStructuredValues();


 public static readonly  END_DATE_YEAR = 
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValues, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }

        getContainedType(): string {
            return 'AspectWithTwoStructuredValues';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValues',
    'endDateYear',
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
        'endDateYear',
    false,
    );




 public static readonly  END_DATE_MONTH = 
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValues, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }

        getContainedType(): string {
            return 'AspectWithTwoStructuredValues';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValues',
    'endDateMonth',
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
        'endDateMonth',
    false,
    );




 public static readonly  END_DATE_DAY = 
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValues, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }

        getContainedType(): string {
            return 'AspectWithTwoStructuredValues';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValues',
    'endDateDay',
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
        'endDateDay',
    false,
    );




 public static readonly  START_DATE_YEAR = 
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValues, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }

        getContainedType(): string {
            return 'AspectWithTwoStructuredValues';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValues',
    'startDateYear',
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
        'startDateYear',
    false,
    );




 public static readonly  START_DATE_MONTH = 
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValues, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }

        getContainedType(): string {
            return 'AspectWithTwoStructuredValues';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValues',
    'startDateMonth',
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
        'startDateMonth',
    false,
    );




 public static readonly  START_DATE_DAY = 
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValues, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }

        getContainedType(): string {
            return 'AspectWithTwoStructuredValues';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValues',
    'startDateDay',
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
        'startDateDay',
    false,
    );




 public static readonly  START_DATE = 
                
        new (class extends DefaultStaticProperty<AspectWithTwoStructuredValues, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValues',
    'startDate',
    (() => { const defaultStructuredValue = new DefaultStructuredValue(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StructuredDate',
'StructuredDate',
'(\\d{4})-(\\d{2})-(\\d{2})',[this.START_DATE_YEAR,
'-',
this.START_DATE_MONTH,
'-',
this.START_DATE_DAY],new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultStructuredValue.addAspectModelUrn = this.NAMESPACE + 'StructuredDate';
 return defaultStructuredValue; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( '2019-09-27' )),
        'startDate',
    false,
    );




 public static readonly  END_DATE = 
                
        new (class extends DefaultStaticProperty<AspectWithTwoStructuredValues, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValues',
    'endDate',
    (() => { const defaultStructuredValue = new DefaultStructuredValue(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StructuredDate',
'StructuredDate',
'(\\d{4})-(\\d{2})-(\\d{2})',[this.END_DATE_YEAR,
'-',
this.END_DATE_MONTH,
'-',
this.END_DATE_DAY],new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultStructuredValue.addAspectModelUrn = this.NAMESPACE + 'StructuredDate';
 return defaultStructuredValue; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( '2019-09-27' )),
        'endDate',
    false,
    );




getModelClass(): string {
return 'AspectWithTwoStructuredValues';
}

getAspectModelUrn(): string {
return MetaAspectWithTwoStructuredValues .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithTwoStructuredValues';
}

getProperties(): Array<StaticProperty<AspectWithTwoStructuredValues, any>> {
return [MetaAspectWithTwoStructuredValues.START_DATE, MetaAspectWithTwoStructuredValues.END_DATE];
}

getAllProperties(): Array<StaticProperty<AspectWithTwoStructuredValues, any>> {
        return this.getProperties();
}




    }


