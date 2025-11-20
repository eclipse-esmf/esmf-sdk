













import { AspectWithConstrainedCollection,} from './AspectWithConstrainedCollection';
import { BoundDefinition,} from './aspect-meta-model/bound-definition';
import { DefaultList,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithConstrainedCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstrainedCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstrainedCollection implements StaticMetaClass<AspectWithConstrainedCollection>, PropertyContainer<AspectWithConstrainedCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstrainedCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstrainedCollection();


 public static readonly  TEST_COLLECTION = 
                
        new (class extends StaticContainerProperty<AspectWithConstrainedCollection, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithConstrainedCollection';
    }

        getContainedType(): string {
            return 'AspectWithConstrainedCollection';
        }

                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultList = new DefaultList(null, 
null, 
null, 
undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultList.isAnonymousNode = true;
 return defaultList; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


