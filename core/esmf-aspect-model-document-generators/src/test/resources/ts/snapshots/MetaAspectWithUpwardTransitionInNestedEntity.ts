import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithUpwardTransitionInNestedEntity,} from './AspectWithUpwardTransitionInNestedEntity';

import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithUpwardTransitionInNestedEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUpwardTransitionInNestedEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithUpwardTransitionInNestedEntity implements StaticMetaClass<AspectWithUpwardTransitionInNestedEntity>, PropertyContainer<AspectWithUpwardTransitionInNestedEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUpwardTransitionInNestedEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUpwardTransitionInNestedEntity();


 public static readonly  FIRST_LEVEL_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithUpwardTransitionInNestedEntity, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithUpwardTransitionInNestedEntity';
    }

        getContainedType(): AspectWithUpwardTransitionInNestedEntity {
            return 'AspectWithUpwardTransitionInNestedEntity';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'firstLevelProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultList({
urn : this.NAMESPACE + 'FirstLevelCharacteristics',
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
        payloadName : 'firstLevelProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithUpwardTransitionInNestedEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithUpwardTransitionInNestedEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithUpwardTransitionInNestedEntity';
}

                        getProperties(): Array<StaticProperty<AspectWithUpwardTransitionInNestedEntity, any>> {
return [MetaAspectWithUpwardTransitionInNestedEntity.FIRST_LEVEL_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUpwardTransitionInNestedEntity, any>> {
    return this.getProperties();
}




    }


