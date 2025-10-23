import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEntitiesAndEither,} from './AspectWithMultipleEntitiesAndEither';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { Either,} from './core/Either';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithMultipleEntitiesAndEither (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesAndEither).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleEntitiesAndEither implements StaticMetaClass<AspectWithMultipleEntitiesAndEither>, PropertyContainer<AspectWithMultipleEntitiesAndEither> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntitiesAndEither';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntitiesAndEither();


 public static readonly  TEST_ENTITY_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesAndEither, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesAndEither';
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
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesAndEither, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesAndEither';
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




 public static readonly  TEST_EITHER_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesAndEither, Either<ReplacedAspectArtifact, ReplacedAspectArtifact>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesAndEither';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testEitherProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'TestEither',
preferredNames : [ {
value : "Test Either",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test Either.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testEitherProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithMultipleEntitiesAndEither';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntitiesAndEither .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntitiesAndEither';
}

                        getProperties(): Array<StaticProperty<AspectWithMultipleEntitiesAndEither, any>> {
return [MetaAspectWithMultipleEntitiesAndEither.TEST_ENTITY_ONE, MetaAspectWithMultipleEntitiesAndEither.TEST_ENTITY_TWO, MetaAspectWithMultipleEntitiesAndEither.TEST_EITHER_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntitiesAndEither, any>> {
    return this.getProperties();
}




    }


