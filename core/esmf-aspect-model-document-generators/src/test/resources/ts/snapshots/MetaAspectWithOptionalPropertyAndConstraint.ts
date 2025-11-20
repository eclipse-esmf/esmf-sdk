













import { AspectWithOptionalPropertyAndConstraint,} from './AspectWithOptionalPropertyAndConstraint';
import { DefaultCharacteristic,DefaultLengthConstraint,DefaultScalar,DefaultTrait,} from './aspect-meta-model';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithOptionalPropertyAndConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertyAndConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalPropertyAndConstraint implements StaticMetaClass<AspectWithOptionalPropertyAndConstraint>, PropertyContainer<AspectWithOptionalPropertyAndConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertyAndConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithOptionalPropertyAndConstraint();


 public static readonly  STRING_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithOptionalPropertyAndConstraint, string, string> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithOptionalPropertyAndConstraint';
    }

        getContainedType(): string {
            return 'AspectWithOptionalPropertyAndConstraint';
        }

                                        })(

        null,
    null,
    null,
    (() => { const trait = new DefaultTrait(null, 
null, 
null, 
(() => { const defaultCharacteristic = new DefaultCharacteristic(null, 
null, 
null, 
new DefaultScalar("http://www.w3.org/2001/XMLSchema#string" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#Text';
defaultCharacteristic.addPreferredName('en' , 'Text');
defaultCharacteristic.addDescription('en' , 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.');
 return defaultCharacteristic; })(),[(() => { const lengthConstraint = new DefaultLengthConstraint(null, 
null, 
null, 
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
return KnownVersionUtils.getLatest()
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


