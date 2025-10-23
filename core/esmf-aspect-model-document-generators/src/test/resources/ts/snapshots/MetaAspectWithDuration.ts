













import { AspectWithDuration,} from './AspectWithDuration';
import { StaticUnitProperty,Unit,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithDuration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDuration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDuration implements StaticMetaClass<AspectWithDuration>, PropertyContainer<AspectWithDuration> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDuration';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithDuration();


 public static readonly  TEST_PROPERTY = 
                
            
        new (class extends StaticUnitProperty<AspectWithDuration, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithDuration';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultDuration({
urn : this.NAMESPACE + 'TestDuration',
preferredNames : [ {
value : "Test Duration",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test Duration",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),Units.fromName("kilosecond"))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithDuration';
}

getAspectModelUrn(): string {
return MetaAspectWithDuration .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithDuration';
}

                        getProperties(): Array<StaticProperty<AspectWithDuration, any>> {
return [MetaAspectWithDuration.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithDuration, any>> {
    return this.getProperties();
}




    }


