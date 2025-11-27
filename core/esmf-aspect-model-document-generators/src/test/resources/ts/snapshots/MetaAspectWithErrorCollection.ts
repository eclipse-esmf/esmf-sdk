













import { AspectWithErrorCollection,} from './AspectWithErrorCollection';
import { DefaultCollection,DefaultEntity,} from './esmf/aspect-meta-model';
import { Error,} from './Error';
import { KnownVersion,} from './esmf/shared/known-version';
import { MetaError,} from './MetaError';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithErrorCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithErrorCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithErrorCollection implements StaticMetaClass<AspectWithErrorCollection>, PropertyContainer<AspectWithErrorCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithErrorCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithErrorCollection();


 public static readonly  ITEMS = 
                
        new (class extends StaticContainerProperty<AspectWithErrorCollection, Error, Error[]> {

    
    getPropertyType(): string {
            return 'Error';
    }

    getContainingType(): string {
        return 'AspectWithErrorCollection';
    }

        getContainedType(): string {
            return 'AspectWithErrorCollection';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithErrorCollection',
    'items',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
true, false, undefined,
(() => { const defaultEntityError = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'Error',
'Error',
MetaError.INSTANCE.getProperties(),false,
undefined)
defaultEntityError.addAspectModelUrn = this.NAMESPACE + 'Error';
defaultEntityError.addPreferredName('en' , 'Error Entity');
defaultEntityError.addDescription('en' , 'The Entity describing an Error.');
 return defaultEntityError; })())
defaultCollection.isAnonymousNode = true;
defaultCollection.addPreferredName('en' , 'Errors');
defaultCollection.addDescription('en' , 'A collection of Error Entities.');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'items',
    false,
    );




getModelClass(): string {
return 'AspectWithErrorCollection';
}

getAspectModelUrn(): string {
return MetaAspectWithErrorCollection .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithErrorCollection';
}

getProperties(): Array<StaticProperty<AspectWithErrorCollection, any>> {
return [MetaAspectWithErrorCollection.ITEMS];
}

getAllProperties(): Array<StaticProperty<AspectWithErrorCollection, any>> {
        return this.getProperties();
}

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Errors Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'The Errors Aspect delivers a list of the currently active errors for a specific machine.', language: 'en'},
        ];
        }


    }


