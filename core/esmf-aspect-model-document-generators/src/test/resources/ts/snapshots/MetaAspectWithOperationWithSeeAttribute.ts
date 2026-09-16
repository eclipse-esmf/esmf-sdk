













import { AspectWithOperationWithSeeAttribute,} from './AspectWithOperationWithSeeAttribute';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithOperationWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOperationWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOperationWithSeeAttribute implements StaticMetaClass<AspectWithOperationWithSeeAttribute>, PropertyContainer<AspectWithOperationWithSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOperationWithSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOperationWithSeeAttribute();


getModelClass(): string {
return 'AspectWithOperationWithSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithOperationWithSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOperationWithSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithOperationWithSeeAttribute, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithOperationWithSeeAttribute, any>> {
        return this.getProperties();
}




    }


