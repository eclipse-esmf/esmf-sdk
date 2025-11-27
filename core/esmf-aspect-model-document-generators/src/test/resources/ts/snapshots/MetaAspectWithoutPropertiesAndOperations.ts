













import { AspectWithoutPropertiesAndOperations,} from './AspectWithoutPropertiesAndOperations';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithoutPropertiesAndOperations (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithoutPropertiesAndOperations).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithoutPropertiesAndOperations implements StaticMetaClass<AspectWithoutPropertiesAndOperations>, PropertyContainer<AspectWithoutPropertiesAndOperations> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithoutPropertiesAndOperations';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithoutPropertiesAndOperations();


getModelClass(): string {
return 'AspectWithoutPropertiesAndOperations';
}

getAspectModelUrn(): string {
return MetaAspectWithoutPropertiesAndOperations .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithoutPropertiesAndOperations';
}

getProperties(): Array<StaticProperty<AspectWithoutPropertiesAndOperations, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithoutPropertiesAndOperations, any>> {
        return this.getProperties();
}




    }


