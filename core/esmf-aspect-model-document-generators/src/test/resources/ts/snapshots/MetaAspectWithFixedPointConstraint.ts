













import { AspectWithFixedPointConstraint,} from './AspectWithFixedPointConstraint';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithFixedPointConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithFixedPointConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithFixedPointConstraint implements StaticMetaClass<AspectWithFixedPointConstraint>, PropertyContainer<AspectWithFixedPointConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithFixedPointConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithFixedPointConstraint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithFixedPointConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithFixedPointConstraint';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestTrait',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'Measurement',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),Units.fromName("metrePerSecond")),new ArrayList<Constraint>(){{add(new DefaultFixedPointConstraint({
isAnonymous : true,
preferredNames : [ {
value : "Test Fixed Point",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test fixed point constraint.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},5,3));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithFixedPointConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithFixedPointConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithFixedPointConstraint';
}

                        getProperties(): Array<StaticProperty<AspectWithFixedPointConstraint, any>> {
return [MetaAspectWithFixedPointConstraint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithFixedPointConstraint, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Test Aspect', languageTag: 'en'},
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            {value: 'This is a test description', languageTag: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            "http://example.com/",
        ];
        }

    }


