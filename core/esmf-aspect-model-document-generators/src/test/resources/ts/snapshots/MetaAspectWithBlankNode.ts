













import { AspectWithBlankNode,} from './AspectWithBlankNode';
import { DefaultCollection,DefaultScalar,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithBlankNode (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithBlankNode).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithBlankNode implements StaticMetaClass<AspectWithBlankNode>, PropertyContainer<AspectWithBlankNode> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithBlankNode';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithBlankNode();


 public static readonly  LIST = 
                
        new (class extends StaticContainerProperty<AspectWithBlankNode, string, string[]> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithBlankNode';
    }

        getContainedType(): string {
            return 'AspectWithBlankNode';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithBlankNode',
    'list',
    (() => { const defaultCollection = new DefaultCollection(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Aspekt mit anonymen Knoten', language: 'de'},
            {value: 'Aspect With Blank Node', language: 'en'},
        ];
        }



    }


