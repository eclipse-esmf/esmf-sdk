













import { AspectWithOperationWithMultipleSeeAttributes,} from './AspectWithOperationWithMultipleSeeAttributes';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithOperationWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOperationWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOperationWithMultipleSeeAttributes implements StaticMetaClass<AspectWithOperationWithMultipleSeeAttributes>, PropertyContainer<AspectWithOperationWithMultipleSeeAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOperationWithMultipleSeeAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOperationWithMultipleSeeAttributes();


getModelClass(): string {
return 'AspectWithOperationWithMultipleSeeAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithOperationWithMultipleSeeAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOperationWithMultipleSeeAttributes';
}

getProperties(): Array<StaticProperty<AspectWithOperationWithMultipleSeeAttributes, any>> {
return [];
}

getAllProperties(): Array<StaticProperty<AspectWithOperationWithMultipleSeeAttributes, any>> {
        return this.getProperties();
}




    }


