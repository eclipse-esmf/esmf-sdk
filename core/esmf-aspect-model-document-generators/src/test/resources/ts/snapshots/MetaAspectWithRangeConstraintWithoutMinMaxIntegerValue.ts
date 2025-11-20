













import { AspectWithRangeConstraintWithoutMinMaxIntegerValue,} from './AspectWithRangeConstraintWithoutMinMaxIntegerValue';
import { BoundDefinition,} from './aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithoutMinMaxIntegerValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue implements StaticMetaClass<AspectWithRangeConstraintWithoutMinMaxIntegerValue>, PropertyContainer<AspectWithRangeConstraintWithoutMinMaxIntegerValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithoutMinMaxIntegerValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue();


 public static readonly  TEST_INT = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithoutMinMaxIntegerValue, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithoutMinMaxIntegerValue';
    }


                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'IntegerCharacteristic';
defaultCharacteristic.addPreferredName('de' , 'Numerische Charakteristik');
defaultCharacteristic.addPreferredName('en' , 'Integer Characteristic');
defaultCharacteristic.addDescription('de' , 'Positive Zahlen');
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.OPEN,BoundDefinition.OPEN,undefined,undefined,)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestRangeConstraint';
trait.addPreferredName('de' , 'Test Range Constraint');
trait.addPreferredName('en' , 'Test Range Constraint');
trait.addDescription('de' , 'Beschränkt einen numerischen Wert auf Werte zwischen 0 und 100.');
trait.addDescription('en' , 'Restricts a numeric value to values between 0 and 100.');
 return trait; })()
,
    false,
    false,
    undefined,
        'testInt',
    false,
    );




getModelClass(): string {
return 'AspectWithRangeConstraintWithoutMinMaxIntegerValue';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithoutMinMaxIntegerValue';
}

                        getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithoutMinMaxIntegerValue, any>> {
return [MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue.TEST_INT];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithoutMinMaxIntegerValue, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Test Aspect', 'en'),
            new LangString('Test Aspekt', 'de'),
        ];
        }



    }


