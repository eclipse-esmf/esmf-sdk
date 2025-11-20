import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithTimeSeries,} from './AspectWithTimeSeries';
import { DefaultEntity,DefaultSortedSet,} from './aspect-meta-model';
import { LangString,} from './core/langString';


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

        getContainedType(): string {
            return 'AspectWithTimeSeries';
        }

                                        })(

        null,
    null,
    null,
    (() => { const defaultSortedSet = new DefaultSortedSet(null, 
null, 
null, 
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),true,
undefined)
extendsDefaultEntityReplacedAspectArtifact.addAspectModelUrn = 'urn:samm:org.eclipse.esmf.samm:entity:2.2.0#ReplacedAspectArtifact';
extendsDefaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Time Series Entity');
extendsDefaultEntityReplacedAspectArtifact.addDescription('en' , 'An Entity which represents a key\/value pair. The key is the timestamp when the value was recorded and the value is the value which was recorded.');
 return extendsDefaultEntityReplacedAspectArtifact; })())
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Test Time Series Entity');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'This is a test time series entity.');
defaultEntityReplacedAspectArtifact.addSeeReference('http:\/\/example.com\/');
 return defaultEntityReplacedAspectArtifact; })())
defaultSortedSet.addAspectModelUrn = this.NAMESPACE + 'TestTimeSeries';
defaultSortedSet.addPreferredName('en' , 'Test Time Series');
defaultSortedSet.addDescription('en' , 'This is a test time series.');
defaultSortedSet.addSeeReference('http:\/\/example.com\/');
 return defaultSortedSet; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




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
            new LangString('Test Aspect', 'en'),
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            new LangString('This is a test description', 'en'),
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


