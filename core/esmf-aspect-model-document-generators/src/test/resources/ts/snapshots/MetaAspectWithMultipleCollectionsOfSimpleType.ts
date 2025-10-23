













import { AspectWithMultipleCollectionsOfSimpleType,} from './AspectWithMultipleCollectionsOfSimpleType';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithMultipleCollectionsOfSimpleType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultipleCollectionsOfSimpleType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultipleCollectionsOfSimpleType implements StaticMetaClass<AspectWithMultipleCollectionsOfSimpleType>, PropertyContainer<AspectWithMultipleCollectionsOfSimpleType> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultipleCollectionsOfSimpleType';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMultipleCollectionsOfSimpleType();


 public static readonly  TEST_LIST_INT = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleCollectionsOfSimpleType, number, number[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithMultipleCollectionsOfSimpleType';
    }

        getContainedType(): AspectWithMultipleCollectionsOfSimpleType {
            return 'AspectWithMultipleCollectionsOfSimpleType';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testListInt',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultList({
urn : this.NAMESPACE + 'IntegerList',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" )),Optional.empty())
,
    exampleValue : {
metaModelBaseAttributes : {},
value : Integer.valueOf( "35" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testListInt',
    isAbstract : false,
    });




 public static readonly  TEST_LIST_STRING = 
                
        new (class extends StaticContainerProperty<AspectWithMultipleCollectionsOfSimpleType, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithMultipleCollectionsOfSimpleType';
    }

        getContainedType(): AspectWithMultipleCollectionsOfSimpleType {
            return 'AspectWithMultipleCollectionsOfSimpleType';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testListString',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultList({
urn : this.NAMESPACE + 'StringList',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" )),Optional.empty())
,
    exampleValue : {
metaModelBaseAttributes : {},
value : "test string",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testListString',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithMultipleCollectionsOfSimpleType';
}

getAspectModelUrn(): string {
return MetaAspectWithMultipleCollectionsOfSimpleType .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMultipleCollectionsOfSimpleType';
}

                        getProperties(): Array<StaticProperty<AspectWithMultipleCollectionsOfSimpleType, any>> {
return [MetaAspectWithMultipleCollectionsOfSimpleType.TEST_LIST_INT, MetaAspectWithMultipleCollectionsOfSimpleType.TEST_LIST_STRING];
}

getAllProperties(): Array<StaticProperty<AspectWithMultipleCollectionsOfSimpleType, any>> {
    return this.getProperties();
}




    }


