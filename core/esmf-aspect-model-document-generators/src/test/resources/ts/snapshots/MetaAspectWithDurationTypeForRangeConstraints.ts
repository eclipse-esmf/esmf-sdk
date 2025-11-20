













import { AspectWithDurationTypeForRangeConstraints,} from './AspectWithDurationTypeForRangeConstraints';
import { BoundDefinition,} from './aspect-meta-model/bound-definition';
import { DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { DefaultUnit,} from './aspect-meta-model/default-unit';


    

/*
* Generated class MetaAspectWithDurationTypeForRangeConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDurationTypeForRangeConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDurationTypeForRangeConstraints implements StaticMetaClass<AspectWithDurationTypeForRangeConstraints>, PropertyContainer<AspectWithDurationTypeForRangeConstraints> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDurationTypeForRangeConstraints';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDurationTypeForRangeConstraints();


 public static readonly  TEST_PROPERTY_WITH_DAY_TIME_DURATION = 
                
        new (class extends DefaultStaticProperty<AspectWithDurationTypeForRangeConstraints, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithDurationTypeForRangeConstraints';
    }


                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultMeasurement = new DefaultMeasurement(null, 
null, 
null, 
(() => { const defaultUnit = new DefaultUnit(null, 
null, 
null, 
'h','HUR','secondUnitOfTime','3600 s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour';
defaultUnit.addPreferredName('en' , 'hour');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#dayTimeDuration" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDayTimeDuration';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dayTimeDuration" ),'P1DT5H'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dayTimeDuration" ),'P1DT8H'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithDurationMinDurationMaxDayTimeDuration';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithDayTimeDuration',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_DURATION = 
                
        new (class extends DefaultStaticProperty<AspectWithDurationTypeForRangeConstraints, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithDurationTypeForRangeConstraints';
    }


                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultMeasurement = new DefaultMeasurement(null, 
null, 
null, 
(() => { const defaultUnit = new DefaultUnit(null, 
null, 
null, 
'h','HUR','secondUnitOfTime','3600 s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour';
defaultUnit.addPreferredName('en' , 'hour');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#duration" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDuration';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#duration" ),'PT1H5M0S'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#duration" ),'PT1H5M3S'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithDurationMinDurationMaxDuration';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithDuration',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_YEAR_MONTH_DURATION = 
                
        new (class extends DefaultStaticProperty<AspectWithDurationTypeForRangeConstraints, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithDurationTypeForRangeConstraints';
    }


                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultMeasurement = new DefaultMeasurement(null, 
null, 
null, 
(() => { const defaultUnit = new DefaultUnit(null, 
null, 
null, 
'h','HUR','secondUnitOfTime','3600 s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour';
defaultUnit.addPreferredName('en' , 'hour');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#yearMonthDuration" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementYearMonthDuration';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#yearMonthDuration" ),'P5Y2M'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#yearMonthDuration" ),'P5Y3M'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithDurationMinDurationMaxYearMonthDuration';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithYearMonthDuration',
    false,
    );




getModelClass(): string {
return 'AspectWithDurationTypeForRangeConstraints';
}

getAspectModelUrn(): string {
return MetaAspectWithDurationTypeForRangeConstraints .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithDurationTypeForRangeConstraints';
}

                        getProperties(): Array<StaticProperty<AspectWithDurationTypeForRangeConstraints, any>> {
return [MetaAspectWithDurationTypeForRangeConstraints.TEST_PROPERTY_WITH_DAY_TIME_DURATION, MetaAspectWithDurationTypeForRangeConstraints.TEST_PROPERTY_WITH_DURATION, MetaAspectWithDurationTypeForRangeConstraints.TEST_PROPERTY_WITH_YEAR_MONTH_DURATION];
}

getAllProperties(): Array<StaticProperty<AspectWithDurationTypeForRangeConstraints, any>> {
    return this.getProperties();
}




    }


