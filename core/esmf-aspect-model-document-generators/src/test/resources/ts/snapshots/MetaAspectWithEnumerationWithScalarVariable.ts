import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumerationWithScalarVariable,} from './AspectWithEnumerationWithScalarVariable';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithEnumerationWithScalarVariable (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumerationWithScalarVariable).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumerationWithScalarVariable implements StaticMetaClass<AspectWithEnumerationWithScalarVariable>, PropertyContainer<AspectWithEnumerationWithScalarVariable> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumerationWithScalarVariable';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumerationWithScalarVariable();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumerationWithScalarVariable, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumerationWithScalarVariable';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [ {
value : "testProperty",
languageTag : 'en',
},
 ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultEnumeration({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "ReplacedAspectArtifact Result",
languageTag : 'en',
},
 ],
descriptions : [  ],
see : [ 'http://example.com/',
 ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),new ArrayList<Value>(){{add({
metaModelBaseAttributes : {},
value : "No",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});add({
metaModelBaseAttributes : {},
value : "Yes",
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
return 'AspectWithEnumerationWithScalarVariable';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumerationWithScalarVariable .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEnumerationWithScalarVariable';
}

                        getProperties(): Array<StaticProperty<AspectWithEnumerationWithScalarVariable, any>> {
return [MetaAspectWithEnumerationWithScalarVariable.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumerationWithScalarVariable, any>> {
    return this.getProperties();
}




    }


