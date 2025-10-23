













import { AspectWithGTypeForRangeConstraints,} from './AspectWithGTypeForRangeConstraints';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithGTypeForRangeConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithGTypeForRangeConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithGTypeForRangeConstraints implements StaticMetaClass<AspectWithGTypeForRangeConstraints>, PropertyContainer<AspectWithGTypeForRangeConstraints> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithGTypeForRangeConstraints';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithGTypeForRangeConstraints();


 public static readonly  TEST_PROPERTY_WITH_G_YEAR = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithGYear',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGYear',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementGYear',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYear" ),Units.fromName("year")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendarDate( Integer.valueOf( "2000" ), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYear" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendarDate( Integer.valueOf( "2001" ), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYear" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithGYear',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_G_MONTH = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithGMonth',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGMonth',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementGMonth',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonth" ),Units.fromName("month")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendarDate( DatatypeConstants.FIELD_UNDEFINED, Integer.valueOf( "--04" ), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonth" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendarDate( DatatypeConstants.FIELD_UNDEFINED, Integer.valueOf( "--05" ), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonth" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithGMonth',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_G_DAY = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithGDay',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGDay',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementGDay',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#gDay" ),Units.fromName("day")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendar( "---04" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gDay" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendar( "---05" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gDay" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithGDay',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_G_YEAR_MONTH = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithGYearMonth',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGYearMonth',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementGYearMonth',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYearMonth" ),Units.fromName("one")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendar( "2000-01" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYearMonth" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendar( "2000-02" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gYearMonth" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithGYearMonth',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_G_MONTH_YEAR = 
                
        new (class extends DefaultStaticProperty<AspectWithGTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithGTypeForRangeConstraints';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithGMonthYear',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'testWithGregorianCalendarMinGregorianCalendarMaxGMonthYear',
preferredNames : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Range",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'MeasurementGMonthYear',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonthDay" ),Units.fromName("one")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendarDate( DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, Integer.valueOf( "--01-01" ), DatatypeConstants.FIELD_UNDEFINED ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonthDay" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : _datatypeFactory.newXMLGregorianCalendarDate( DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, Integer.valueOf( "--01-02" ), DatatypeConstants.FIELD_UNDEFINED ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#gMonthDay" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithGMonthYear',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithGTypeForRangeConstraints';
}

getAspectModelUrn(): string {
return MetaAspectWithGTypeForRangeConstraints .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithGTypeForRangeConstraints';
}

                        getProperties(): Array<StaticProperty<AspectWithGTypeForRangeConstraints, any>> {
return [MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_YEAR, MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_MONTH, MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_DAY, MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_YEAR_MONTH, MetaAspectWithGTypeForRangeConstraints.TEST_PROPERTY_WITH_G_MONTH_YEAR];
}

getAllProperties(): Array<StaticProperty<AspectWithGTypeForRangeConstraints, any>> {
    return this.getProperties();
}




    }


