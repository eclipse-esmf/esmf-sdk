













import { AspectWithCustomUnit,} from './AspectWithCustomUnit';
import { StaticUnitProperty,Unit,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCustomUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCustomUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCustomUnit implements StaticMetaClass<AspectWithCustomUnit>, PropertyContainer<AspectWithCustomUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCustomUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCustomUnit();


 public static readonly  TEST_PROPERTY = 
                
            
        new (class extends StaticUnitProperty<AspectWithCustomUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithCustomUnit';
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
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),Optional.of(new DefaultUnit({
urn : this.NAMESPACE + 'normLitrePerMinute',
preferredNames : [ {
value : "norm litre per minute",
languageTag : 'en',
},
 ],
descriptions : [  ],
see : [  ],
},Optional[nl/min],Optional.empty,Optional.empty,Optional.empty,new HashSet<>(){{add(QuantityKinds.VOLUME_FLOW_RATE);}})))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCustomUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithCustomUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCustomUnit';
}

                        getProperties(): Array<StaticProperty<AspectWithCustomUnit, any>> {
return [MetaAspectWithCustomUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithCustomUnit, any>> {
    return this.getProperties();
}




    }


