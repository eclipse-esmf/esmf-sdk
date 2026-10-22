













import { AspectWithConstrainedCollection,} from './AspectWithConstrainedCollection';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultList,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithConstrainedCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstrainedCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithConstrainedCollection implements StaticMetaClass<AspectWithConstrainedCollection>, PropertyContainer<AspectWithConstrainedCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstrainedCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstrainedCollection();


 public static readonly  TEST_COLLECTION = 
                
        new (class extends StaticContainerProperty<AspectWithConstrainedCollection, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstrainedCollection';
    }

        getContainedType(): string {
            return 'string';
        }

    getValue( object : AspectWithConstrainedCollection) : string[] {
        return object.testCollection;
    }

        setValue( object : AspectWithConstrainedCollection, value : string[] ) {
            object.testCollection = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstrainedCollection',
    'testCollection',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'IntegerRange',
'IntegerRange',
(() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultList.isAnonymousNode = true;
 return defaultList; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.AT_MOST,BoundDefinition.AT_LEAST,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'2'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'10'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'IntegerRange';
 return trait; })()
,
    false,
    false,
    undefined,
        'testCollection',
    false,
    );




getModelClass(): string {
return 'AspectWithConstrainedCollection';
}

getAspectModelUrn(): string {
return MetaAspectWithConstrainedCollection .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithConstrainedCollection';
}

getProperties(): Array<StaticProperty<AspectWithConstrainedCollection, any>> {
return [MetaAspectWithConstrainedCollection.TEST_COLLECTION];
}

getAllProperties(): Array<StaticProperty<AspectWithConstrainedCollection, any>> {
        return this.getProperties();
}




    }


