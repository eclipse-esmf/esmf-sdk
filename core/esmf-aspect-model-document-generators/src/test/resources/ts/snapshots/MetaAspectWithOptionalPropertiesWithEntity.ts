import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithOptionalPropertiesWithEntity,} from './AspectWithOptionalPropertiesWithEntity';
import { DefaultStaticProperty,StaticContainerProperty,} from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithOptionalPropertiesWithEntity (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertiesWithEntity).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalPropertiesWithEntity implements StaticMetaClass<AspectWithOptionalPropertiesWithEntity>, PropertyContainer<AspectWithOptionalPropertiesWithEntity> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertiesWithEntity';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalPropertiesWithEntity();


 public static readonly  TEST_STRING = 
                
        new (class extends DefaultStaticProperty<AspectWithOptionalPropertiesWithEntity, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesWithEntity';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testString',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#Text',
preferredNames : [ {
value : "Text",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.",
languageTag : 'en',
},
 ],
see : [  ],
},
}
,
    exampleValue : {
metaModelBaseAttributes : {},
value : "Example Value Test",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testString',
    isAbstract : false,
    });




 public static readonly  TEST_OPTIONAL_STRING = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertiesWithEntity, string, string> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesWithEntity';
    }

        getContainedType(): AspectWithOptionalPropertiesWithEntity {
            return 'AspectWithOptionalPropertiesWithEntity';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testOptionalString',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#Text',
preferredNames : [ {
value : "Text",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.",
languageTag : 'en',
},
 ],
see : [  ],
},
}
,
    exampleValue : {
metaModelBaseAttributes : {},
value : "Example Value Test",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
},
    optional : true,
    notInPayload : false,
        payloadName : 'testOptionalString',
    isAbstract : false,
    });




 public static readonly  TEST_OPTIONAL_ENTITY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertiesWithEntity, ReplacedAspectArtifact, ReplacedAspectArtifact> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertiesWithEntity';
    }

        getContainedType(): AspectWithOptionalPropertiesWithEntity {
            return 'AspectWithOptionalPropertiesWithEntity';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testOptionalEntity',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
}
,
    exampleValue : {},
    optional : true,
    notInPayload : false,
        payloadName : 'testOptionalEntity',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithOptionalPropertiesWithEntity';
}

getAspectModelUrn(): string {
return MetaAspectWithOptionalPropertiesWithEntity .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithOptionalPropertiesWithEntity';
}

                        getProperties(): Array<StaticProperty<AspectWithOptionalPropertiesWithEntity, any>> {
return [MetaAspectWithOptionalPropertiesWithEntity.TEST_STRING, MetaAspectWithOptionalPropertiesWithEntity.TEST_OPTIONAL_STRING, MetaAspectWithOptionalPropertiesWithEntity.TEST_OPTIONAL_ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithOptionalPropertiesWithEntity, any>> {
    return this.getProperties();
}




    }


