













import { AspectWithEitherWithoutSeeAttribute,} from './AspectWithEitherWithoutSeeAttribute';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { Either,} from './core/Either';


    

/*
* Generated class MetaAspectWithEitherWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEitherWithoutSeeAttribute implements StaticMetaClass<AspectWithEitherWithoutSeeAttribute>, PropertyContainer<AspectWithEitherWithoutSeeAttribute> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEitherWithoutSeeAttribute';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEitherWithoutSeeAttribute();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEitherWithoutSeeAttribute, Either<number, string>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithEitherWithoutSeeAttribute';
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
see : [  ],
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
return 'AspectWithEitherWithoutSeeAttribute';
}

getAspectModelUrn(): string {
return MetaAspectWithEitherWithoutSeeAttribute .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEitherWithoutSeeAttribute';
}

                        getProperties(): Array<StaticProperty<AspectWithEitherWithoutSeeAttribute, any>> {
return [MetaAspectWithEitherWithoutSeeAttribute.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEitherWithoutSeeAttribute, any>> {
    return this.getProperties();
}




    }


