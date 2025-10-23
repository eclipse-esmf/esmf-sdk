













import { AspectWithPropertyWithDescriptions,} from './AspectWithPropertyWithDescriptions';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithPropertyWithDescriptions (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithDescriptions).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithPropertyWithDescriptions implements StaticMetaClass<AspectWithPropertyWithDescriptions>, PropertyContainer<AspectWithPropertyWithDescriptions> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPropertyWithDescriptions';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPropertyWithDescriptions();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPropertyWithDescriptions, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPropertyWithDescriptions';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testBoolean',
preferredNames : [  ],
descriptions : [ {
value : "Test Beschreibung",
languageTag : 'de',
},
{
value : "Test Description",
languageTag : 'en',
},
 ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'BooleanReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testBoolean',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithPropertyWithDescriptions';
}

getAspectModelUrn(): string {
return MetaAspectWithPropertyWithDescriptions .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithPropertyWithDescriptions';
}

                        getProperties(): Array<StaticProperty<AspectWithPropertyWithDescriptions, any>> {
return [MetaAspectWithPropertyWithDescriptions.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPropertyWithDescriptions, any>> {
    return this.getProperties();
}




    }


