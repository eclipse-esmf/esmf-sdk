import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithStringEnumeration,} from './AspectWithStringEnumeration';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithStringEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithStringEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithStringEnumeration implements StaticMetaClass<AspectWithStringEnumeration>, PropertyContainer<AspectWithStringEnumeration> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithStringEnumeration';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithStringEnumeration();


 public static readonly  ENUMERATION_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithStringEnumeration, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithStringEnumeration';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'enumerationProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultEnumeration({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),new ArrayList<Value>(){{add({
metaModelBaseAttributes : {},
value : "bar",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});add({
metaModelBaseAttributes : {},
value : "foo",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'enumerationProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithStringEnumeration';
}

getAspectModelUrn(): string {
return MetaAspectWithStringEnumeration .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithStringEnumeration';
}

                        getProperties(): Array<StaticProperty<AspectWithStringEnumeration, any>> {
return [MetaAspectWithStringEnumeration.ENUMERATION_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithStringEnumeration, any>> {
    return this.getProperties();
}




    }


