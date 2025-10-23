













import { AspectWithEitherWithSeeAttribute,} from './AspectWithEitherWithSeeAttribute';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { Either,} from './core/Either';


    

/*
* Generated class MetaAspectWithEitherWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEitherWithSeeAttribute implements StaticMetaClass<AspectWithEitherWithSeeAttribute>, PropertyContainer<AspectWithEitherWithSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEitherWithSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEitherWithSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEitherWithSeeAttribute, Either<number, string>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithEitherWithSeeAttribute';
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
return 'AspectWithEitherWithSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithEitherWithSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEitherWithSeeAttribute';
}

                        getProperties(): Array<StaticProperty<AspectWithEitherWithSeeAttribute, any>> {
return [MetaAspectWithEitherWithSeeAttribute.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEitherWithSeeAttribute, any>> {
    return this.getProperties();
}




    }


