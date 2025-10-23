













import { AspectWithQuantifiableWithUnit,} from './AspectWithQuantifiableWithUnit';
import { StaticUnitProperty,Unit,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithQuantifiableWithUnit (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithQuantifiableWithUnit).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithQuantifiableWithUnit implements StaticMetaClass<AspectWithQuantifiableWithUnit>, PropertyContainer<AspectWithQuantifiableWithUnit> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithQuantifiableWithUnit';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithQuantifiableWithUnit();


 public static readonly  TEST_PROPERTY = 
                
            
        new (class extends StaticUnitProperty<AspectWithQuantifiableWithUnit, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithQuantifiableWithUnit';
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
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),Units.fromName("percent"))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithQuantifiableWithUnit';
}

getAspectModelUrn(): string {
return MetaAspectWithQuantifiableWithUnit .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithQuantifiableWithUnit';
}

                        getProperties(): Array<StaticProperty<AspectWithQuantifiableWithUnit, any>> {
return [MetaAspectWithQuantifiableWithUnit.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithQuantifiableWithUnit, any>> {
    return this.getProperties();
}




    }


