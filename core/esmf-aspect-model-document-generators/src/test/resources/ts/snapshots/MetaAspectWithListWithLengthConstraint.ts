













import { AspectWithListWithLengthConstraint,} from './AspectWithListWithLengthConstraint';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithListWithLengthConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithListWithLengthConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithListWithLengthConstraint implements StaticMetaClass<AspectWithListWithLengthConstraint>, PropertyContainer<AspectWithListWithLengthConstraint> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithListWithLengthConstraint';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithListWithLengthConstraint();


 public static readonly  TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT = 
                
        new (class extends StaticContainerProperty<AspectWithListWithLengthConstraint, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithListWithLengthConstraint';
    }

        getContainedType(): AspectWithListWithLengthConstraint {
            return 'AspectWithListWithLengthConstraint';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testPropertyCollectionLengthConstraint',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestLengthConstraintWithCollection',
preferredNames : [ {
value : "Test Length Constraint with collection",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Test Length Constraint with collection",
languageTag : 'en',
},
 ],
see : [  ],
},new DefaultList({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" )),Optional.empty()),new ArrayList<Constraint>(){{add(new DefaultLengthConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new BigInteger( "1" )),Optional.of(new BigInteger( "10" ))));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testPropertyCollectionLengthConstraint',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithListWithLengthConstraint';
}

getAspectModelUrn(): string {
return MetaAspectWithListWithLengthConstraint .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithListWithLengthConstraint';
}

                        getProperties(): Array<StaticProperty<AspectWithListWithLengthConstraint, any>> {
return [MetaAspectWithListWithLengthConstraint.TEST_PROPERTY_COLLECTION_LENGTH_CONSTRAINT];
}

getAllProperties(): Array<StaticProperty<AspectWithListWithLengthConstraint, any>> {
    return this.getProperties();
}




    }


