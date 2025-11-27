













import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultList,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { KnownVersion,} from './esmf/shared/known-version';
import { ModelWithBlankAndAdditionalNodes,} from './ModelWithBlankAndAdditionalNodes';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaModelWithBlankAndAdditionalNodes (urn:samm:org.eclipse.esmf.test:1.0.0#ModelWithBlankAndAdditionalNodes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaModelWithBlankAndAdditionalNodes implements StaticMetaClass<ModelWithBlankAndAdditionalNodes>, PropertyContainer<ModelWithBlankAndAdditionalNodes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'ModelWithBlankAndAdditionalNodes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaModelWithBlankAndAdditionalNodes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<ModelWithBlankAndAdditionalNodes, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'ModelWithBlankAndAdditionalNodes';
    }

        getContainedType(): string {
            return 'ModelWithBlankAndAdditionalNodes';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'ModelWithBlankAndAdditionalNodes',
    'testProperty',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'NumberList',
'NumberList',
(() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Number',
'Number',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'Number';
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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


