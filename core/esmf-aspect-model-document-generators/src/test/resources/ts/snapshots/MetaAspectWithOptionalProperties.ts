













import { AspectWithOptionalProperties,} from './AspectWithOptionalProperties';
import { DefaultStaticProperty,StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithOptionalProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalProperties implements StaticMetaClass<AspectWithOptionalProperties>, PropertyContainer<AspectWithOptionalProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalProperties();


 public static readonly  NUMBER_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalProperties, string, string> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalProperties';
    }

        getContainedType(): AspectWithOptionalProperties {
            return 'AspectWithOptionalProperties';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'numberProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultQuantifiable({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#unsignedLong" ),Units.fromName("metre"))
,
    exampleValue : {},
    optional : true,
    notInPayload : false,
        payloadName : 'numberProperty',
    isAbstract : false,
    });




 public static readonly  TIMESTAMP_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithOptionalProperties, Date>{

    
    getPropertyType(): string {
                return 'Date';
    }

    getContainingType(): string {
        return 'AspectWithOptionalProperties';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'timestampProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#Timestamp',
preferredNames : [ {
value : "Timestamp",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property which contains the date and time with an optional timezone.",
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
        payloadName : 'timestampProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithOptionalProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithOptionalProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithOptionalProperties';
}

                        getProperties(): Array<StaticProperty<AspectWithOptionalProperties, any>> {
return [MetaAspectWithOptionalProperties.NUMBER_PROPERTY, MetaAspectWithOptionalProperties.TIMESTAMP_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithOptionalProperties, any>> {
    return this.getProperties();
}




    }


