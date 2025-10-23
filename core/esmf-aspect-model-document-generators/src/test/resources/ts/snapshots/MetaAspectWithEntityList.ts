import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityList,} from './AspectWithEntityList';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithEntityList (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityList).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityList implements StaticMetaClass<AspectWithEntityList>, PropertyContainer<AspectWithEntityList> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityList';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityList();


 public static readonly  TEST_LIST = 
                
        new (class extends StaticContainerProperty<AspectWithEntityList, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithEntityList';
    }

        getContainedType(): AspectWithEntityList {
            return 'AspectWithEntityList';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testList',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultList({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty())),Optional.empty())
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testList',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEntityList';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityList .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityList';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityList, any>> {
return [MetaAspectWithEntityList.TEST_LIST];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityList, any>> {
    return this.getProperties();
}




    }


