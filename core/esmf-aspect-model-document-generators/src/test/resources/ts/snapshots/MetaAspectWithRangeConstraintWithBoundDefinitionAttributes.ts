













import { AspectWithRangeConstraintWithBoundDefinitionAttributes,} from './AspectWithRangeConstraintWithBoundDefinitionAttributes';
import { BoundDefinition,} from './aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithRangeConstraintWithBoundDefinitionAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithBoundDefinitionAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithBoundDefinitionAttributes implements StaticMetaClass<AspectWithRangeConstraintWithBoundDefinitionAttributes>, PropertyContainer<AspectWithRangeConstraintWithBoundDefinitionAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithBoundDefinitionAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithBoundDefinitionAttributes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithBoundDefinitionAttributes, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithBoundDefinitionAttributes';
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
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.LESS_THAN,BoundDefinition.GREATER_THAN,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),'5'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),'10'),)
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
return 'AspectWithRangeConstraintWithBoundDefinitionAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithBoundDefinitionAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithBoundDefinitionAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithBoundDefinitionAttributes, any>> {
return [MetaAspectWithRangeConstraintWithBoundDefinitionAttributes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithBoundDefinitionAttributes, any>> {
    return this.getProperties();
}




    }


