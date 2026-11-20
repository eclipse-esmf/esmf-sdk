













import { AspectWithExclusiveRangeConstraint,} from './AspectWithExclusiveRangeConstraint';
import { BoundDefinition,} from './esmf/aspect-meta-model/bound-definition';
import { DefaultCharacteristic,DefaultRangeConstraint,DefaultScalar,DefaultTrait,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithExclusiveRangeConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExclusiveRangeConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithExclusiveRangeConstraint implements StaticMetaClass<AspectWithExclusiveRangeConstraint>, PropertyContainer<AspectWithExclusiveRangeConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithExclusiveRangeConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithExclusiveRangeConstraint();


 public static readonly  FLOAT_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


    getValue( object : AspectWithExclusiveRangeConstraint) : number {
        return object.floatProp;
    }

        setValue( object : AspectWithExclusiveRangeConstraint, value : number ) {
            object.floatProp = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithExclusiveRangeConstraint',
    'floatProp',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'FloatRange',
'FloatRange',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.LESS_THAN,BoundDefinition.GREATER_THAN,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'12.3'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#float" ),'23.45'),)
defaultRangeConstraint.isAnonymousNode = true;
defaultRangeConstraint.addDescription('en' , 'This is a floating range constraint');
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'FloatRange';
 return trait; })()
,
    false,
    false,
    undefined,
        'floatProp',
    false,
    );




 public static readonly  DOUBLE_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


    getValue( object : AspectWithExclusiveRangeConstraint) : number {
        return object.doubleProp;
    }

        setValue( object : AspectWithExclusiveRangeConstraint, value : number ) {
            object.doubleProp = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithExclusiveRangeConstraint',
    'doubleProp',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'DoubleRange',
'DoubleRange',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.LESS_THAN,BoundDefinition.GREATER_THAN,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),'12.3'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#double" ),'23.45'),)
defaultRangeConstraint.isAnonymousNode = true;
defaultRangeConstraint.addDescription('en' , 'This is a double range constraint');
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'DoubleRange';
 return trait; })()
,
    false,
    false,
    undefined,
        'doubleProp',
    false,
    );




 public static readonly  DECIMAL_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


    getValue( object : AspectWithExclusiveRangeConstraint) : string {
        return object.decimalProp;
    }

        setValue( object : AspectWithExclusiveRangeConstraint, value : string ) {
            object.decimalProp = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithExclusiveRangeConstraint',
    'decimalProp',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'DecimalRange',
'DecimalRange',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.LESS_THAN,BoundDefinition.GREATER_THAN,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),'12.3'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#decimal" ),'23.45'),)
defaultRangeConstraint.isAnonymousNode = true;
defaultRangeConstraint.addDescription('en' , 'This is a decimal range constraint');
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'DecimalRange';
 return trait; })()
,
    false,
    false,
    undefined,
        'decimalProp',
    false,
    );




 public static readonly  INTEGER_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, string>{

    
    getPropertyType(): string {
                return 'string';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


    getValue( object : AspectWithExclusiveRangeConstraint) : string {
        return object.integerProp;
    }

        setValue( object : AspectWithExclusiveRangeConstraint, value : string ) {
            object.integerProp = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithExclusiveRangeConstraint',
    'integerProp',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'IntegerRange',
'IntegerRange',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.LESS_THAN,BoundDefinition.GREATER_THAN,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'12'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#integer" ),'23'),)
defaultRangeConstraint.isAnonymousNode = true;
defaultRangeConstraint.addDescription('en' , 'This is a integer range constraint');
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'IntegerRange';
 return trait; })()
,
    false,
    false,
    undefined,
        'integerProp',
    false,
    );




 public static readonly  INT_PROP = 
                
        new (class extends DefaultStaticProperty<AspectWithExclusiveRangeConstraint, number>{

    
    getPropertyType(): string {
                return 'number';
    }

    getContainingType(): string {
        return 'AspectWithExclusiveRangeConstraint';
    }


    getValue( object : AspectWithExclusiveRangeConstraint) : number {
        return object.intProp;
    }

        setValue( object : AspectWithExclusiveRangeConstraint, value : number ) {
            object.intProp = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithExclusiveRangeConstraint',
    'intProp',
    (() => { const trait = new DefaultTrait(KnownVersion.getLatest().toString(),
this.NAMESPACE + 'IntRange',
'IntRange',
(() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ))
defaultCharacteristic.isAnonymousNode = true;
 return defaultCharacteristic; })(),[(() => { const defaultRangeConstraint = new DefaultRangeConstraint(KnownVersion.getLatest().toString(),
'urn:samm:anonymous.elements:0.0.0#ReplacedAspectArtifact',
'ReplacedAspectArtifact',
BoundDefinition.LESS_THAN,BoundDefinition.GREATER_THAN,new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'12'),new DefaultScalarValue(new DefaultScalar("http://www.w3.org/2001/XMLSchema#int" ),'23'),)
defaultRangeConstraint.isAnonymousNode = true;
 return defaultRangeConstraint; })()])
trait.addAspectModelUrn = this.NAMESPACE + 'IntRange';
 return trait; })()
,
    false,
    false,
    undefined,
        'intProp',
    false,
    );




getModelClass(): string {
return 'AspectWithExclusiveRangeConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithExclusiveRangeConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithExclusiveRangeConstraint';
}

getProperties(): Array<StaticProperty<AspectWithExclusiveRangeConstraint, any>> {
return [MetaAspectWithExclusiveRangeConstraint.FLOAT_PROP, MetaAspectWithExclusiveRangeConstraint.DOUBLE_PROP, MetaAspectWithExclusiveRangeConstraint.DECIMAL_PROP, MetaAspectWithExclusiveRangeConstraint.INTEGER_PROP, MetaAspectWithExclusiveRangeConstraint.INT_PROP];
}

getAllProperties(): Array<StaticProperty<AspectWithExclusiveRangeConstraint, any>> {
        return this.getProperties();
}




    }


