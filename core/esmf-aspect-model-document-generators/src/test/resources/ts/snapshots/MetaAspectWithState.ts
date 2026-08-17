import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithState,} from './AspectWithState';
import { DefaultScalar,DefaultState,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';



    

/*
* Generated class MetaAspectWithState (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithState).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





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


    getValue( object : AspectWithState) : ReplacedAspectArtifact {
        return object.status;
    }

        setValue( object : AspectWithState, value : ReplacedAspectArtifact ) {
            object.status = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithState',
    'status',
    (() => { const defaultState = new DefaultState(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Error'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'In Progress'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Success')],new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'In Progress'),new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultState.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
 return defaultState; })()
,
    false,
    false,
    undefined,
        'status',
    false,
    );




getModelClass(): string {
return 'AspectWithState';
}

getAspectModelUrn(): string {
return MetaAspectWithState .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
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


