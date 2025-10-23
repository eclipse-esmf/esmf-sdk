













import { AspectWithListAndElementConstraint,} from './AspectWithListAndElementConstraint';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithListAndElementConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithListAndElementConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithListAndElementConstraint implements StaticMetaClass<AspectWithListAndElementConstraint>, PropertyContainer<AspectWithListAndElementConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithListAndElementConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithListAndElementConstraint();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithListAndElementConstraint, number, number[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithListAndElementConstraint';
    }

        getContainedType(): AspectWithListAndElementConstraint {
            return 'AspectWithListAndElementConstraint';
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
    characteristic :     new DefaultList({
urn : this.NAMESPACE + 'TestList',
preferredNames : [ {
value : "Test List",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test list.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" )),Optional.of(new DefaultTrait({
isAnonymous : true,
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
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : Float.valueOf( "2.3" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : Float.valueOf( "10.5" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})))
,
    exampleValue : {
metaModelBaseAttributes : {},
value : Float.valueOf( "5.0" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithListAndElementConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithListAndElementConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithListAndElementConstraint';
}

                        getProperties(): Array<StaticProperty<AspectWithListAndElementConstraint, any>> {
return [MetaAspectWithListAndElementConstraint.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithListAndElementConstraint, any>> {
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


