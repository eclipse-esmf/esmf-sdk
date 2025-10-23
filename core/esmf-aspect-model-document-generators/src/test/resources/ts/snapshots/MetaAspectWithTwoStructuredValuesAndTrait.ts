













import { AspectWithTwoStructuredValuesAndTrait,} from './AspectWithTwoStructuredValuesAndTrait';
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
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'startDate',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'StructuredDateWithConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultStructuredValue({
urn : this.NAMESPACE + 'StructuredDate',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),"(\\d{4})-(\\d{2})-(\\d{2})",new ArrayList<Object>(){{add(START_DATE_YEAR);add("-");add(START_DATE_MONTH);add("-");add(START_DATE_DAY);}}),new ArrayList<Constraint>(){{add(new DefaultRegularExpressionConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},"[\\d-]*"));}})
,
    exampleValue : {
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendar( "2019-09-27" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'startDate',
    isAbstract : false,
    });




 public static readonly  END_DATE = 
                
        new (class extends DefaultStaticProperty<AspectWithTwoStructuredValuesAndTrait, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithTwoStructuredValuesAndTrait';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'endDate',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'StructuredDateWithConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultStructuredValue({
urn : this.NAMESPACE + 'StructuredDate',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),"(\\d{4})-(\\d{2})-(\\d{2})",new ArrayList<Object>(){{add(END_DATE_YEAR);add("-");add(END_DATE_MONTH);add("-");add(END_DATE_DAY);}}),new ArrayList<Constraint>(){{add(new DefaultRegularExpressionConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},"[\\d-]*"));}})
,
    exampleValue : {
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendar( "2019-09-27" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#date" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'endDate',
    isAbstract : false,
    });




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


