













import { AspectWithNumericStructuredValue,} from './AspectWithNumericStructuredValue';
import { DefaultCharacteristic,DefaultScalar,DefaultStructuredValue,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithNumericStructuredValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNumericStructuredValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithNumericStructuredValue implements StaticMetaClass<AspectWithNumericStructuredValue>, PropertyContainer<AspectWithNumericStructuredValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNumericStructuredValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithNumericStructuredValue();


 public static readonly  YEAR = 
                
        new (class extends StaticContainerProperty<AspectWithNumericStructuredValue, number, number> {

    
    getPropertyType(): string {
            return 'number';
    }

    getContainingType(): string {
        return 'AspectWithNumericStructuredValue';
    }

        getContainedType(): string {
            return 'AspectWithNumericStructuredValue';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithNumericStructuredValue',
    'year',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Year',
'Year',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedInt" ))
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
                
        new (class extends StaticContainerProperty<AspectWithNumericStructuredValue, number, number> {

    
    getPropertyType(): string {
            return 'number';
    }

    getContainingType(): string {
        return 'AspectWithNumericStructuredValue';
    }

        getContainedType(): string {
            return 'AspectWithNumericStructuredValue';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithNumericStructuredValue',
    'month',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Month',
'Month',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedInt" ))
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
                
        new (class extends StaticContainerProperty<AspectWithNumericStructuredValue, number, number> {

    
    getPropertyType(): string {
            return 'number';
    }

    getContainingType(): string {
        return 'AspectWithNumericStructuredValue';
    }

        getContainedType(): string {
            return 'AspectWithNumericStructuredValue';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithNumericStructuredValue',
    'day',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Day',
'Day',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedInt" ))
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
                
        new (class extends DefaultStaticProperty<AspectWithNumericStructuredValue, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithNumericStructuredValue';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithNumericStructuredValue',
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
return 'AspectWithNumericStructuredValue';
}

getAspectModelUrn(): string {
return MetaAspectWithNumericStructuredValue .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithNumericStructuredValue';
}

getProperties(): Array<StaticProperty<AspectWithNumericStructuredValue, any>> {
return [MetaAspectWithNumericStructuredValue.DATE];
}

getAllProperties(): Array<StaticProperty<AspectWithNumericStructuredValue, any>> {
        return this.getProperties();
}




    }


