import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithUpwardTransitionInNestedEntity,} from './AspectWithUpwardTransitionInNestedEntity';
import { DefaultEntity,DefaultList,} from './esmf/aspect-meta-model';

import { KnownVersion,} from './esmf/shared/known-version';

import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithUpwardTransitionInNestedEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUpwardTransitionInNestedEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithUpwardTransitionInNestedEntity implements StaticMetaClass<AspectWithUpwardTransitionInNestedEntity>, PropertyContainer<AspectWithUpwardTransitionInNestedEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUpwardTransitionInNestedEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUpwardTransitionInNestedEntity();


 public static readonly  FIRST_LEVEL_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithUpwardTransitionInNestedEntity, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithUpwardTransitionInNestedEntity';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

    getValue( object : AspectWithUpwardTransitionInNestedEntity) : ReplacedAspectArtifact[] {
        return object.firstLevelProperty;
    }

        setValue( object : AspectWithUpwardTransitionInNestedEntity, value : ReplacedAspectArtifact[] ) {
            object.firstLevelProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithUpwardTransitionInNestedEntity',
    'firstLevelProperty',
    (() => { const defaultList = new DefaultList(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'FirstLevelCharacteristics',
'FirstLevelCharacteristics',
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultList.addAspectModelUrn = this.NAMESPACE + 'FirstLevelCharacteristics';
 return defaultList; })()
,
    false,
    false,
    undefined,
        'firstLevelProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithUpwardTransitionInNestedEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithUpwardTransitionInNestedEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
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


