













import { AspectWithPropertyWithPreferredNames,} from './AspectWithPropertyWithPreferredNames';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithPropertyWithPreferredNames (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithPreferredNames).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithPropertyWithPreferredNames implements StaticMetaClass<AspectWithPropertyWithPreferredNames>, PropertyContainer<AspectWithPropertyWithPreferredNames> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPropertyWithPreferredNames';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPropertyWithPreferredNames();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPropertyWithPreferredNames, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPropertyWithPreferredNames';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#boolean" ))
defaultCharacteristic.addAspectModelUrn = this.NAMESPACE + 'BooleanReplacedAspectArtifact';
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testBoolean',
    false,
    );




getModelClass(): string {
return 'AspectWithPropertyWithPreferredNames';
}

getAspectModelUrn(): string {
return MetaAspectWithPropertyWithPreferredNames .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithPropertyWithPreferredNames';
}

                        getProperties(): Array<StaticProperty<AspectWithPropertyWithPreferredNames, any>> {
return [MetaAspectWithPropertyWithPreferredNames.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPropertyWithPreferredNames, any>> {
    return this.getProperties();
}




    }


