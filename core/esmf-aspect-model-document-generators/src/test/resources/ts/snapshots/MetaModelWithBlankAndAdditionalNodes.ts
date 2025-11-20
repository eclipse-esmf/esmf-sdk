













import { BoundDefinition,} from './aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultList,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { ModelWithBlankAndAdditionalNodes,} from './ModelWithBlankAndAdditionalNodes';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaModelWithBlankAndAdditionalNodes (urn:samm:org.eclipse.esmf.test:1.0.0#ModelWithBlankAndAdditionalNodes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaModelWithBlankAndAdditionalNodes implements StaticMetaClass<ModelWithBlankAndAdditionalNodes>, PropertyContainer<ModelWithBlankAndAdditionalNodes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'ModelWithBlankAndAdditionalNodes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaModelWithBlankAndAdditionalNodes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<ModelWithBlankAndAdditionalNodes, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'ModelWithBlankAndAdditionalNodes';
    }

        getContainedType(): string {
            return 'ModelWithBlankAndAdditionalNodes';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultList = new DefaultList(null, 
null, 
null, 
(() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'Number';
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),'5'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),'10'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.isAnonymousNode = true;
 return trait; })(),
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultList.addAspectModelUrn = this.NAMESPACE + 'NumberList';
 return defaultList; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'ModelWithBlankAndAdditionalNodes';
}

getAspectModelUrn(): string {
return MetaModelWithBlankAndAdditionalNodes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'ModelWithBlankAndAdditionalNodes';
}

                        getProperties(): Array<StaticProperty<ModelWithBlankAndAdditionalNodes, any>> {
return [MetaModelWithBlankAndAdditionalNodes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<ModelWithBlankAndAdditionalNodes, any>> {
    return this.getProperties();
}




    }


