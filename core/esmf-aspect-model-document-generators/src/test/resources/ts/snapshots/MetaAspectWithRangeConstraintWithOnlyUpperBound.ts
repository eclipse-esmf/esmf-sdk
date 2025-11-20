













import { AspectWithRangeConstraintWithOnlyUpperBound,} from './AspectWithRangeConstraintWithOnlyUpperBound';
import { BoundDefinition,} from './aspect-meta-model/bound-definition';
import { DefaultMeasurement,DefaultQuantityKind,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { DefaultUnit,} from './aspect-meta-model/default-unit';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithRangeConstraintWithOnlyUpperBound (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithOnlyUpperBound).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithOnlyUpperBound implements StaticMetaClass<AspectWithRangeConstraintWithOnlyUpperBound>, PropertyContainer<AspectWithRangeConstraintWithOnlyUpperBound> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyUpperBound';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithOnlyUpperBound();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithOnlyUpperBound, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithOnlyUpperBound';
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
 return defaultUnit; })(),new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultMeasurement.addAspectModelUrn = this.NAMESPACE + 'Measurement';
 return defaultMeasurement; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.AT_MOST,BoundDefinition.OPEN,undefined,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'2.3'),)
defaultRangeConstraint.isAnonymousNode = true;
defaultRangeConstraint.addPreferredName('en' , 'Test Range Constraint');
defaultRangeConstraint.addDescription('en' , 'This is a test range constraint.');
defaultRangeConstraint.addSeeReference('http:\/\/example.com\/');
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestRangeConstraint';
 return trait; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'2.0'),
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithRangeConstraintWithOnlyUpperBound';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithOnlyUpperBound .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithOnlyUpperBound';
}

                        getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyUpperBound, any>> {
return [MetaAspectWithRangeConstraintWithOnlyUpperBound.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyUpperBound, any>> {
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


