













import { AspectWithCustomUnitAndQuantityKind,} from './AspectWithCustomUnitAndQuantityKind';
import { StaticUnitProperty,Unit,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithCustomUnitAndQuantityKind (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCustomUnitAndQuantityKind).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCustomUnitAndQuantityKind implements StaticMetaClass<AspectWithCustomUnitAndQuantityKind>, PropertyContainer<AspectWithCustomUnitAndQuantityKind> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCustomUnitAndQuantityKind';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCustomUnitAndQuantityKind();


 public static readonly  EMISSIONS = 
                
            
        new (class extends StaticUnitProperty<AspectWithCustomUnitAndQuantityKind, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithCustomUnitAndQuantityKind';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'emissions',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultQuantifiable({
urn : this.NAMESPACE + 'Emissions',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),Optional.of(new DefaultUnit({
urn : this.NAMESPACE + 'gCO2eqPerkWh',
preferredNames : [ {
value : "gram CO₂ equivalent per kWh",
languageTag : 'en',
},
 ],
descriptions : [  ],
see : [  ],
},Optional[gCO₂eq/kWh],Optional.empty,Optional.empty,Optional.empty,new HashSet<>(){{add(new DefaultQuantityKind({
urn : this.NAMESPACE + 'greenhouseGasEmissions',
preferredNames : [ {
value : "greenhouse gas emissions",
languageTag : 'en',
},
 ],
descriptions : [  ],
see : [  ],
},"greenhouse gas emissions"));}})))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'emissions',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithCustomUnitAndQuantityKind';
}

getAspectModelUrn(): string {
return MetaAspectWithCustomUnitAndQuantityKind .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithCustomUnitAndQuantityKind';
}

                        getProperties(): Array<StaticProperty<AspectWithCustomUnitAndQuantityKind, any>> {
return [MetaAspectWithCustomUnitAndQuantityKind.EMISSIONS];
}

getAllProperties(): Array<StaticProperty<AspectWithCustomUnitAndQuantityKind, any>> {
    return this.getProperties();
}




    }


