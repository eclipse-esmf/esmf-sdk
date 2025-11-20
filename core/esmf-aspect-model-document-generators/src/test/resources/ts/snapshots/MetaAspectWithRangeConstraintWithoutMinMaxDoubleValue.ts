













import { AspectWithRangeConstraintWithoutMinMaxDoubleValue,} from './AspectWithRangeConstraintWithoutMinMaxDoubleValue';
import { BoundDefinition,} from './aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithoutMinMaxDoubleValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue implements StaticMetaClass<AspectWithRangeConstraintWithoutMinMaxDoubleValue>, PropertyContainer<AspectWithRangeConstraintWithoutMinMaxDoubleValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithoutMinMaxDoubleValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue();


 public static readonly  DOUBLE_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithoutMinMaxDoubleValue, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithoutMinMaxDoubleValue';
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
new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'DoubleCharacteristic';
defaultCharacteristic.addPreferredName('de' , 'Numerische Charakteristik');
defaultCharacteristic.addPreferredName('en' , 'Double Characteristic');
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
        'doubleProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithRangeConstraintWithoutMinMaxDoubleValue';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithoutMinMaxDoubleValue';
}

                        getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithoutMinMaxDoubleValue, any>> {
return [MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue.DOUBLE_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithoutMinMaxDoubleValue, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Test Aspect', 'en'),
            new LangString('Test Aspekt', 'de'),
        ];
        }



    }


