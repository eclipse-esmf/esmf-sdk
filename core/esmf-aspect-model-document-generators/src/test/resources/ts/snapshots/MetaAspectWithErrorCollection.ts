













import { AspectWithErrorCollection,} from './AspectWithErrorCollection';
import { DefaultCollection,DefaultEntity,} from './aspect-meta-model';
import { Error,} from './Error';
import { LangString,} from './core/langString';
import { MetaError,} from './MetaError';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithErrorCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithErrorCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithErrorCollection implements StaticMetaClass<AspectWithErrorCollection>, PropertyContainer<AspectWithErrorCollection> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithErrorCollection';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithErrorCollection();


 public static readonly  ITEMS = 
                
        new (class extends StaticContainerProperty<AspectWithErrorCollection, Error, Error[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithErrorCollection';
    }

        getContainedType(): string {
            return 'AspectWithErrorCollection';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultCollection = new DefaultCollection(null, 
null, 
null, 
true, false, undefined,
(() => { const defaultEntityError = new DefaultEntity(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Errors Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('The Errors Aspect delivers a list of the currently active errors for a specific machine.', 'en'),
        ];
        }


    }


