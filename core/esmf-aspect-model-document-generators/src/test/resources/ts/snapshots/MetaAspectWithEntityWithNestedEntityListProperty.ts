













import { AspectWithEntityWithNestedEntityListProperty,} from './AspectWithEntityWithNestedEntityListProperty';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { Entity,} from './Entity';
import { MetaEntity,} from './MetaEntity';


    

/*
* Generated class MetaAspectWithEntityWithNestedEntityListProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityWithNestedEntityListProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityWithNestedEntityListProperty implements StaticMetaClass<AspectWithEntityWithNestedEntityListProperty>, PropertyContainer<AspectWithEntityWithNestedEntityListProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityWithNestedEntityListProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEntityWithNestedEntityListProperty();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEntityWithNestedEntityListProperty, Entity>{

    
    getPropertyType(): string {
                return 'Entity';
    }

    getContainingType(): string {
        return 'AspectWithEntityWithNestedEntityListProperty';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultSingleEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifactCharacteristic',
preferredNames : [ {
value : "Test Entity Characteristic",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test Entity Characteristic",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
}, DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'Entity',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},MetaEntity.INSTANCE.getProperties(),Optional.empty()))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEntityWithNestedEntityListProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithEntityWithNestedEntityListProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEntityWithNestedEntityListProperty';
}

                        getProperties(): Array<StaticProperty<AspectWithEntityWithNestedEntityListProperty, any>> {
return [MetaAspectWithEntityWithNestedEntityListProperty.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEntityWithNestedEntityListProperty, any>> {
    return this.getProperties();
}




    }


