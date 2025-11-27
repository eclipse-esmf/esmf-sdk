













import { AspectWithConstraintWithoutSeeAttribute,} from './AspectWithConstraintWithoutSeeAttribute';
import { DefaultCharacteristic,DefaultLengthConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithConstraintWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraintWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithConstraintWithoutSeeAttribute implements StaticMetaClass<AspectWithConstraintWithoutSeeAttribute>, PropertyContainer<AspectWithConstraintWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraintWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithConstraintWithoutSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithConstraintWithoutSeeAttribute, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithConstraintWithoutSeeAttribute';
    }


        })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithConstraintWithoutSeeAttribute',
    'testProperty',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'TestTrait',
'TestTrait',
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
5,5,)
lengthConstraint.isAnonymousNode = true;
lengthConstraint.addPreferredName('en' , 'Test Constraint');
lengthConstraint.addDescription('en' , 'Test Constraint');
 return lengthConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'TestTrait';
 return trait; })()
,
    false,
    false,
    undefined,
        'testProperty',
    false,
    );




getModelClass(): string {
return 'AspectWithConstraintWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithConstraintWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithConstraintWithoutSeeAttribute';
}

getProperties(): Array<StaticProperty<AspectWithConstraintWithoutSeeAttribute, any>> {
return [MetaAspectWithConstraintWithoutSeeAttribute.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithConstraintWithoutSeeAttribute, any>> {
        return this.getProperties();
}




    }


