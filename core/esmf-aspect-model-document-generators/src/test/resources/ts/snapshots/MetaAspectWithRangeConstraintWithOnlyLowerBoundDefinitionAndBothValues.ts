













import { AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues,} from './AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues implements StaticMetaClass<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues>, PropertyContainer<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';
    }


    getValue( object : AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues) : string {
        return object.testProperty;
    }

        setValue( object : AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues, value : string ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues',
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
BoundDefinition.AT_MOST,BoundDefinition.GREATER_THAN,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),'5'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),'10'),)
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
return 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues';
}

getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues, any>> {
return [MetaAspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyLowerBoundDefinitionAndBothValues, any>> {
        return this.getProperties();
}




    }


