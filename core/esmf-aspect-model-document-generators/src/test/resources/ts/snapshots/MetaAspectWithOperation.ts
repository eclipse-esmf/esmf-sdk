













import { AspectWithOperation,} from './AspectWithOperation';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithOperation (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOperation).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOperation implements StaticMetaClass<AspectWithOperation>, PropertyContainer<AspectWithOperation> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOperation';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOperation();


getModelClass(): string {
return 'AspectWithOperation';
}

getAspectModelUrn(): string {
return MetaAspectWithOperation .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOperation';
}

getProperties(): Array<StaticProperty<AspectWithOperation, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithOperation, any>> {
        return this.getProperties();
}




    }


