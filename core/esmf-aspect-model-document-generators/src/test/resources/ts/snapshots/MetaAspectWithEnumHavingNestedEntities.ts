import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumHavingNestedEntities,} from './AspectWithEnumHavingNestedEntities';
import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';

import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithEnumHavingNestedEntities (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumHavingNestedEntities).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEnumHavingNestedEntities implements StaticMetaClass<AspectWithEnumHavingNestedEntities>, PropertyContainer<AspectWithEnumHavingNestedEntities> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumHavingNestedEntities';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumHavingNestedEntities();


 public static readonly  RESULT = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumHavingNestedEntities, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumHavingNestedEntities';
    }


    getValue( object : AspectWithEnumHavingNestedEntities) : ReplacedAspectArtifact {
        return object.result;
    }

        setValue( object : AspectWithEnumHavingNestedEntities, value : ReplacedAspectArtifact ) {
            object.result = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumHavingNestedEntities',
    'result',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultEntityInstance('ResultBad',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evalution Result');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance('ResultGood',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evalution Result');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEntityReplacedAspectArtifact; })(),
undefined)],(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evalution Result');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEntityReplacedAspectArtifact; })())
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addPreferredName('en' , 'Evaluation Results');
defaultEnumeration.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'result',
    false,
    );




 public static readonly  SIMPLE_RESULT = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumHavingNestedEntities, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumHavingNestedEntities';
    }


    getValue( object : AspectWithEnumHavingNestedEntities) : ReplacedAspectArtifact {
        return object.simpleResult;
    }

        setValue( object : AspectWithEnumHavingNestedEntities, value : ReplacedAspectArtifact ) {
            object.simpleResult = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumHavingNestedEntities',
    'simpleResult',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'No'),
new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ),'Yes')],new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultEnumeration.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEnumeration.addPreferredName('en' , 'ReplacedAspectArtifact Result');
 return defaultEnumeration; })()
,
    false,
    false,
    undefined,
        'simpleResult',
    false,
    );




getModelClass(): string {
return 'AspectWithEnumHavingNestedEntities';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumHavingNestedEntities .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEnumHavingNestedEntities';
}

getProperties(): Array<StaticProperty<AspectWithEnumHavingNestedEntities, any>> {
return [MetaAspectWithEnumHavingNestedEntities.RESULT, MetaAspectWithEnumHavingNestedEntities.SIMPLE_RESULT];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumHavingNestedEntities, any>> {
        return this.getProperties();
}




    }


