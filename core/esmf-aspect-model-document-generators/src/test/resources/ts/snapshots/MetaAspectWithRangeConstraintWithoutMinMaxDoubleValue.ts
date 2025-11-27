













import { AspectWithRangeConstraintWithoutMinMaxDoubleValue,} from './AspectWithRangeConstraintWithoutMinMaxDoubleValue';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithoutMinMaxDoubleValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithRangeConstraintWithoutMinMaxDoubleValue',
    'doubleProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestRangeConstraint',
'TestRangeConstraint',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'DoubleCharacteristic',
'DoubleCharacteristic',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'DoubleCharacteristic';
defaultCharacteristic.addPreferredName('de' , 'Numerische Charakteristik');
defaultCharacteristic.addPreferredName('en' , 'Double Characteristic');
defaultCharacteristic.addDescription('de' , 'Positive Zahlen');
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
            {value: 'Test Aspekt', language: 'de'},
        ];
        }



    }


