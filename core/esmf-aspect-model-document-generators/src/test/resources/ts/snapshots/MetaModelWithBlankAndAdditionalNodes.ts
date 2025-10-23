













import { ModelWithBlankAndAdditionalNodes,} from './ModelWithBlankAndAdditionalNodes';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaModelWithBlankAndAdditionalNodes (urn:samm:org.eclipse.esmf.test:1.0.0#ModelWithBlankAndAdditionalNodes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaModelWithBlankAndAdditionalNodes implements StaticMetaClass<ModelWithBlankAndAdditionalNodes>, PropertyContainer<ModelWithBlankAndAdditionalNodes> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'ModelWithBlankAndAdditionalNodes';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaModelWithBlankAndAdditionalNodes();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<ModelWithBlankAndAdditionalNodes, string, string[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'ModelWithBlankAndAdditionalNodes';
    }

        getContainedType(): ModelWithBlankAndAdditionalNodes {
            return 'ModelWithBlankAndAdditionalNodes';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
    characteristic :     new DefaultList({
urn : this.NAMESPACE + 'NumberList',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of(new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" )),Optional.of(new DefaultTrait({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},{
metaModelBaseAttributes : {
urn : this.NAMESPACE + 'Number',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},
},new ArrayList<Constraint>(){{add(new DefaultRangeConstraint({
isAnonymous : true,
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},Optional.of({
metaModelBaseAttributes : {},
value : new BigInteger( "5" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),
}),Optional.of({
metaModelBaseAttributes : {},
value : new BigInteger( "10" ),
type : new DefaultScalar("http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ),
}),BoundDefinition.AT_LEAST,BoundDefinition.AT_MOST));}})))
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'ModelWithBlankAndAdditionalNodes';
}

getAspectModelUrn(): string {
return MetaModelWithBlankAndAdditionalNodes .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'ModelWithBlankAndAdditionalNodes';
}

                        getProperties(): Array<StaticProperty<ModelWithBlankAndAdditionalNodes, any>> {
return [MetaModelWithBlankAndAdditionalNodes.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<ModelWithBlankAndAdditionalNodes, any>> {
    return this.getProperties();
}




    }


