import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';















import { DefaultCharacteristic,DefaultEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';

import { Either,} from './esmf/aspect-meta-model/Either';
import { KnownVersion,} from './esmf/shared/known-version';



import { ModelWithBrokenCycles,} from './ModelWithBrokenCycles';



    

/*
* Generated class MetaModelWithBrokenCycles (urn:samm:org.eclipse.esmf.test:1.0.0#ModelWithBrokenCycles).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaModelWithBrokenCycles implements StaticMetaClass<ModelWithBrokenCycles>, PropertyContainer<ModelWithBrokenCycles> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'ModelWithBrokenCycles';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaModelWithBrokenCycles();


 public static readonly  A = 
                
        new (class extends DefaultStaticProperty<ModelWithBrokenCycles, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'ModelWithBrokenCycles';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'ModelWithBrokenCycles',
    'a',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'aCharacteristic',
'aCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'aCharacteristic';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'a',
    false,
    );




 public static readonly  E = 
                
        new (class extends DefaultStaticProperty<ModelWithBrokenCycles, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'ModelWithBrokenCycles';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'ModelWithBrokenCycles',
    'e',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'eCharacteristic',
'eCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultEntityReplacedAspectArtifact; })())
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'eCharacteristic';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'e',
    false,
    );




 public static readonly  H = 
                
        new (class extends DefaultStaticProperty<ModelWithBrokenCycles, Either<ReplacedAspectArtifact, ReplacedAspectArtifact>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'ModelWithBrokenCycles';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'ModelWithBrokenCycles',
    'h',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'hCharacteristic',
'hCharacteristic',
undefined)
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'hCharacteristic';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'h',
    false,
    );




getModelClass(): string {
return 'ModelWithBrokenCycles';
}

getAspectModelUrn(): string {
return MetaModelWithBrokenCycles .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'ModelWithBrokenCycles';
}

getProperties(): Array<StaticProperty<ModelWithBrokenCycles, any>> {
return [MetaModelWithBrokenCycles.A, MetaModelWithBrokenCycles.E, MetaModelWithBrokenCycles.H];
}

getAllProperties(): Array<StaticProperty<ModelWithBrokenCycles, any>> {
        return this.getProperties();
}




    }


