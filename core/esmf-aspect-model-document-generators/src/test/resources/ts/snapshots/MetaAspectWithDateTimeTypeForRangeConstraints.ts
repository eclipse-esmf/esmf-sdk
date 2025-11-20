













import { AspectWithDateTimeTypeForRangeConstraints,} from './AspectWithDateTimeTypeForRangeConstraints';
import { BoundDefinition,} from './aspect-meta-model/bound-definition';
import { DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { DefaultUnit,} from './aspect-meta-model/default-unit';


    

/*
* Generated class MetaAspectWithDateTimeTypeForRangeConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDateTimeTypeForRangeConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDateTimeTypeForRangeConstraints implements StaticMetaClass<AspectWithDateTimeTypeForRangeConstraints>, PropertyContainer<AspectWithDateTimeTypeForRangeConstraints> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDateTimeTypeForRangeConstraints';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDateTimeTypeForRangeConstraints();


 public static readonly  TEST_PROPERTY_WITH_DATE_TIME = 
                
        new (class extends DefaultStaticProperty<AspectWithDateTimeTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithDateTimeTypeForRangeConstraints';
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
's','SEC',undefined,'s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'half life')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'time constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'relaxation time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'periodic time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'reactor time constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'period')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'carrier life time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'mean life')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'reverberation time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#secondUnitOfTime';
defaultUnit.addPreferredName('en' , 'second [unit of time]');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDateTime';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ),new Date( ''2000-01-01T14:23:00'' )),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTime" ),new Date( ''2000-01-02T15:23:00'' )),)
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
                
        new (class extends DefaultStaticProperty<AspectWithDateTimeTypeForRangeConstraints, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithDateTimeTypeForRangeConstraints';
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
's','SEC',undefined,'s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'half life')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'time constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'relaxation time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'periodic time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'reactor time constant')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'period')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'carrier life time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'mean life')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'reverberation time')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#secondUnitOfTime';
defaultUnit.addPreferredName('en' , 'second [unit of time]');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTimeStamp" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'MeasurementDateTimeStamp';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTimeStamp" ),new Date( ''2000-01-01T14:23:00.66372+14:00'' )),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#dateTimeStamp" ),new Date( ''2000-01-01T15:23:00.66372+14:00'' )),)
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
return KnownVersionUtils.getLatest()
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


