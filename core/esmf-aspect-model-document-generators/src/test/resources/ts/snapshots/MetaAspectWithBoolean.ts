













import { AspectWithBoolean,} from './AspectWithBoolean';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithBoolean (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithBoolean).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithBoolean implements StaticMetaClass<AspectWithBoolean>, PropertyContainer<AspectWithBoolean> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithBoolean';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithBoolean();


 public static readonly  TEST_BOOLEAN = 
                
        new (class extends DefaultStaticProperty<AspectWithBoolean, boolean>{

    
    getPropertyType(): string {
                return 'boolean';
    }

    getContainingType(): string {
        return 'AspectWithBoolean';
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
return 'AspectWithBoolean';
}

getAspectModelUrn(): string {
return MetaAspectWithBoolean .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithBoolean';
}

                        getProperties(): Array<StaticProperty<AspectWithBoolean, any>> {
return [MetaAspectWithBoolean.TEST_BOOLEAN];
}

getAllProperties(): Array<StaticProperty<AspectWithBoolean, any>> {
    return this.getProperties();
}




    }


