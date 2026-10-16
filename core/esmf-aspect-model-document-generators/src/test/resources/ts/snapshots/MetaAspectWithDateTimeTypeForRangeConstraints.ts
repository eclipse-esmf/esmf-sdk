













import { AspectWithDateTimeTypeForRangeConstraints,} from './AspectWithDateTimeTypeForRangeConstraints';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { DefaultUnit,} from './esmf/aspect-meta-model/default-unit';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithDateTimeTypeForRangeConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDateTimeTypeForRangeConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithDateTimeTypeForRangeConstraints implements StaticMetaClass<AspectWithDateTimeTypeForRangeConstraints>, PropertyContainer<AspectWithDateTimeTypeForRangeConstraints> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDateTimeTypeForRangeConstraints';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDateTimeTypeForRangeConstraints();


 public static readonly  TEST_PROPERTY_WITH_DATE_TIME = 
                
        new (class extends DefaultStaticProperty<AspectWithDateTimeTypeForRangeConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithDateTimeTypeForRangeConstraints';
    }


    getValue( object : AspectWithDateTimeTypeForRangeConstraints) : string {
        return object.testPropertyWithDateTime;
    }

        setValue( object : AspectWithDateTimeTypeForRangeConstraints, value : string ) {
            object.testPropertyWithDateTime = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithDateTimeTypeForRangeConstraints',
    'testPropertyWithDateTime',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithGregorianCalenderMinGregorianCalenderMaxDateTime',
'testWithGregorianCalenderMinGregorianCalenderMaxDateTime',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementDateTime',
'MeasurementDateTime',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#secondUnitOfTime',
'secondUnitOfTime',
's','SEC',undefined,'s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#halfLife',
'halfLife',
'half life')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reverberationTime',
'reverberationTime',
'reverberation time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#meanLife',
'meanLife',
'mean life')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#timeConstant',
'timeConstant',
'time constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#carrierLifeTime',
'carrierLifeTime',
'carrier life time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#periodicTime',
'periodicTime',
'periodic time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#period',
'period',
'period')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reactorTimeConstant',
'reactorTimeConstant',
'reactor time constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#time',
'time',
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relaxationTime',
'relaxationTime',
'relaxation time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#secondUnitOfTime';
defaultUnit.addPreferredName('en' , 'second [unit of time]');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDateTime';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ),new Date( '2000-01-01T14:23:00' )),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ),new Date( '2000-01-02T15:23:00' )),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithGregorianCalenderMinGregorianCalenderMaxDateTime';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithDateTime',
    false,
    );




 public static readonly  TEST_PROPERTY_WITH_DATE_TIME_STAMP = 
                
        new (class extends DefaultStaticProperty<AspectWithDateTimeTypeForRangeConstraints, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithDateTimeTypeForRangeConstraints';
    }


    getValue( object : AspectWithDateTimeTypeForRangeConstraints) : string {
        return object.testPropertyWithDateTimeStamp;
    }

        setValue( object : AspectWithDateTimeTypeForRangeConstraints, value : string ) {
            object.testPropertyWithDateTimeStamp = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithDateTimeTypeForRangeConstraints',
    'testPropertyWithDateTimeStamp',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'testWithGregorianCalenderMinGregorianCalenderMaxDateTimeStamp',
'testWithGregorianCalenderMinGregorianCalenderMaxDateTimeStamp',
(() => { const defaultMeasurement = new DefaultMeasurement(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'MeasurementDateTimeStamp',
'MeasurementDateTimeStamp',
(() => { const defaultUnit = new DefaultUnit(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#secondUnitOfTime',
'secondUnitOfTime',
's','SEC',undefined,'s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#halfLife',
'halfLife',
'half life')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reverberationTime',
'reverberationTime',
'reverberation time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#meanLife',
'meanLife',
'mean life')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#timeConstant',
'timeConstant',
'time constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#carrierLifeTime',
'carrierLifeTime',
'carrier life time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#periodicTime',
'periodicTime',
'periodic time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#period',
'period',
'period')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#reactorTimeConstant',
'reactorTimeConstant',
'reactor time constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#time',
'time',
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(KnownVersion.getLatest().toString(),
'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#relaxationTime',
'relaxationTime',
'relaxation time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#secondUnitOfTime';
defaultUnit.addPreferredName('en' , 'second [unit of time]');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTimeStamp" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDateTimeStamp';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTimeStamp" ),new Date( '2000-01-01T14:23:00.66372+14:00' )),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTimeStamp" ),new Date( '2000-01-01T15:23:00.66372+14:00' )),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'testWithGregorianCalenderMinGregorianCalenderMaxDateTimeStamp';
trait.addPreferredName('en' , 'Test Range');
trait.addDescription('en' , 'Test Range');
 return trait; })()
,
    false,
    false,
    undefined,
        'testPropertyWithDateTimeStamp',
    false,
    );




getModelClass(): string {
return 'AspectWithDateTimeTypeForRangeConstraints';
}

getAspectModelUrn(): string {
return MetaAspectWithDateTimeTypeForRangeConstraints .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithDateTimeTypeForRangeConstraints';
}

getProperties(): Array<StaticProperty<AspectWithDateTimeTypeForRangeConstraints, any>> {
return [MetaAspectWithDateTimeTypeForRangeConstraints.TEST_PROPERTY_WITH_DATE_TIME, MetaAspectWithDateTimeTypeForRangeConstraints.TEST_PROPERTY_WITH_DATE_TIME_STAMP];
}

getAllProperties(): Array<StaticProperty<AspectWithDateTimeTypeForRangeConstraints, any>> {
        return this.getProperties();
}




    }


