













import { AspectWithDescriptions,} from './AspectWithDescriptions';
import { DefaultCharacteristic,DefaultScalar,} from './aspect-meta-model';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithDescriptions (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDescriptions).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDescriptions implements StaticMetaClass<AspectWithDescriptions>, PropertyContainer<AspectWithDescriptions> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDescriptions';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDescriptions();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithDescriptions, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithDescriptions';
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
return 'AspectWithDescriptions';
}

getAspectModelUrn(): string {
return MetaAspectWithDescriptions .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithDescriptions';
}

                        getProperties(): Array<StaticProperty<AspectWithDescriptions, any>> {
return [MetaAspectWithDescriptions.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithDescriptions, any>> {
    return this.getProperties();
}


        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('Test Beschreibung', 'de'),
            new LangString('Test Description', 'en'),
        ];
        }


    }


