import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { EntityInstanceTest1,} from './EntityInstanceTest1';
import { MetaTheEntity,} from './MetaTheEntity';



    

/*
* Generated class MetaEntityInstanceTest1 (urn:samm:org.eclipse.esmf.test:1.0.0#EntityInstanceTest1).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaEntityInstanceTest1 implements StaticMetaClass<EntityInstanceTest1>, PropertyContainer<EntityInstanceTest1> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'EntityInstanceTest1';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaEntityInstanceTest1();


 public static readonly  ASPECT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<EntityInstanceTest1, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'EntityInstanceTest1';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'aspectProperty',
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
urn : this.NAMESPACE + 'TheEntity',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaTheEntity.INSTANCE.getProperties(),Optional.empty()),new ArrayList<Value>(){{add(new DefaultEntityInstance({
urn : this.NAMESPACE + 'entityInstance',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new HashMap<Property, Value>() {{put(MetaTheEntity.ENTITY_PROPERTY,{
metaModelBaseAttributes : {},
value : "Test",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}},DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'TheEntity',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaTheEntity.INSTANCE.getProperties(),Optional.empty())));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'aspectProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'EntityInstanceTest1';
}

getAspectModelUrn(): string {
return MetaEntityInstanceTest1 .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'EntityInstanceTest1';
}

                        getProperties(): Array<StaticProperty<EntityInstanceTest1, any>> {
return [MetaEntityInstanceTest1.ASPECT_PROPERTY];
}

getAllProperties(): Array<StaticProperty<EntityInstanceTest1, any>> {
    return this.getProperties();
}




    }


