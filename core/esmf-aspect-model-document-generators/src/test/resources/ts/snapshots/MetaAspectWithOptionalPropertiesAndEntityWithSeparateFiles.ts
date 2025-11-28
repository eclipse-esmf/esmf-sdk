import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithOptionalPropertiesAndEntityWithSeparateFiles,} from './AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
import { DefaultEntity,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';

import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';

import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertiesAndEntityWithSeparateFiles).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles implements StaticMetaClass<AspectWithOptionalPropertiesAndEntityWithSeparateFiles>, PropertyContainer<AspectWithOptionalPropertiesAndEntityWithSeparateFiles> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles();


 public static readonly  PRODUCTION_PERIOD = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertiesAndEntityWithSeparateFiles, ReplacedAspectArtifact, ReplacedAspectArtifact> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles',
    'productionPeriod',
    (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ProductionPeriodCharacteristic',
'ProductionPeriodCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Production Period Entity');
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'ProductionPeriodCharacteristic';
defaultSingleEntity.addPreferredName('en' , 'Production Period Characteristic');
 return defaultSingleEntity; })()
,
    false,
    true,
    undefined,
        'productionPeriod',
    false,
    );




getModelClass(): string {
return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
}

getAspectModelUrn(): string {
return MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
}

getProperties(): Array<StaticProperty<AspectWithOptionalPropertiesAndEntityWithSeparateFiles, any>> {
return [MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles.PRODUCTION_PERIOD];
}

getAllProperties(): Array<StaticProperty<AspectWithOptionalPropertiesAndEntityWithSeparateFiles, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Minimal Vehicle Type Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'Simplified aspect containing only ReplacedAspectArtifact as an optional property.', language: 'en'},
        ];
        }


    }


