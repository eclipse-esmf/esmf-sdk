













import { AspectWithOperationWithoutSeeAttribute,} from './AspectWithOperationWithoutSeeAttribute';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithOperationWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOperationWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOperationWithoutSeeAttribute implements StaticMetaClass<AspectWithOperationWithoutSeeAttribute>, PropertyContainer<AspectWithOperationWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOperationWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOperationWithoutSeeAttribute();


getModelClass(): string {
return 'AspectWithOperationWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithOperationWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOperationWithoutSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithOperationWithoutSeeAttribute, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithOperationWithoutSeeAttribute, any>> {
        return this.getProperties();
}




    }


