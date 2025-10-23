import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { EntityInstanceTest4,} from './EntityInstanceTest4';
import { MetaTheEntity,} from './MetaTheEntity';



    

/*
* Generated class MetaEntityInstanceTest4 (urn:samm:org.eclipse.esmf.test:1.0.0#EntityInstanceTest4).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaEntityInstanceTest4 implements StaticMetaClass<EntityInstanceTest4>, PropertyContainer<EntityInstanceTest4> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'EntityInstanceTest4';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaEntityInstanceTest4();


 public static readonly  ASPECT_PROPERTY = 
                
        new (class extends DefaultStaticProperty<EntityInstanceTest4, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'EntityInstanceTest4';
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
},new HashMap<Property, Value>() {{}},DefaultEntity.createDefaultEntity({
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
return 'EntityInstanceTest4';
}

getAspectModelUrn(): string {
return MetaEntityInstanceTest4 .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'EntityInstanceTest4';
}

                        getProperties(): Array<StaticProperty<EntityInstanceTest4, any>> {
return [MetaEntityInstanceTest4.ASPECT_PROPERTY];
}

getAllProperties(): Array<StaticProperty<EntityInstanceTest4, any>> {
    return this.getProperties();
}




    }


