













import { AspectWithQuantifiableAndUnit,} from './AspectWithQuantifiableAndUnit';
import { StaticUnitProperty,Unit,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithQuantifiableAndUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableAndUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithQuantifiableAndUnit implements StaticMetaClass<AspectWithQuantifiableAndUnit>, PropertyContainer<AspectWithQuantifiableAndUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithQuantifiableAndUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithQuantifiableAndUnit();


 public static readonly  TEST_PROPERTY = 
                
            
        new (class extends StaticUnitProperty<AspectWithQuantifiableAndUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithQuantifiableAndUnit';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultQuantifiable({
urn : this.NAMESPACE + 'TestQuantifiable',
preferredNames : [ {
value : "Test Quantifiable",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test Quantifiable",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),Units.fromName("hertz"))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithQuantifiableAndUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithQuantifiableAndUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithQuantifiableAndUnit';
}

                        getProperties(): Array<StaticProperty<AspectWithQuantifiableAndUnit, any>> {
return [MetaAspectWithQuantifiableAndUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithQuantifiableAndUnit, any>> {
    return this.getProperties();
}




    }


