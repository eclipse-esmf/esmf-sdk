













import { AspectWithDurationTypeForRangeConstraints,} from './AspectWithDurationTypeForRangeConstraints';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithDurationTypeForRangeConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDurationTypeForRangeConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithDurationTypeForRangeConstraints) : any {
        return object.testPropertyWithDayTimeDuration;
    }

        setValue( object : AspectWithDurationTypeForRangeConstraints, value : any ) {
            object.testPropertyWithDayTimeDuration = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithDurationTypeForRangeConstraints',
    'testPropertyWithDayTimeDuration',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithDurationMinDurationMaxDayTimeDuration',
'testWithDurationMinDurationMaxDayTimeDuration',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementDayTimeDuration',
'MeasurementDayTimeDuration',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour',
'hour',
'h','HUR','secondUnitOfTime','3600 s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#time',
'time',
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour';
defaultUnit.addPreferredName('en' , 'hour');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#dayTimeDuration" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDayTimeDuration';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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


    getValue( object : AspectWithDurationTypeForRangeConstraints) : any {
        return object.testPropertyWithDuration;
    }

        setValue( object : AspectWithDurationTypeForRangeConstraints, value : any ) {
            object.testPropertyWithDuration = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithDurationTypeForRangeConstraints',
    'testPropertyWithDuration',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithDurationMinDurationMaxDuration',
'testWithDurationMinDurationMaxDuration',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementDuration',
'MeasurementDuration',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour',
'hour',
'h','HUR','secondUnitOfTime','3600 s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#time',
'time',
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour';
defaultUnit.addPreferredName('en' , 'hour');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#duration" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDuration';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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


    getValue( object : AspectWithDurationTypeForRangeConstraints) : any {
        return object.testPropertyWithYearMonthDuration;
    }

        setValue( object : AspectWithDurationTypeForRangeConstraints, value : any ) {
            object.testPropertyWithYearMonthDuration = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithDurationTypeForRangeConstraints',
    'testPropertyWithYearMonthDuration',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithDurationMinDurationMaxYearMonthDuration',
'testWithDurationMinDurationMaxYearMonthDuration',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementYearMonthDuration',
'MeasurementYearMonthDuration',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour',
'hour',
'h','HUR','secondUnitOfTime','3600 s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#time',
'time',
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#hour';
defaultUnit.addPreferredName('en' , 'hour');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#yearMonthDuration" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementYearMonthDuration';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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


