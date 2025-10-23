import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntityCollections,} from './AspectWithMultipleEntityCollections';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithMultipleEntityCollections (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntityCollections).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleEntityCollections implements StaticMetaClass<AspectWithMultipleEntityCollections>, PropertyContainer<AspectWithMultipleEntityCollections> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntityCollections';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntityCollections();


 public static readonly  TEST_LIST_ONE = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleEntityCollections, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntityCollections';
    }

        getContainedType(): AspectWithMultipleEntityCollections {
            return 'AspectWithMultipleEntityCollections';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testListOne',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultList({
urn : this.NAMESPACE + 'EntityList',
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
        payloadName : 'testListOne',
    isAbstract : false,
    });




 public static readonly  TEST_LIST_TWO = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleEntityCollections, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntityCollections';
    }

        getContainedType(): AspectWithMultipleEntityCollections {
            return 'AspectWithMultipleEntityCollections';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testListTwo',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultList({
urn : this.NAMESPACE + 'EntityList',
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
        payloadName : 'testListTwo',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithMultipleEntityCollections';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntityCollections .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntityCollections';
}

                        getProperties(): Array<StaticProperty<AspectWithMultipleEntityCollections, any>> {
return [MetaAspectWithMultipleEntityCollections.TEST_LIST_ONE, MetaAspectWithMultipleEntityCollections.TEST_LIST_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntityCollections, any>> {
    return this.getProperties();
}




    }


