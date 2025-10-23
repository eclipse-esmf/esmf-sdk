













import { AspectWithEitherWithMultipleSeeAttributes,} from './AspectWithEitherWithMultipleSeeAttributes';
import { DefaultStaticProperty,} from './core/staticConstraintProperty';
import { Either,} from './core/Either';


    

/*
* Generated class MetaAspectWithEitherWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEitherWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEitherWithMultipleSeeAttributes implements StaticMetaClass<AspectWithEitherWithMultipleSeeAttributes>, PropertyContainer<AspectWithEitherWithMultipleSeeAttributes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEitherWithMultipleSeeAttributes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithEitherWithMultipleSeeAttributes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends DefaultStaticProperty<AspectWithEitherWithMultipleSeeAttributes, Either<number, string>>{

    
    getPropertyType(): string {
                return 'Either';
    }

    getContainingType(): string {
        return 'AspectWithEitherWithMultipleSeeAttributes';
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
return 'AspectWithEitherWithMultipleSeeAttributes';
}

getAspectModelUrn(): string {
return MetaAspectWithEitherWithMultipleSeeAttributes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithEitherWithMultipleSeeAttributes';
}

                        getProperties(): Array<StaticProperty<AspectWithEitherWithMultipleSeeAttributes, any>> {
return [MetaAspectWithEitherWithMultipleSeeAttributes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithEitherWithMultipleSeeAttributes, any>> {
    return this.getProperties();
}




    }


