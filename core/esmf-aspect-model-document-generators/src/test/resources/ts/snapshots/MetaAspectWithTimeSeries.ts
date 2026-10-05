import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithTimeSeries,} from './AspectWithTimeSeries';
import { DefaultEntity,DefaultSortedSet,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';


import { MultiLanguageText,} from './esmf/instantiator/characteristic/characteristic-instantiator-util';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';



    

/*
* Generated class MetaAspectWithTimeSeries (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTimeSeries).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithTimeSeries implements StaticMetaClass<AspectWithTimeSeries>, PropertyContainer<AspectWithTimeSeries> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTimeSeries';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithTimeSeries();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithTimeSeries, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithTimeSeries';
    }

        getContainedType(): string {
            return 'ReplacedAspectArtifact';
        }

    getValue( object : AspectWithTimeSeries) : ReplacedAspectArtifact[] {
        return object.testProperty;
    }

        setValue( object : AspectWithTimeSeries, value : ReplacedAspectArtifact[] ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithTimeSeries',
    'testProperty',
    (() => { const defaultSortedSet = new DefaultSortedSet(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestTimeSeries',
'TestTimeSeries',
undefined,
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
(() => { const extendsDefaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
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
return KnownVersion.getLatest()
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

        
    getPreferredNames(): Array<MultiLanguageText> {
        return [
            {value: 'Test Aspect', language: 'en'},
        ];
        }

        
        getDescriptions(): Array<MultiLanguageText> {
        return [
            {value: 'This is a test description', language: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            'http:\/\/example.com\/',
        ];
        }

    }


