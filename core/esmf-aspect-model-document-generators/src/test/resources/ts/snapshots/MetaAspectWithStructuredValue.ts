













import { AspectWithStructuredValue,} from './AspectWithStructuredValue';
import { DefaultScalar,DefaultStructuredValue,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithStructuredValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithStructuredValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithStructuredValue implements StaticMetaClass<AspectWithStructuredValue>, PropertyContainer<AspectWithStructuredValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithStructuredValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithStructuredValue();


 public static readonly  DATE = 
                
        new (class extends DefaultStaticProperty<AspectWithStructuredValue, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithStructuredValue';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultStructuredValue = new DefaultStructuredValue(null, 
null, 
null, 
'(\\d{4})-(\\d{2})-(\\d{2})',[YEAR,
'-',
MONTH,
'-',
DAY],new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultStructuredValue.addAspectModelUrn = this.NAMESPACE + 'StructuredDate';
 return defaultStructuredValue; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( ''2019-09-27'' )),
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
return KnownVersionUtils.getLatest()
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


