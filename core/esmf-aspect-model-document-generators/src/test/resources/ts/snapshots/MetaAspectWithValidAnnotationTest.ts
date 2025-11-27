import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithValidAnnotationTest,} from './AspectWithValidAnnotationTest';
import { DefaultCharacteristic,DefaultCollection,DefaultEntity,DefaultScalar,DefaultSingleEntity,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';

import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';



    

/*
* Generated class MetaAspectWithValidAnnotationTest (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithValidAnnotationTest).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithValidAnnotationTest implements StaticMetaClass<AspectWithValidAnnotationTest>, PropertyContainer<AspectWithValidAnnotationTest> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithValidAnnotationTest';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithValidAnnotationTest();


 public static readonly  ENTITY = 
                
        new (class extends DefaultStaticProperty<AspectWithValidAnnotationTest, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithValidAnnotationTest';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithValidAnnotationTest',
    'entity',
    (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityCharacteristic',
'EntityCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Test Entity');
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'EntityCharacteristic';
defaultSingleEntity.addPreferredName('en' , 'Test Entity Characteristic');
 return defaultSingleEntity; })()
,
    false,
    false,
    undefined,
        'entity',
    false,
    );




 public static readonly  COLLECTION_ENTITY = 
                
        new (class extends StaticContainerProperty<AspectWithValidAnnotationTest, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithValidAnnotationTest';
    }

        getContainedType(): string {
            return 'AspectWithValidAnnotationTest';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithValidAnnotationTest',
    'collectionEntity',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestCollection',
'TestCollection',
true, false, undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Test Entity');
 return defaultEntityReplacedAspectArtifact; })())
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollection';
defaultCollection.addPreferredName('en' , 'Test Collection');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'collectionEntity',
    false,
    );




 public static readonly  OPTIONAL_ENTITY = 
                
        new (class extends StaticContainerProperty<AspectWithValidAnnotationTest, ReplacedAspectArtifact, ReplacedAspectArtifact> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithValidAnnotationTest';
    }

        getContainedType(): string {
            return 'AspectWithValidAnnotationTest';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithValidAnnotationTest',
    'optionalEntity',
    (() => { const defaultSingleEntity = new DefaultSingleEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'EntityCharacteristic',
'EntityCharacteristic',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Test Entity');
 return defaultEntityReplacedAspectArtifact; })())
defaultSingleEntity.addAspectModelUrn = this.NAMESPACE + 'EntityCharacteristic';
defaultSingleEntity.addPreferredName('en' , 'Test Entity Characteristic');
 return defaultSingleEntity; })()
,
    false,
    true,
    undefined,
        'optionalEntity',
    false,
    );




 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithValidAnnotationTest, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithValidAnnotationTest';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithValidAnnotationTest',
    'testProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




 public static readonly  COLLECTION_TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithValidAnnotationTest, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithValidAnnotationTest';
    }

        getContainedType(): string {
            return 'AspectWithValidAnnotationTest';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithValidAnnotationTest',
    'collectionTestProperty',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
true, false, undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.isAnonymousNode = true;
defaultCollection.addPreferredName('en' , 'Collection Test Property');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'collectionTestProperty',
    false,
    );




 public static readonly  OPTIONAL_TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithValidAnnotationTest, string, string> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithValidAnnotationTest';
    }

        getContainedType(): string {
            return 'AspectWithValidAnnotationTest';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithValidAnnotationTest',
    'optionalTestProperty',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })()
,
    false,
    true,
    undefined,
        'optionalTestProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithValidAnnotationTest';
}

getAspectModelUrn(): string {
return MetaAspectWithValidAnnotationTest .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithValidAnnotationTest';
}

getProperties(): Array<StaticProperty<AspectWithValidAnnotationTest, any>> {
return [MetaAspectWithValidAnnotationTest.ENTITY, MetaAspectWithValidAnnotationTest.COLLECTION_ENTITY, MetaAspectWithValidAnnotationTest.OPTIONAL_ENTITY, MetaAspectWithValidAnnotationTest.TEST_PROPERTY, MetaAspectWithValidAnnotationTest.COLLECTION_TEST_PROPERTY, MetaAspectWithValidAnnotationTest.OPTIONAL_TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithValidAnnotationTest, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Aspect with Valid Annotation Test', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This aspect is used to test the @Valid annotation rules.', language: 'en'},
        ];
        }


    }


