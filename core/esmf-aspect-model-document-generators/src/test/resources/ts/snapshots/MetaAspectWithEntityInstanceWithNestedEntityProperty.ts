import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEntityInstanceWithNestedEntityProperty,} from './AspectWithEntityInstanceWithNestedEntityProperty';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { MetaNestedEntity,} from './MetaNestedEntity';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';



    

/*
* Generated class MetaAspectWithEntityInstanceWithNestedEntityProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityInstanceWithNestedEntityProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityInstanceWithNestedEntityProperty implements StaticMetaClass<AspectWithEntityInstanceWithNestedEntityProperty>, PropertyContainer<AspectWithEntityInstanceWithNestedEntityProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityInstanceWithNestedEntityProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityInstanceWithNestedEntityProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityInstanceWithNestedEntityProperty, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEntityInstanceWithNestedEntityProperty';
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
});put(MetaReplacedAspectArtifact.NESTED_ENTITY,new DefaultEntityInstance({
urn : this.NAMESPACE + 'NestedEntityInstance',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new HashMap<Property, Value>() {{put(MetaNestedEntity.NESTED_ENTITY_PROPERTY,{
metaModelBaseAttributes : {},
value : "bar",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
});}},DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'NestedEntity',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaNestedEntity.INSTANCE.getProperties(),Optional.empty())));}},DefaultEntity.createDefaultEntity({
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
return 'AspectWithEntityInstanceWithNestedEntityProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityInstanceWithNestedEntityProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityInstanceWithNestedEntityProperty';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityInstanceWithNestedEntityProperty, any>> {
return [MetaAspectWithEntityInstanceWithNestedEntityProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityInstanceWithNestedEntityProperty, any>> {
    return this.getProperties();
}




    }


