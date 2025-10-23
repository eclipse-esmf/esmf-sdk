













import { AspectWithMultipleEntitiesSameExtend,} from './AspectWithMultipleEntitiesSameExtend';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { MetatestEntityOne,} from './MetatestEntityOne';
import { MetatestEntityTwo,} from './MetatestEntityTwo';
import { testEntityOne,} from './testEntityOne';
import { testEntityTwo,} from './testEntityTwo';


    

/*
* Generated class MetaAspectWithMultipleEntitiesSameExtend (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEntitiesSameExtend).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleEntitiesSameExtend implements StaticMetaClass<AspectWithMultipleEntitiesSameExtend>, PropertyContainer<AspectWithMultipleEntitiesSameExtend> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEntitiesSameExtend';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEntitiesSameExtend();


 public static readonly  TEST_PROPERTY_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesSameExtend, testEntityOne>{

    
    getPropertyType(): string {
                return 'testEntityOne';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesSameExtend';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyOne',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testCharacteristicOne',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyOne',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEntitiesSameExtend, testEntityTwo>{

    
    getPropertyType(): string {
                return 'testEntityTwo';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEntitiesSameExtend';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyTwo',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testCharacteristicTwo',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyTwo',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithMultipleEntitiesSameExtend';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEntitiesSameExtend .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultipleEntitiesSameExtend';
}

                        getProperties(): Array<StaticProperty<AspectWithMultipleEntitiesSameExtend, any>> {
return [MetaAspectWithMultipleEntitiesSameExtend.TEST_PROPERTY_ONE, MetaAspectWithMultipleEntitiesSameExtend.TEST_PROPERTY_TWO];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEntitiesSameExtend, any>> {
    return this.getProperties();
}




    }


