import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityInstanceWithScalarProperties,} from './AspectWithEntityInstanceWithScalarProperties';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithEntityInstanceWithScalarProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityInstanceWithScalarProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityInstanceWithScalarProperties implements StaticMetaClass<AspectWithEntityInstanceWithScalarProperties>, PropertyContainer<AspectWithEntityInstanceWithScalarProperties> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityInstanceWithScalarProperties';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityInstanceWithScalarProperties();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityInstanceWithScalarProperties, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityInstanceWithScalarProperties';
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
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty()),new ArrayList<Value>(){{add(new DefaultEntityInstance({
urn : this.NAMESPACE + 'ReplacedAspectArtifactInstance',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new HashMap<Property, Value>() {{put(MetaReplacedAspectArtifact.CODE,{
metaModelBaseAttributes : {},
value : Short.parseShort( "3" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#short" ),
});put(MetaReplacedAspectArtifact.DESCRIPTION,{
metaModelBaseAttributes : {},
value : "foo",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}},DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [  ],
descriptions : [  ],
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
return 'AspectWithEntityInstanceWithScalarProperties';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityInstanceWithScalarProperties .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityInstanceWithScalarProperties';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityInstanceWithScalarProperties, any>> {
return [MetaAspectWithEntityInstanceWithScalarProperties.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityInstanceWithScalarProperties, any>> {
    return this.getProperties();
}




    }


