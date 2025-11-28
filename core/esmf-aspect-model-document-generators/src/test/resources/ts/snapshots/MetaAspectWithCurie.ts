













import { AspectWithCurie,} from './AspectWithCurie';
import { DefaultCharacteristic,DefaultScalar,} from './esmf/aspect-meta-model';
import { DefaultScalarValue,} from './esmf/aspect-meta-model/default-scalar-value';
import { DefaultStaticProperty,PropertyContainer,StaticMetaClass,StaticProperty,} from './esmf/aspect-meta-model/staticProperty';
import { KnownVersion,} from './esmf/shared/known-version';


    

/*
* Generated class MetaAspectWithCurie (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCurie).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/





export class MetaAspectWithCurie implements StaticMetaClass<AspectWithCurie>, PropertyContainer<AspectWithCurie> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCurie';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithCurie();


 public static readonly  TEST_CURIE = 
                
        new (class extends DefaultStaticProperty<AspectWithCurie, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithCurie';
    }


    getValue( object : AspectWithCurie) : any {
        return object.testCurie;
    }

        setValue( object : AspectWithCurie, value : any ) {
            object.testCurie = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCurie',
    'testCurie',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#UnitReference',
'UnitReference',
new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#UnitReference';
defaultCharacteristic.addPreferredName('en' , 'Unit Reference');
defaultCharacteristic.addDescription('en' , 'Describes a Property containing a reference to one of the units in the Unit Catalog.');
 return defaultCharacteristic; })()
,
    false,
    false,
    new DefaultScalarValue(new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ),'unit:hectopascal'),
        'testCurie',
    false,
    );




 public static readonly  TEST_CURIE_WITHOUT_EXAMPLE_VALUE = 
                
        new (class extends DefaultStaticProperty<AspectWithCurie, any>{

    
    getPropertyType(): string {
                return 'any';
    }

    getContainingType(): string {
        return 'AspectWithCurie';
    }


    getValue( object : AspectWithCurie) : any {
        return object.testCurieWithoutExampleValue;
    }

        setValue( object : AspectWithCurie, value : any ) {
            object.testCurieWithoutExampleValue = value;
        }

    })(

        KnownVersion.getLatest().toString(),
    this.NAMESPACE + 'AspectWithCurie',
    'testCurieWithoutExampleValue',
    (() => { const defaultCharacteristic = new DefaultCharacteristic(KnownVersion.getLatest().toString(),
this.CHARACTERISTIC_NAMESPACE + '#UnitReference',
'UnitReference',
new DefaultScalar("urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie" ))
defaultCharacteristic.addAspectModelUrn = this.CHARACTERISTIC_NAMESPACE + '#UnitReference';
defaultCharacteristic.addPreferredName('en' , 'Unit Reference');
defaultCharacteristic.addDescription('en' , 'Describes a Property containing a reference to one of the units in the Unit Catalog.');
 return defaultCharacteristic; })()
,
    false,
    false,
    undefined,
        'testCurieWithoutExampleValue',
    false,
    );




getModelClass(): string {
return 'AspectWithCurie';
}

getAspectModelUrn(): string {
return MetaAspectWithCurie .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersion.getLatest()
}

getName(): string {
return 'AspectWithCurie';
}

getProperties(): Array<StaticProperty<AspectWithCurie, any>> {
return [MetaAspectWithCurie.TEST_CURIE, MetaAspectWithCurie.TEST_CURIE_WITHOUT_EXAMPLE_VALUE];
}

getAllProperties(): Array<StaticProperty<AspectWithCurie, any>> {
        return this.getProperties();
}




    }


