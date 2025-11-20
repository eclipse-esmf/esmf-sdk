













import { AspectWithTwoStructuredValuesAndTrait,} from './AspectWithTwoStructuredValuesAndTrait';
import { DefaultRegularExpressionConstraint,DefaultScalar,DefaultStructuredValue,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithTwoStructuredValuesAndTrait (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTwoStructuredValuesAndTrait).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithTwoStructuredValuesAndTrait implements StaticMetaClass<AspectWithTwoStructuredValuesAndTrait>, PropertyContainer<AspectWithTwoStructuredValuesAndTrait> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithTwoStructuredValuesAndTrait();


 public static readonly  START_DATE = 
                
        new (class extends DefaultStaticProperty<AspectWithTwoStructuredValuesAndTrait, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }


                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
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
 return defaultStructuredValue; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(null, 
null, 
null, 
'[\\d-]*')
regularExpressionConstraint.isAnonymousNode = true;
 return regularExpressionConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'StructuredDateWithConstraint';
 return trait; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( ''2019-09-27'' )),
        'startDate',
    false,
    );




 public static readonly  END_DATE = 
                
        new (class extends DefaultStaticProperty<AspectWithTwoStructuredValuesAndTrait, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }


                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
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
 return defaultStructuredValue; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(null, 
null, 
null, 
'[\\d-]*')
regularExpressionConstraint.isAnonymousNode = true;
 return regularExpressionConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'StructuredDateWithConstraint';
 return trait; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( ''2019-09-27'' )),
        'endDate',
    false,
    );




getModelClass(): string {
return 'AspectWithTwoStructuredValuesAndTrait';
}

getAspectModelUrn(): string {
return MetaAspectWithTwoStructuredValuesAndTrait .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithTwoStructuredValuesAndTrait';
}

                        getProperties(): Array<StaticProperty<AspectWithTwoStructuredValuesAndTrait, any>> {
return [MetaAspectWithTwoStructuredValuesAndTrait.START_DATE, MetaAspectWithTwoStructuredValuesAndTrait.END_DATE];
}

getAllProperties(): Array<StaticProperty<AspectWithTwoStructuredValuesAndTrait, any>> {
    return this.getProperties();
}




    }


