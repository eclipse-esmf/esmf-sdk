import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityCollection,} from './AspectWithEntityCollection';
import { DefaultCollection,DefaultEntity,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';

import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';



    

/*
* Generated class MetaAspectWithEntityCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEntityCollection implements StaticMetaClass<AspectWithEntityCollection>, PropertyContainer<AspectWithEntityCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityCollection();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithEntityCollection, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityCollection';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEntityCollection',
    'testProperty',
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
defaultEntityReplacedAspectArtifact.addDescription('en' , 'This is a test entity');
 return defaultEntityReplacedAspectArtifact; })())
defaultCollection.addAspectModelUrn = this.NAMESPACE + 'TestCollection';
defaultCollection.addPreferredName('en' , 'Test Collection');
defaultCollection.addDescription('en' , 'This is a test collection.');
defaultCollection.addSeeReference('http:\/\/example.com\/');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEntityCollection';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityCollection .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEntityCollection';
}

getProperties(): Array<StaticProperty<AspectWithEntityCollection, any>> {
return [MetaAspectWithEntityCollection.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityCollection, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This is a test description', language: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


