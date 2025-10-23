import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumAndOptionalEnumProperties,} from './AspectWithEnumAndOptionalEnumProperties';
import { DefaultStaticProperty,StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithEnumAndOptionalEnumProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumAndOptionalEnumProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumAndOptionalEnumProperties implements StaticMetaClass<AspectWithEnumAndOptionalEnumProperties>, PropertyContainer<AspectWithEnumAndOptionalEnumProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumAndOptionalEnumProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumAndOptionalEnumProperties();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumAndOptionalEnumProperties, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumAndOptionalEnumProperties';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
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
        payloadName : 'testProperty',
    isAbstract : false,
    });




 public static readonly  OPTIONAL_TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithEnumAndOptionalEnumProperties, string, ReplacedAspectArtifact> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithEnumAndOptionalEnumProperties';
    }

        getContainedType(): AspectWithEnumAndOptionalEnumProperties {
            return 'AspectWithEnumAndOptionalEnumProperties';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'optionalTestProperty',
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
    optional : true,
    notInPayload : false,
        payloadName : 'optionalTestProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEnumAndOptionalEnumProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumAndOptionalEnumProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEnumAndOptionalEnumProperties';
}

                        getProperties(): Array<StaticProperty<AspectWithEnumAndOptionalEnumProperties, any>> {
return [MetaAspectWithEnumAndOptionalEnumProperties.TEST_PROPERTY, MetaAspectWithEnumAndOptionalEnumProperties.OPTIONAL_TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumAndOptionalEnumProperties, any>> {
    return this.getProperties();
}




    }


