













import { AspectWithTwoStructuredValuesAndTrait,} from './AspectWithTwoStructuredValuesAndTrait';
import { DefaultCharacteristic,DefaultRegularExpressionConstraint,DefaultScalar,DefaultStructuredValue,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithTwoStructuredValuesAndTrait (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTwoStructuredValuesAndTrait).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithTwoStructuredValuesAndTrait implements StaticMetaClass<AspectWithTwoStructuredValuesAndTrait>, PropertyContainer<AspectWithTwoStructuredValuesAndTrait> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithTwoStructuredValuesAndTrait();


 public static readonly  END_DATE_YEAR = 
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValuesAndTrait, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }

        getContainedType(): string {
            return 'Date';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait',
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
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValuesAndTrait, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }

        getContainedType(): string {
            return 'Date';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait',
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
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValuesAndTrait, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }

        getContainedType(): string {
            return 'Date';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait',
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
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValuesAndTrait, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }

        getContainedType(): string {
            return 'Date';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait',
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
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValuesAndTrait, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }

        getContainedType(): string {
            return 'Date';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait',
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
                
        new (class extends StaticContainerProperty<AspectWithTwoStructuredValuesAndTrait, Date, Date> {

    
    getPropertyType(): string {
            return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }

        getContainedType(): string {
            return 'Date';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait',
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
                
        new (class extends DefaultStaticProperty<AspectWithTwoStructuredValuesAndTrait, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait',
    'startDate',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StructuredDateWithConstraint',
'StructuredDateWithConstraint',
(() => { const defaultStructuredValue = new DefaultStructuredValue(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StructuredDate',
'StructuredDate',
'(\\d{4})-(\\d{2})-(\\d{2})',[this.START_DATE_YEAR,
'-',
this.START_DATE_MONTH,
'-',
this.START_DATE_DAY],new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultStructuredValue.addAspectModelUrn = this.NAMESPACE + 'StructuredDate';
 return defaultStructuredValue; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
'[\\d-]*')
regularExpressionConstraint.isAnonymousNode = true;
 return regularExpressionConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'StructuredDateWithConstraint';
 return trait; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( '2019-09-27' )),
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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTwoStructuredValuesAndTrait',
    'endDate',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StructuredDateWithConstraint',
'StructuredDateWithConstraint',
(() => { const defaultStructuredValue = new DefaultStructuredValue(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'StructuredDate',
'StructuredDate',
'(\\d{4})-(\\d{2})-(\\d{2})',[this.END_DATE_YEAR,
'-',
this.END_DATE_MONTH,
'-',
this.END_DATE_DAY],new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ))
defaultStructuredValue.addAspectModelUrn = this.NAMESPACE + 'StructuredDate';
 return defaultStructuredValue; })(),[(() => { const regularExpressionConstraint = new DefaultRegularExpressionConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
'[\\d-]*')
regularExpressionConstraint.isAnonymousNode = true;
 return regularExpressionConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'StructuredDateWithConstraint';
 return trait; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),new Date( '2019-09-27' )),
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
return KnownVersion.getLatest()
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


