import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntitiesOnMultipleLevels,} from './AspectWithMultipleEntitiesOnMultipleLevels';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';




    

/*
* Generated class MetaAspectWithMultipleEntitiesOnMultipleLevels (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesOnMultipleLevels).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleEntitiesOnMultipleLevels implements StaticMetaClass<AspectWithMultipleEntitiesOnMultipleLevels>, PropertyContainer<AspectWithMultipleEntitiesOnMultipleLevels> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntitiesOnMultipleLevels';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntitiesOnMultipleLevels();


 public static readonly  TEST_ENTITY_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesOnMultipleLevels';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testEntityOne',
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
    optional : false,
    notInPayload : false,
        payloadName : 'testEntityOne',
    isAbstract : false,
    });




 public static readonly  TEST_ENTITY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesOnMultipleLevels';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testEntityTwo',
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
    optional : false,
    notInPayload : false,
        payloadName : 'testEntityTwo',
    isAbstract : false,
    });




 public static readonly  TEST_STRING = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesOnMultipleLevels';
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




 public static readonly  TEST_SECOND_ENTITY = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesOnMultipleLevels';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testSecondEntity',
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
    optional : false,
    notInPayload : false,
        payloadName : 'testSecondEntity',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithMultipleEntitiesOnMultipleLevels';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntitiesOnMultipleLevels .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntitiesOnMultipleLevels';
}

                        getProperties(): Array<StaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, any>> {
return [MetaAspectWithMultipleEntitiesOnMultipleLevels.TEST_ENTITY_ONE, MetaAspectWithMultipleEntitiesOnMultipleLevels.TEST_ENTITY_TWO, MetaAspectWithMultipleEntitiesOnMultipleLevels.TEST_STRING, MetaAspectWithMultipleEntitiesOnMultipleLevels.TEST_SECOND_ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntitiesOnMultipleLevels, any>> {
    return this.getProperties();
}




    }


