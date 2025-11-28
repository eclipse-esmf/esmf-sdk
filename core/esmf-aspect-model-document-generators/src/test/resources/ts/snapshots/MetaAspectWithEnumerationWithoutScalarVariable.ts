import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEnumerationWithoutScalarVariable,} from './AspectWithEnumerationWithoutScalarVariable';
import { DefaultEntity,DefaultEntityInstance,DefaultEnumeration,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';

import { KnownVersion,} from './esmf/shared/known-version';



    

/*
* Generated class MetaAspectWithEnumerationWithoutScalarVariable (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumerationWithoutScalarVariable).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithEnumerationWithoutScalarVariable implements StaticMetaClass<AspectWithEnumerationWithoutScalarVariable>, PropertyContainer<AspectWithEnumerationWithoutScalarVariable> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumerationWithoutScalarVariable';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEnumerationWithoutScalarVariable();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEnumerationWithoutScalarVariable, ReplacedAspectArtifact>{

    
    getPropertyType(): string {
                return 'ReplacedAspectArtifact';
    }

    getContainingType(): string {
        return 'AspectWithEnumerationWithoutScalarVariable';
    }


    getValue( object : AspectWithEnumerationWithoutScalarVariable) : ReplacedAspectArtifact {
        return object.testProperty;
    }

        setValue( object : AspectWithEnumerationWithoutScalarVariable, value : ReplacedAspectArtifact ) {
            object.testProperty = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithEnumerationWithoutScalarVariable',
    'testProperty',
    (() => { const defaultEnumeration = new DefaultEnumeration(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'ReplacedAspectArtifact',
'ReplacedAspectArtifact',
[new DefaultEntityInstance('ResultGood',
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
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithEnumerationWithoutScalarVariable';
}

getAspectModelUrn(): string {
return MetaAspectWithEnumerationWithoutScalarVariable .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithEnumerationWithoutScalarVariable';
}

getProperties(): Array<StaticProperty<AspectWithEnumerationWithoutScalarVariable, any>> {
return [MetaAspectWithEnumerationWithoutScalarVariable.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEnumerationWithoutScalarVariable, any>> {
        return this.getProperties();
}




    }


