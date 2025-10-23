import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityEnumeration,} from './AspectWithEntityEnumeration';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithEntityEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityEnumeration implements StaticMetaClass<AspectWithEntityEnumeration>, PropertyContainer<AspectWithEntityEnumeration> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityEnumeration';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityEnumeration();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityEnumeration, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityEnumeration';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [ {
value : "Test Property",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test property.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
'http://example.com/me',
 ],
},
    characteristic :     new DefaultEnumeration({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "Test Enumeration",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test for enumeration.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "Test Entity",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test entity",
languageTag : 'en',
},
 ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty()),new ArrayList<Value>(){{add(new DefaultEntityInstance({
urn : this.NAMESPACE + 'entityInstance',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new HashMap<Property, Value>() {{put(MetaReplacedAspectArtifact.ENTITY_PROPERTY,{
metaModelBaseAttributes : {},
value : "This is a test.",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}},DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "Test Entity",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test entity",
languageTag : 'en',
},
 ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty())));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEntityEnumeration';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityEnumeration .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityEnumeration';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityEnumeration, any>> {
return [MetaAspectWithEntityEnumeration.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityEnumeration, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Test Aspect', languageTag: 'en'},
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            {value: 'This is a test description', languageTag: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            "http://example.com/",
        ];
        }

    }


