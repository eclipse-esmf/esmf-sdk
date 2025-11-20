













import { AspectWithBlankNode,} from './AspectWithBlankNode';
import { DefaultCollection,DefaultScalar,} from './aspect-meta-model';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithBlankNode (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithBlankNode).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithBlankNode implements StaticMetaClass<AspectWithBlankNode>, PropertyContainer<AspectWithBlankNode> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithBlankNode';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithBlankNode();


 public static readonly  LIST = 
                
        new (class extends StaticContainerProperty<AspectWithBlankNode, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithBlankNode';
    }

        getContainedType(): string {
            return 'AspectWithBlankNode';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultCollection = new DefaultCollection(null, 
null, 
null, 
true, false, undefined,
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCollection.isAnonymousNode = true;
defaultCollection.addPreferredName('de' , 'Blank Node Liste');
defaultCollection.addPreferredName('en' , 'Blank Node Collection');
 return defaultCollection; })()
,
    false,
    false,
    undefined,
        'list',
    false,
    );




getModelClass(): string {
return 'AspectWithBlankNode';
}

getAspectModelUrn(): string {
return MetaAspectWithBlankNode .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithBlankNode';
}

                        getProperties(): Array<StaticProperty<AspectWithBlankNode, any>> {
return [MetaAspectWithBlankNode.LIST];
}

getAllProperties(): Array<StaticProperty<AspectWithBlankNode, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            new LangString('Aspekt mit anonymen Knoten', 'de'),
            new LangString('Aspect With Blank Node', 'en'),
        ];
        }



    }


