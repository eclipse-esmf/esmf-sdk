import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties,} from './AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties implements StaticMetaClass<AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties>, PropertyContainer<AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties';
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
});put(MetaReplacedAspectArtifact.NOT_IN_PAYLOAD_ENTITY_PROPERTY,{
metaModelBaseAttributes : {},
value : "This is not part of the payload.",
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
return 'AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties, any>> {
return [MetaAspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityEnumerationWithOptionalAndNotInPayloadProperties, any>> {
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


