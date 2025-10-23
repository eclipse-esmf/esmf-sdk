import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithMultipleEnumerationsOnMultipleLevels,} from './AspectWithMultipleEnumerationsOnMultipleLevels';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';





    

/*
* Generated class MetaAspectWithMultipleEnumerationsOnMultipleLevels (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleEnumerationsOnMultipleLevels).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleEnumerationsOnMultipleLevels implements StaticMetaClass<AspectWithMultipleEnumerationsOnMultipleLevels>, PropertyContainer<AspectWithMultipleEnumerationsOnMultipleLevels> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleEnumerationsOnMultipleLevels';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleEnumerationsOnMultipleLevels();


 public static readonly  TEST_PROPERTY_WITH_ENUM_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEnumerationsOnMultipleLevels';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithEnumOne',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultEnumeration({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),new ArrayList<Value>(){{add({
metaModelBaseAttributes : {},
value : new BigInteger( "1" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),
});add({
metaModelBaseAttributes : {},
value : new BigInteger( "2" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),
});add({
metaModelBaseAttributes : {},
value : new BigInteger( "3" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),
});}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithEnumOne',
    isAbstract : false,
    });




 public static readonly  TEST_PROPERTY_WITH_ENUM_TWO = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEnumerationsOnMultipleLevels';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyWithEnumTwo',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultEnumeration({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),new ArrayList<Value>(){{add({
metaModelBaseAttributes : {},
value : "One",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});add({
metaModelBaseAttributes : {},
value : "Three",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});add({
metaModelBaseAttributes : {},
value : "Two",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyWithEnumTwo',
    isAbstract : false,
    });




 public static readonly  TEST_ENTITY_WITH_ENUM_ONE = 
                
        new (class extends DefaultStaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithMultipleEnumerationsOnMultipleLevels';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testEntityWithEnumOne',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultSingleEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
}, DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty()))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testEntityWithEnumOne',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithMultipleEnumerationsOnMultipleLevels';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleEnumerationsOnMultipleLevels .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultipleEnumerationsOnMultipleLevels';
}

                        getProperties(): Array<StaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, any>> {
return [MetaAspectWithMultipleEnumerationsOnMultipleLevels.TEST_PROPERTY_WITH_ENUM_ONE, MetaAspectWithMultipleEnumerationsOnMultipleLevels.TEST_PROPERTY_WITH_ENUM_TWO, MetaAspectWithMultipleEnumerationsOnMultipleLevels.TEST_ENTITY_WITH_ENUM_ONE];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleEnumerationsOnMultipleLevels, any>> {
    return this.getProperties();
}




    }


