import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithExtendedEnums,} from './AspectWithExtendedEnums';
import { DefaultEntity,DefaultEnumeration,DefaultScalar,} from './aspect-meta-model';
import { DefaultEntityInstance,} from './aspect-meta-model/default-entity-instance';
import { DefaultScalarValue,} from './aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';





    

/*
* Generated class MetaAspectWithExtendedEnums (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExtendedEnums).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithExtendedEnums implements StaticMetaClass<AspectWithExtendedEnums>, PropertyContainer<AspectWithExtendedEnums> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithExtendedEnums';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithExtendedEnums();


 public static readonly  RESULT = 
                
        new (class extends DefaultStaticProperty<AspectWithExtendedEnums, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithExtendedEnums';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultEnumeration = new DefaultEnumeration(null, 
null, 
null, 
[new DefaultEntityInstance(null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evaluation Result');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance(null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evaluation Result');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEntityReplacedAspectArtifact; })(),
undefined),
new DefaultEntityInstance(null, 
(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
MetaReplacedAspectArtifact.INSTANCE.getProperties(),false,
undefined)
defaultEntityReplacedAspectArtifact.addAspectModelUrn = this.NAMESPACE + 'ReplacedAspectArtifact';
defaultEntityReplacedAspectArtifact.addPreferredName('en' , 'Evaluation Result');
defaultEntityReplacedAspectArtifact.addDescription('en' , 'Possible values for the evaluation of a process');
 return defaultEntityReplacedAspectArtifact; })(),
undefined)],(() => { const defaultEntityReplacedAspectArtifact = new DefaultEntity(null, 
null, 
null, 
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
                
        new (class extends DefaultStaticProperty<AspectWithExtendedEnums, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithExtendedEnums';
    }


                                        })(

        null,
    null,
    null,
    (() => { const defaultEnumeration = new DefaultEnumeration(null, 
null, 
null, 
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
return 'AspectWithExtendedEnums';
}

getAspectModelUrn(): string {
return MetaAspectWithExtendedEnums .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithExtendedEnums';
}

                        getProperties(): Array<StaticProperty<AspectWithExtendedEnums, any>> {
return [MetaAspectWithExtendedEnums.RESULT, MetaAspectWithExtendedEnums.SIMPLE_RESULT];
}

getAllProperties(): Array<StaticProperty<AspectWithExtendedEnums, any>> {
    return this.getProperties();
}




    }


