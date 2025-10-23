













import { AspectWithDescriptionInProperty,} from './AspectWithDescriptionInProperty';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithDescriptionInProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDescriptionInProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDescriptionInProperty implements StaticMetaClass<AspectWithDescriptionInProperty>, PropertyContainer<AspectWithDescriptionInProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDescriptionInProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDescriptionInProperty();


 public static readonly  ENABLED = 
                
        new (class extends DefaultStaticProperty<AspectWithDescriptionInProperty, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithDescriptionInProperty';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'enabled',
preferredNames : [ {
value : "Aktiviert/Deaktiviert",
languageTag : 'de',
},
{
value : "Enabled/Disabled",
languageTag : 'en',
},
 ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#Boolean',
preferredNames : [ {
value : "Boolean",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Represents a boolean value (i.e. a \"flag\").",
languageTag : 'en',
},
 ],
see : [  ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'enabled',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithDescriptionInProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithDescriptionInProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithDescriptionInProperty';
}

                        getProperties(): Array<StaticProperty<AspectWithDescriptionInProperty, any>> {
return [MetaAspectWithDescriptionInProperty.ENABLED];
}

getAllProperties(): Array<StaticProperty<AspectWithDescriptionInProperty, any>> {
    return this.getProperties();
}




    }


