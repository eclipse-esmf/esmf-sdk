













import { AspectWithAllBaseAttributes,} from './AspectWithAllBaseAttributes';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithAllBaseAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAllBaseAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithAllBaseAttributes implements StaticMetaClass<AspectWithAllBaseAttributes>, PropertyContainer<AspectWithAllBaseAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithAllBaseAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithAllBaseAttributes();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithAllBaseAttributes, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithAllBaseAttributes';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testBoolean',
preferredNames : [  ],
descriptions : [  ],
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
return 'AspectWithAllBaseAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithAllBaseAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithAllBaseAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithAllBaseAttributes, any>> {
return [MetaAspectWithAllBaseAttributes.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithAllBaseAttributes, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Aspekt Mit Boolean', languageTag: 'de'},
            {value: 'Aspect With Boolean', languageTag: 'en'},
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            {value: 'Test Beschreibung', languageTag: 'de'},
            {value: 'Test Description', languageTag: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            "http://example.com/",
        ];
        }

    }


