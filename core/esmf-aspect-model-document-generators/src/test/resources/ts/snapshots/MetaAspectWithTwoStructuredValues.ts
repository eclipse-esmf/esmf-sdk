













import { AspectWithTwoStructuredValues,} from './AspectWithTwoStructuredValues';
import { DefaultScalar,DefaultStructuredValue,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithTwoStructuredValues (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTwoStructuredValues).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithTwoStructuredValues implements StaticMetaClass<AspectWithTwoStructuredValues>, PropertyContainer<AspectWithTwoStructuredValues> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTwoStructuredValues';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithTwoStructuredValues();


 public static readonly  START_DATE = 
                
        new (class extends DefaultStaticProperty<AspectWithTwoStructuredValues, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValues';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultStructuredValue = new DefaultStructuredValue(null, 
null, 
null, 
'(\\d{4})-(\\d{2})-(\\d{2})',[START_DATE_YEAR,
'-',
START_DATE_MONTH,
'-',
START_DATE_DAY],new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultStructuredValue.addAspectModelUrn = this.NAMESPACE + 'StructuredDate';
 return defaultStructuredValue; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( ''2019-09-27'' )),
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

        null,
    null,
    null,
    (() => { const defaultStructuredValue = new DefaultStructuredValue(null, 
null, 
null, 
'(\\d{4})-(\\d{2})-(\\d{2})',[END_DATE_YEAR,
'-',
END_DATE_MONTH,
'-',
END_DATE_DAY],new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultStructuredValue.addAspectModelUrn = this.NAMESPACE + 'StructuredDate';
 return defaultStructuredValue; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( ''2019-09-27'' )),
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
return KnownVersionUtils.getLatest()
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


