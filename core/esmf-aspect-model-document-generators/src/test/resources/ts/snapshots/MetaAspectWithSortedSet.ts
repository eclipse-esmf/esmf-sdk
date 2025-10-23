













import { AspectWithSortedSet,} from './AspectWithSortedSet';
import { LangString,} from './core/langString';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithSortedSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSortedSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSortedSet implements StaticMetaClass<AspectWithSortedSet>, PropertyContainer<AspectWithSortedSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSortedSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithSortedSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithSortedSet, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithSortedSet';
    }

        getContainedType(): AspectWithSortedSet {
            return 'AspectWithSortedSet';
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
    characteristic :     new DefaultSortedSet({
urn : this.NAMESPACE + 'TestSortedSet',
preferredNames : [ {
value : "Test Sorted Set",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test sorted set.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" )),Optional.empty())
,
    exampleValue : {
metaModelBaseAttributes : {},
value : "Example Value",
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),
},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithSortedSet';
}

getAspectModelUrn(): string {
return MetaAspectWithSortedSet .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithSortedSet';
}

                        getProperties(): Array<StaticProperty<AspectWithSortedSet, any>> {
return [MetaAspectWithSortedSet.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithSortedSet, any>> {
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


