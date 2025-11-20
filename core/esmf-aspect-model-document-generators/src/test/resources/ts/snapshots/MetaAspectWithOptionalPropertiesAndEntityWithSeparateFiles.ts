import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithOptionalPropertiesAndEntityWithSeparateFiles,} from './AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
import { DefaultEntity,DefaultSingleEntity,} from './aspect-meta-model';
import { LangString,} from './core/langString';


import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertiesAndEntityWithSeparateFiles).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles implements StaticMetaClass<AspectWithOptionalPropertiesAndEntityWithSeparateFiles>, PropertyContainer<AspectWithOptionalPropertiesAndEntityWithSeparateFiles> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalPropertiesAndEntityWithSeparateFiles();


 public static readonly  PRODUCTION_PERIOD = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertiesAndEntityWithSeparateFiles, ReplacedAspectArtifact, ReplacedAspectArtifact> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
    }

        getContainedType(): string {
            return 'AspectWithOptionalPropertiesAndEntityWithSeparateFiles';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultSingleEntity = new DefaultSingleEntity(null, 
null, 
null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Minimal Vehicle Type Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('Simplified aspect containing only ReplacedAspectArtifact as an optional property.', 'en'),
        ];
        }


    }


