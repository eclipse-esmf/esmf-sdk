import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithEitherWithComplexTypes,} from './AspectWithEitherWithComplexTypes';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { Either,} from './core/Either';




    

/*
* Generated class MetaAspectWithEitherWithComplexTypes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithComplexTypes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEitherWithComplexTypes implements StaticMetaClass<AspectWithEitherWithComplexTypes>, PropertyContainer<AspectWithEitherWithComplexTypes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEitherWithComplexTypes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEitherWithComplexTypes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEitherWithComplexTypes, Either<ReplacedAspectArtifact, ReplacedAspectArtifact>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithEitherWithComplexTypes';
    }


                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     {
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'TestEither',
preferredNames : [ {
value : "Test Either",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Either Characteristic",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},
}
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithEitherWithComplexTypes';
}

getAspectModelUrn(): string {
return MetaAspectWithEitherWithComplexTypes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEitherWithComplexTypes';
}

                        getProperties(): Array<StaticProperty<AspectWithEitherWithComplexTypes, any>> {
return [MetaAspectWithEitherWithComplexTypes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEitherWithComplexTypes, any>> {
    return this.getProperties();
}




    }


