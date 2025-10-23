import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithUsedAndUnusedEnumeration,} from './AspectWithUsedAndUnusedEnumeration';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithUsedAndUnusedEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithUsedAndUnusedEnumeration implements StaticMetaClass<AspectWithUsedAndUnusedEnumeration>, PropertyContainer<AspectWithUsedAndUnusedEnumeration> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedEnumeration';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUsedAndUnusedEnumeration();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithUsedAndUnusedEnumeration, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithUsedAndUnusedEnumeration';
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
preferredNames : [ {
value : "Used Test Enumeration",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Used Test Enumeration",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
'http://example.com/me',
 ],
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
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithUsedAndUnusedEnumeration';
}

getAspectModelUrn(): string {
return MetaAspectWithUsedAndUnusedEnumeration .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithUsedAndUnusedEnumeration';
}

                        getProperties(): Array<StaticProperty<AspectWithUsedAndUnusedEnumeration, any>> {
return [MetaAspectWithUsedAndUnusedEnumeration.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUsedAndUnusedEnumeration, any>> {
    return this.getProperties();
}




    }


