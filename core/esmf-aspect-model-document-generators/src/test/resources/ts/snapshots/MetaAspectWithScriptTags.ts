import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithScriptTags,} from './AspectWithScriptTags';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithScriptTags (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithScriptTags).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithScriptTags implements StaticMetaClass<AspectWithScriptTags>, PropertyContainer<AspectWithScriptTags> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithScriptTags';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithScriptTags();


 public static readonly  TEST_ENTITY = 
                
        new (class extends DefaultStaticProperty<AspectWithScriptTags, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithScriptTags';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testEntity',
preferredNames : [ {
value : "Test preferred name with script: <script>alert('Should not be alerted');</script>",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test description with script: <script>alert('Should not be alerted');</script>",
languageTag : 'en',
},
 ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
preferredNames : [ {
value : "Test preferred name with script: <script>alert('Should not be alerted');</script>",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test description with script: <script>alert('Should not be alerted');</script>",
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
        payloadName : 'testEntity',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithScriptTags';
}

getAspectModelUrn(): string {
return MetaAspectWithScriptTags .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithScriptTags';
}

                        getProperties(): Array<StaticProperty<AspectWithScriptTags, any>> {
return [MetaAspectWithScriptTags.TEST_ENTITY];
}

getAllProperties(): Array<StaticProperty<AspectWithScriptTags, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Aspect With Entity', languageTag: 'en'},
        ];
        }



    }


