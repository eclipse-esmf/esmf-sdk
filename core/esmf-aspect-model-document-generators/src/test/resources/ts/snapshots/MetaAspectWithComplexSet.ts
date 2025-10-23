import { ReplacedAspectArtifact,} from './ReplacedAspectArtifact';














import { AspectWithComplexSet,} from './AspectWithComplexSet';

import { LangString,} from './core/langString';
import { MetaReplacedAspectArtifact,} from './MetaReplacedAspectArtifact';
import { StaticContainerProperty,} from './core/staticConstraintProperty';


    

/*
* Generated class MetaAspectWithComplexSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithComplexSet implements StaticMetaClass<AspectWithComplexSet>, PropertyContainer<AspectWithComplexSet> {
 public static readonly  NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
 public static readonly  MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithComplexSet';

private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

 public static readonly  INSTANCE = new MetaAspectWithComplexSet();


 public static readonly  TEST_PROPERTY = 
                
        new (class extends StaticContainerProperty<AspectWithComplexSet, ReplacedAspectArtifact, ReplacedAspectArtifact[]> {

    
    getPropertyType(): string {
            return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
    }

    getContainingType(): string {
        return 'AspectWithComplexSet';
    }

        getContainedType(): AspectWithComplexSet {
            return 'AspectWithComplexSet';
        }

                                        })(
        {
        metaModelBaseAttributes : {
urn : this.NAMESPACE + 'testProperty',
preferredNames : [ {
value : "Test Property",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test property.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},
    characteristic :     new DefaultTrait({
urn : this.NAMESPACE + 'TestTrait',
preferredNames : [  ],
descriptions : [  ],
see : [  ],
},new DefaultSet({
urn : this.NAMESPACE + 'TestSet',
preferredNames : [ {
value : "Test Set",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "This is a test set.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},Optional.of(DefaultEntity.createDefaultEntity({
urn : this.NAMESPACE + 'ReplacedAspectArtifact',
preferredNames : [ {
value : "Unique ReplacedAspectArtifactentifier",
languageTag : 'en',
},
 ],
descriptions : [  ],
see : [  ],
},MetaReplacedAspectArtifact.INSTANCE.getProperties(),Optional.empty())),Optional.empty()),new ArrayList<Constraint>(){{add(new DefaultLengthConstraint({
urn : this.NAMESPACE + 'TestSetConstraint',
preferredNames : [ {
value : "TestSet Constraint",
languageTag : 'en',
},
 ],
descriptions : [ {
value : "Constraint for defining a non-empty set of identifiers.",
languageTag : 'en',
},
 ],
see : [ 'http://example.com/',
 ],
},Optional.of(new BigInteger( "2" )),Optional.empty()));}})
,
    exampleValue : {},
    optional : false,
    notInPayload : false,
        payloadName : 'testProperty',
    isAbstract : false,
    });




getModelClass(): string {
return 'AspectWithComplexSet';
}

getAspectModelUrn(): string {
return MetaAspectWithComplexSet .MODEL_ELEMENT_URN;
}

getMetaModelVersion(): KnownVersion {
return KnownVersionUtils.getLatest()
}

getName(): string {
return 'AspectWithComplexSet';
}

                        getProperties(): Array<StaticProperty<AspectWithComplexSet, any>> {
return [MetaAspectWithComplexSet.TEST_PROPERTY];
}

getAllProperties(): Array<StaticProperty<AspectWithComplexSet, any>> {
    return this.getProperties();
}

        
    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Test Aspect', languageTag: 'en'},
        ];
        }

        
        getDescriptions(): Array<LangString> {
        return [
            {value: 'This is a test description', languageTag: 'en'},
        ];
        }

        getSee(): Array<String> {
        return [
            "http://example.com/",
        ];
        }

    }


