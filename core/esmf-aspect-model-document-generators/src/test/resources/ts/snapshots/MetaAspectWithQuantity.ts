import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithQuantity,} from './AspectWithQuantity';
import { DefaultEntity,DefaultSingleEntity,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';
import { MetaQuantity,} from './MetaQuantity';




    

/*
* Generated class MetaAspectWithQuantity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithQuantity implements StaticMetaClass<AspectWithQuantity>, PropertyContainer<AspectWithQuantity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithQuantity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithQuantity();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithQuantity, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithQuantity';
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
(() => { const extendsDefaultEntityQuantity = new DefaultEntity(null, 
null, 
null, 
MetaQuantity.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityQuantity.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:entity:2.2.0#Quantity';
extendsDefaultEntityQuantity.addPreferredName('en' , 'Quantity');
extendsDefaultEntityQuantity.addDescription('en' , 'A numeric value and the physical unit of the value.');
 return extendsDefaultEntityQuantity; })())
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Quantity');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'A numeric value and the physical unit of the value.');
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.isAnonymousNode = true;
 return defaultSingleEntity; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithQuantity';
}

getAspectModelUrn(): string {
return MetaAspectWithQuantity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithQuantity';
}

                        getProperties(): Array<StaticProperty<AspectWithQuantity, any>> {
return [MetaAspectWithQuantity.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithQuantity, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Test Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('This is a test description', 'en'),
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


