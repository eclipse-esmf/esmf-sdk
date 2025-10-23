













import { AspectWithCurie,} from './AspectWithCurie';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCurie (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCurie).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCurie implements StaticMetaClass<AspectWithCurie>, PropertyContainer<AspectWithCurie> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCurie';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCurie();


 public static readonly  TEST_CURIE = 
                
        new (class extends DefaultStaticProperty<AspectWithCurie, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithCurie';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testCurie',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#UnitReference',
preferredNames : [ {
value : "Unit Reference",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property containing a reference to one of the units in the Unit Catalog.",
languageTag : 'en',
},
 ],
see : [  ],
},
}
,
    exampleValue : {
metaModelBaseAttributes : {},
value : new Curie( "unit:hectopascal" ),
type : new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testCurie',
    isAbstract : false,
    });




 public static readonly  TEST_CURIE_WITHOUT_EXAMPLE_VALUE = 
                
        new (class extends DefaultStaticProperty<AspectWithCurie, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithCurie';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testCurieWithoutExampleValue',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.CHARACTERISTIC_NAMESPACE + '#UnitReference',
preferredNames : [ {
value : "Unit Reference",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Describes a Property containing a reference to one of the units in the Unit Catalog.",
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
        payloadName : 'testCurieWithoutExampleValue',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCurie';
}

getAspectModelUrn(): string {
return MetaAspectWithCurie .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCurie';
}

                        getProperties(): Array<StaticProperty<AspectWithCurie, any>> {
return [MetaAspectWithCurie.TEST_CURIE, MetaAspectWithCurie.TEST_CURIE_WITHOUT_EXAMPLE_VALUE];
}

getAllProperties(): Array<StaticProperty<AspectWithCurie, any>> {
    return this.getProperties();
}




    }


