













import { AspectWithPreferredNames,} from './AspectWithPreferredNames';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithPreferredNames (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithPreferredNames).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithPreferredNames implements StaticMetaClass<AspectWithPreferredNames>, PropertyContainer<AspectWithPreferredNames> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithPreferredNames';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithPreferredNames();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithPreferredNames, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithPreferredNames';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testBoolean',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'BooleanReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testBoolean',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithPreferredNames';
}

getAspectModelUrn(): string {
return MetaAspectWithPreferredNames .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithPreferredNames';
}

                        getProperties(): Array<StaticProperty<AspectWithPreferredNames, any>> {
return [MetaAspectWithPreferredNames.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithPreferredNames, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Aspekt Mit Boolean', languageTag: 'de'},
            {value: 'Aspect With Boolean', languageTag: 'en'},
        ];
        }



    }


