













import { AspectWithMeasurement,} from './AspectWithMeasurement';
import { StaticUnitProperty,Unit,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithMeasurement (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMeasurement).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMeasurement implements StaticMetaClass<AspectWithMeasurement>, PropertyContainer<AspectWithMeasurement> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMeasurement';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithMeasurement();


 public static readonly  TEST_PROPERTY = 
                
            
        new (class extends StaticUnitProperty<AspectWithMeasurement, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithMeasurement';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultMeasurement({
urn : this.NAMESPACE + 'TestMeasurement',
preferredNames : [ {
value : "Test Measurement",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test Measurement",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),Units.fromName("kelvin"))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithMeasurement';
}

getAspectModelUrn(): string {
return MetaAspectWithMeasurement .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithMeasurement';
}

                        getProperties(): Array<StaticProperty<AspectWithMeasurement, any>> {
return [MetaAspectWithMeasurement.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithMeasurement, any>> {
    return this.getProperties();
}




    }


