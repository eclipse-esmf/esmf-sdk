import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithCollectionAndElementCharacteristic,} from './AspectWithCollectionAndElementCharacteristic';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithCollectionAndElementCharacteristic (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionAndElementCharacteristic).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionAndElementCharacteristic implements StaticMetaClass<AspectWithCollectionAndElementCharacteristic>, PropertyContainer<AspectWithCollectionAndElementCharacteristic> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionAndElementCharacteristic';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCollectionAndElementCharacteristic();


 public static readonly  ITEMS = 
                
        new (class extends StaticContainerProperty<AspectWithCollectionAndElementCharacteristic, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithCollectionAndElementCharacteristic';
    }

        getContainedType(): AspectWithCollectionAndElementCharacteristic {
            return 'AspectWithCollectionAndElementCharacteristic';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'items',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultCollection({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty())),Optional.of(new DefaultSingleEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
}, DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty()))))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'items',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCollectionAndElementCharacteristic';
}

getAspectModelUrn(): string {
return MetaAspectWithCollectionAndElementCharacteristic .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCollectionAndElementCharacteristic';
}

                        getProperties(): Array<StaticProperty<AspectWithCollectionAndElementCharacteristic, any>> {
return [MetaAspectWithCollectionAndElementCharacteristic.ITEMS];
}

getAllProperties(): Array<StaticProperty<AspectWithCollectionAndElementCharacteristic, any>> {
    return this.getProperties();
}




    }


