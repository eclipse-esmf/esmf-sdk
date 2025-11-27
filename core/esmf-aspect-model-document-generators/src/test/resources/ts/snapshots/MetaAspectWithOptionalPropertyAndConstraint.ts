













import { AspectWithOptionalPropertyAndConstraint,} from './AspectWithOptionalPropertyAndConstraint';
import { DefaultCharacteristic,DefaultLengthConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { KnownVersion,} from './esmf/shared/known-version';
import { PropertyContainer,StaticContainerProperty,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';


    

/*
* Generated class MetaAspectWithOptionalPropertyAndConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertyAndConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithOptionalPropertyAndConstraint implements StaticMetaClass<AspectWithOptionalPropertyAndConstraint>, PropertyContainer<AspectWithOptionalPropertyAndConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertyAndConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalPropertyAndConstraint();


 public static readonly  STRING_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertyAndConstraint, string, string> {

    
    getPropertyType(): string {
            return 'string';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertyAndConstraint';
    }

        getContainedType(): string {
            return 'AspectWithOptionalPropertyAndConstraint';
        }

        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithOptionalPropertyAndConstraint',
    'stringProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestLengthConstraint',
'TestLengthConstraint',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#Text',
'Text',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
undefined,undefined,)
lengthConstraint.isAnonymousNode = true;
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestLengthConstraint';
 return trait; })()
,
    false,
    true,
    undefined,
        'stringProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithOptionalPropertyAndConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithOptionalPropertyAndConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithOptionalPropertyAndConstraint';
}

getProperties(): Array<StaticProperty<AspectWithOptionalPropertyAndConstraint, any>> {
return [MetaAspectWithOptionalPropertyAndConstraint.STRING_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithOptionalPropertyAndConstraint, any>> {
        return this.getProperties();
}




    }


