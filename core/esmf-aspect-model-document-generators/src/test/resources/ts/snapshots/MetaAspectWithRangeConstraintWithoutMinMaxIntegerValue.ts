













import { AspectWithRangeConstraintWithoutMinMaxIntegerValue,} from './AspectWithRangeConstraintWithoutMinMaxIntegerValue';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';


    

/*
* Generated class MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithoutMinMaxIntegerValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithRangeConstraintWithoutMinMaxIntegerValue) : string {
        return object.testInt;
    }

        setValue( object : AspectWithRangeConstraintWithoutMinMaxIntegerValue, value : string ) {
            object.testInt = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithRangeConstraintWithoutMinMaxIntegerValue',
    'testInt',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestRangeConstraint',
'TestRangeConstraint',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'IntegerCharacteristic',
'IntegerCharacteristic',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'IntegerCharacteristic';
defaultCharacteristic.addPreferredName('de' , 'Numerische Charakteristik');
defaultCharacteristic.addPreferredName('en' , 'Integer Characteristic');
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
return KnownVersion.getLatest()
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

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
            {value: 'Test Aspekt', language: 'de'},
        ];
        }



    }


