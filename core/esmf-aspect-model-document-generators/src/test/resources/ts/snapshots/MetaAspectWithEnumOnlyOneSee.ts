import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumOnlyOneSee,} from './AspectWithEnumOnlyOneSee';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';




    

/*
* Generated class MetaAspectWithEnumOnlyOneSee (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumOnlyOneSee).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumOnlyOneSee implements StaticMetaClass<AspectWithEnumOnlyOneSee>, PropertyContainer<AspectWithEnumOnlyOneSee> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumOnlyOneSee';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumOnlyOneSee();


 public static readonly  PROP1 = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumOnlyOneSee, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumOnlyOneSee';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'prop1',
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
value : "a",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});add({
metaModelBaseAttributes : {},
value : "b",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'prop1',
    isAbstract : false,
    });




 public static readonly  PROP2 = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumOnlyOneSee, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumOnlyOneSee';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'prop2',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultEnumeration({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [ 'https://test.com',
 ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),new ArrayList<Value>(){{add({
metaModelBaseAttributes : {},
value : "1",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});add({
metaModelBaseAttributes : {},
value : "2",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'prop2',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEnumOnlyOneSee';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumOnlyOneSee .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEnumOnlyOneSee';
}

                        getProperties(): Array<StaticProperty<AspectWithEnumOnlyOneSee, any>> {
return [MetaAspectWithEnumOnlyOneSee.PROP1, MetaAspectWithEnumOnlyOneSee.PROP2];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumOnlyOneSee, any>> {
    return this.getProperties();
}




    }


