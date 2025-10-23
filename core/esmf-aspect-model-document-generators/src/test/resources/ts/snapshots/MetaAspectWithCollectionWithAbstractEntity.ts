import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';















import { AspectWithCollectionWithAbstractEntity,} from './AspectWithCollectionWithAbstractEntity';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCollectionWithAbstractEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithAbstractEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionWithAbstractEntity implements StaticMetaClass<AspectWithCollectionWithAbstractEntity>, PropertyContainer<AspectWithCollectionWithAbstractEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithAbstractEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionWithAbstractEntity();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionWithAbstractEntity, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionWithAbstractEntity';
    }

        getContainedType(): AspectWithCollectionWithAbstractEntity {
            return 'AspectWithCollectionWithAbstractEntity';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [ {
value : "This is a test property",
languageTag : 'en',
},
 ],
see : [  ],
},
    characteristic :     new DefaultCollection({
urn : this.NAMESPACE + 'EntityCollectionCharacteristic',
preferredNames : [  ],
descriptions : [ {
value : "This is an entity collection characteristic",
languageTag : 'en',
},
 ],
see : [  ],
},Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [ {
value : "This is an abstract test entity",
languageTag : 'en',
},
 ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty(),List.of(AspectModelUrn.fromUrn("urn:samm:org.eclipse.esmf.test:1.0.0#ReplacedAspectArtifact")))),Optional.empty())
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCollectionWithAbstractEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionWithAbstractEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollectionWithAbstractEntity';
}

                        getProperties(): Array<StaticProperty<AspectWithCollectionWithAbstractEntity, any>> {
return [MetaAspectWithCollectionWithAbstractEntity.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionWithAbstractEntity, any>> {
    return this.getProperties();
}




    }


