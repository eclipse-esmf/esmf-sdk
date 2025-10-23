import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithState,} from './AspectWithState';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithState (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithState).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithState implements StaticMetaClass<AspectWithState>, PropertyContainer<AspectWithState> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithState';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithState();


 public static readonly  STATUS = 
                
        new (class extends DefaultStaticProperty<AspectWithState, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithState';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'status',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultState({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),new ArrayList<Value>(){{add({
metaModelBaseAttributes : {},
value : "ReplacedAspectArtifact",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});add({
metaModelBaseAttributes : {},
value : "In Progress",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});add({
metaModelBaseAttributes : {},
value : "Success",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}},{
metaModelBaseAttributes : {},
value : "In Progress",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'status',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithState';
}

getAspectModelUrn(): string {
return MetaAspectWithState .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithState';
}

                        getProperties(): Array<StaticProperty<AspectWithState, any>> {
return [MetaAspectWithState.STATUS];
}

getAllProperties(): Array<StaticProperty<AspectWithState, any>> {
    return this.getProperties();
}




    }


