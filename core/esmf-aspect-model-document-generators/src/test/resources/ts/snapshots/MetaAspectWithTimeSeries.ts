import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithTimeSeries,} from './AspectWithTimeSeries';
import { LangString,} from './core/langString';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { MetaTimeSeriesEntity,} from './MetaTimeSeriesEntity';
import { StaticContainerProperty,} from './core/staticConstraintProperty';



    

/*
* Generated class MetaAspectWithTimeSeries (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTimeSeries).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithTimeSeries implements StaticMetaClass<AspectWithTimeSeries>, PropertyContainer<AspectWithTimeSeries> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTimeSeries';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithTimeSeries();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithTimeSeries, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithTimeSeries';
    }

        getContainedType(): AspectWithTimeSeries {
            return 'AspectWithTimeSeries';
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
urn : this.NAMESPACE + 'TestTimeSeries',
preferredNames : [ {
value : "Test Time Series",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test time series.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},Optional.of(DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "Test Time Series Entity",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test time series entity.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.of(DefaultAbstractEntity.createDefaultAbstractEntity({
urn : 'urn:samm:org.eclipse.esmf.samm:entity:2.2.0#TimeSeriesEntity',
preferredNames : [ {
value : "Time Series Entity",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "An Entity which represents a key/value pair. The key is the timestamp when the value was recorded and the value is the value which was recorded.",
languageTag : 'en',
},
 ],
see : [  ],
},MetaTimeSeriesEntity.INSTANCE.getProperties(),Optional.empty(),List.of(AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#ReplacedAspectArtifact" )))))),Optional.empty())
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithTimeSeries';
}

getAspectModelUrn(): string {
return MetaAspectWithTimeSeries .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithTimeSeries';
}

                        getProperties(): Array<StaticProperty<AspectWithTimeSeries, any>> {
return [MetaAspectWithTimeSeries.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithTimeSeries, any>> {
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


