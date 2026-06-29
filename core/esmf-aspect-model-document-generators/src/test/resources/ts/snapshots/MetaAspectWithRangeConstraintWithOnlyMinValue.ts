













import { AspectWithRangeConstraintWithOnlyMinValue,} from './AspectWithRangeConstraintWithOnlyMinValue';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithRangeConstraintWithOnlyMinValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithOnlyMinValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithRangeConstraintWithOnlyMinValue implements StaticMetaClass<AspectWithRangeConstraintWithOnlyMinValue>, PropertyContainer<AspectWithRangeConstraintWithOnlyMinValue> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyMinValue';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithOnlyMinValue();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithOnlyMinValue, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithOnlyMinValue';
    }


    getValue( object : AspectWithRangeConstraintWithOnlyMinValue) : string {
        return object.testProperty;
    }

        setValue( object : AspectWithRangeConstraintWithOnlyMinValue, value : string ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyMinValue',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestTrait',
'TestTrait',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.OPEN,BoundDefinition.GREATER_THAN,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),'5'),undefined,)
defaultRangeConstraint.isAnonymousNode = true;
defaultRangeConstraint.addPreferredName('en' , 'Test Constraint');
defaultRangeConstraint.addDescription('en' , 'Test Constraint');
defaultRangeConstraint.addSeeReference('http:\/\/example.com\/');
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestTrait';
 return trait; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithRangeConstraintWithOnlyMinValue';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithOnlyMinValue .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithOnlyMinValue';
}

getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyMinValue, any>> {
return [MetaAspectWithRangeConstraintWithOnlyMinValue.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyMinValue, any>> {
        return this.getProperties();
}




    }


