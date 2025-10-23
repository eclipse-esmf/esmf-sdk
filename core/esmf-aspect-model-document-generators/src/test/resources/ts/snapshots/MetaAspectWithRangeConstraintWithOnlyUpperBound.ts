













import { AspectWithRangeConstraintWithOnlyUpperBound,} from './AspectWithRangeConstraintWithOnlyUpperBound';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { LangString,} from './core/langString';


    

/*
* Generated class MetaAspectWithRangeConstraintWithOnlyUpperBound (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithOnlyUpperBound).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithOnlyUpperBound implements StaticMetaClass<AspectWithRangeConstraintWithOnlyUpperBound>, PropertyContainer<AspectWithRangeConstraintWithOnlyUpperBound> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyUpperBound';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithRangeConstraintWithOnlyUpperBound();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithOnlyUpperBound, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithRangeConstraintWithOnlyUpperBound';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [ {
value : "Test Property",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test property.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
'http://example.com/me',
 ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestRangeConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultMeasurement({
urn : this.NAMESPACE + 'Measurement',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),Units.fromName("metrePerSecond")),new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [ {
value : "Test Range Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test range constraint.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},Optional.empty(),Optional.of({
metaModelBaseAttributes : {
isAnonymous : true,
preferredNames : [ {
value : "Test Range Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test range constraint.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},
value : Float.valueOf( "2.3" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),BoundDefinition.OPEN,BoundDefinition.AT_MOST));}})
,
    exampleValue : {
metaModelBaseAttributes : {},
value : Float.valueOf( "2.0" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithRangeConstraintWithOnlyUpperBound';
}

getAspectModelUrn(): string {
return MetaAspectWithRangeConstraintWithOnlyUpperBound .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithRangeConstraintWithOnlyUpperBound';
}

                        getProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyUpperBound, any>> {
return [MetaAspectWithRangeConstraintWithOnlyUpperBound.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithRangeConstraintWithOnlyUpperBound, any>> {
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


