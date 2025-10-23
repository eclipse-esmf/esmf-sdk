













import { AspectWithUsedAndUnusedEither,} from './AspectWithUsedAndUnusedEither';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { Either,} from './core/Either';


    

/*
* Generated class MetaAspectWithUsedAndUnusedEither (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithUsedAndUnusedEither).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithUsedAndUnusedEither implements StaticMetaClass<AspectWithUsedAndUnusedEither>, PropertyContainer<AspectWithUsedAndUnusedEither> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithUsedAndUnusedEither';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithUsedAndUnusedEither();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithUsedAndUnusedEither, Either<number, string>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithUsedAndUnusedEither';
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
urn : this.NAMESPACE + 'UsedTestEither',
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
'http://example.com/me',
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
return 'AspectWithUsedAndUnusedEither';
}

getAspectModelUrn(): string {
return MetaAspectWithUsedAndUnusedEither .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithUsedAndUnusedEither';
}

                        getProperties(): Array<StaticProperty<AspectWithUsedAndUnusedEither, any>> {
return [MetaAspectWithUsedAndUnusedEither.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithUsedAndUnusedEither, any>> {
    return this.getProperties();
}




    }


