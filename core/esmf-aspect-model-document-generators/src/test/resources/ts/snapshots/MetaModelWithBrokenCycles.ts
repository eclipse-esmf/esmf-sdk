import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';















import { DefaultCharacteristic,DefaultEntity,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';

import { Either,} from './core/Either';



import { ModelWithBrokenCycles,} from './ModelWithBrokenCycles';



    

/*
* Generated class MetaModelWithBrokenCycles (urn:samm:org.eclipse.esmf.test:1.0.0#ModelWithBrokenCycles).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

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

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
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

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
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

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


