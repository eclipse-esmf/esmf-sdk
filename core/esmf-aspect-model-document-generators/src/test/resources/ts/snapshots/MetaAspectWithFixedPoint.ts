













import { AspectWithFixedPoint,} from './AspectWithFixedPoint';
import { DefaultFixedPointConstraint,DefaultMeasurement,DefaultQuantityKind,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithFixedPoint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithFixedPoint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithFixedPoint implements StaticMetaClass<AspectWithFixedPoint>, PropertyContainer<AspectWithFixedPoint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithFixedPoint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithFixedPoint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithFixedPoint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithFixedPoint';
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
'm\/s','MTS',undefined,'m\/s',[ (() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'phase speed of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'phase velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'phase velocity of electromagnetic waves')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'group velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'velocity (speed) on propagation of electromagnetic waves in vacuo')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'(instantaneous) sound particle velocity')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'velocity of sound (phase velocity)')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })(),
(() => { const defaultQuantityKind = new DefaultQuantityKind(null, 
null, 
null, 
'neutron speed')
defaultQuantityKind.isAnonymousNode = true;
 return defaultQuantityKind; })() ])
defaultUnit.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:unit:2.2.0#metrePerSecond';
defaultUnit.addPreferredName('en' , 'metre per second');
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'Measurement';
 return defaultMeasurement; })(),[(() => { const defaultFixedPointConstraint = new DefaultFixedPointConstraint(null, 
null, 
null, 
5,
3)
defaultFixedPointConstraint.isAnonymousNode = true;
defaultFixedPointConstraint.addPreferredName('en' , 'Test Fixed Point');
defaultFixedPointConstraint.addDescription('en' , 'This is a test fixed point constraint.');
defaultFixedPointConstraint.addSeeReference('http:\/\/example.com\/');
 return defaultFixedPointConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestFixedPoint';
 return trait; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithFixedPoint';
}

getAspectModelUrn(): string {
return MetaAspectWithFixedPoint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithFixedPoint';
}

                        getProperties(): Array<StaticProperty<AspectWithFixedPoint, any>> {
return [MetaAspectWithFixedPoint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithFixedPoint, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Test Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('This is a test description', 'en'),
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


