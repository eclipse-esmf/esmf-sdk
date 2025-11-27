import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithExtendedEnumsWithNotInPayloadProperty,} from './AspectWithExtendedEnumsWithNotInPayloadProperty';
import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';

import { KnownVersion,} from './esmf/shared/known-version';




    

/*
* Generated class MetaAspectWithExtendedEnumsWithNotInPayloadProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExtendedEnumsWithNotInPayloadProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithExtendedEnumsWithNotInPayloadProperty implements StaticMetaClass<AspectWithExtendedEnumsWithNotInPayloadProperty>, PropertyContainer<AspectWithExtendedEnumsWithNotInPayloadProperty> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithExtendedEnumsWithNotInPayloadProperty';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithExtendedEnumsWithNotInPayloadProperty();


 public static readonly  RESULT = 
                
        new (class extends DefaultStaticProperty<AspectWithExtendedEnumsWithNotInPayloadProperty, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithExtendedEnumsWithNotInPayloadProperty';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithExtendedEnumsWithNotInPayloadProperty',
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
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evaluation Result');
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
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evaluation Result');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance('ResultNoStatus',
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evaluation Result');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEntityReplacedAspectArtifact; })(),
undefined)],(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evaluation Result');
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
                
        new (class extends DefaultStaticProperty<AspectWithExtendedEnumsWithNotInPayloadProperty, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithExtendedEnumsWithNotInPayloadProperty';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithExtendedEnumsWithNotInPayloadProperty',
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
return 'AspectWithExtendedEnumsWithNotInPayloadProperty';
}

getAspectModelUrn(): string {
return MetaAspectWithExtendedEnumsWithNotInPayloadProperty .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithExtendedEnumsWithNotInPayloadProperty';
}

getProperties(): Array<StaticProperty<AspectWithExtendedEnumsWithNotInPayloadProperty, any>> {
return [MetaAspectWithExtendedEnumsWithNotInPayloadProperty.RESULT, MetaAspectWithExtendedEnumsWithNotInPayloadProperty.SIMPLE_RESULT];
}

getAllProperties(): Array<StaticProperty<AspectWithExtendedEnumsWithNotInPayloadProperty, any>> {
        return this.getProperties();
}




    }


