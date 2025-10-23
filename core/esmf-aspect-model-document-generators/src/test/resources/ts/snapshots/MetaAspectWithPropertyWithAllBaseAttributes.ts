













import { AspectWithPropertyWithAllBaseAttributes,} from './AspectWithPropertyWithAllBaseAttributes';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithPropertyWithAllBaseAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPropertyWithAllBaseAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithPropertyWithAllBaseAttributes implements StaticMetaClass<AspectWithPropertyWithAllBaseAttributes>, PropertyContainer<AspectWithPropertyWithAllBaseAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPropertyWithAllBaseAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPropertyWithAllBaseAttributes();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPropertyWithAllBaseAttributes, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPropertyWithAllBaseAttributes';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testBoolean',
preferredNames : [ {
value : "Test Boolean",
languageTag : 'de',
},
{
value : "Test Boolean",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Beschreibung",
languageTag : 'de',
},
{
value : "Test Description",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
'http://example.com/me',
 ],
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
return 'AspectWithPropertyWithAllBaseAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithPropertyWithAllBaseAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithPropertyWithAllBaseAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithPropertyWithAllBaseAttributes, any>> {
return [MetaAspectWithPropertyWithAllBaseAttributes.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPropertyWithAllBaseAttributes, any>> {
    return this.getProperties();
}




    }


