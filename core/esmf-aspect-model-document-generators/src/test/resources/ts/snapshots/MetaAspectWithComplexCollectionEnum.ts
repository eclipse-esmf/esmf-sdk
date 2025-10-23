/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

import { ReplacedAspectArtifact, } from './ReplacedAspectArtifact';


import { AspectWithComplexCollectionEnum, } from './AspectWithComplexCollectionEnum';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { MetaMyEntityFour, } from './MetaMyEntityFour';
import { MetaMyEntityOne, } from './MetaMyEntityOne';
import { MetaMyEntityThree, } from './MetaMyEntityThree';
import { MetaMyEntityTwo, } from './MetaMyEntityTwo';


import { string, } from 'asdas';


/*
* Generated class MetaAspectWithComplexCollectionEnum (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexCollectionEnum).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithComplexCollectionEnum implements StaticMetaClass<AspectWithComplexCollectionEnum>, PropertyContainer<AspectWithComplexCollectionEnum> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithComplexCollectionEnum';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithComplexCollectionEnum();


    public static readonly MY_PROPERTY_ONE =

        new (class extends DefaultStaticProperty<AspectWithComplexCollectionEnum, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithComplexCollectionEnum';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'myPropertyOne',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultEnumeration({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'MyEntityOne',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaMyEntityOne.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>() { {
    add(

    new;

    DefaultEntityInstance({
                              urn: this.NAMESPACE

+
    'entityInstanceOne';
,
    preferredNames: [];
,
    descriptions: [];
,
    see: [];
,
}

,
new HashMap<Property, Value>();
{
    {
        put(MetaMyEntityOne.ENTITY_PROPERTY_ONE, new DefaultCollectionValue(new ArrayList<>()
        {
            {
                add({
                    metaModelBaseAttributes: {},
                    value: 'fooOne',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
                add({
                    metaModelBaseAttributes: {},
                    value: 'barOne',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
                add({
                    metaModelBaseAttributes: {},
                    value: 'bazOne',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
            }
        }
    ,
        org.eclipse.esmf.metamodel.CollectionValue.CollectionType.LIST, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string');
    ))

    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'MyEntityOne',
    preferredNames: [],
    descriptions: [],
    see: [],
}, MetaMyEntityOne.INSTANCE.getProperties(), Optional.empty());
))

}
})
,
{
}
,
false,
    notInPayload;
:
false,
    payloadName;
:
'myPropertyOne',
    isAbstract;
:
false,
})



public static readonly
MY_PROPERTY_TWO =

    new (class extends DefaultStaticProperty<AspectWithComplexCollectionEnum, ReplacedAspectArtifact> {


        getPropertyType(): string {
            return 'ReplacedAspectArtifact';
        }

        getContainingType(): string {
            return 'AspectWithComplexCollectionEnum';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'myPropertyTwo',
                preferredNames: [],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultEnumeration({
                urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, DefaultEntity.createDefaultEntity({
                urn: this.NAMESPACE + 'MyEntityTwo',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, MetaMyEntityTwo.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>()
{
    {
        add(new DefaultEntityInstance({
            urn: this.NAMESPACE + 'entityInstanceTwo',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaMyEntityTwo.ENTITY_PROPERTY_TWO, new DefaultCollectionValue(new LinkedHashSet<>()
                {
                    {
                        add({
                            metaModelBaseAttributes: {},
                            value: 'fooTwo',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                        add({
                            metaModelBaseAttributes: {},
                            value: 'barTwo',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                        add({
                            metaModelBaseAttributes: {},
                            value: 'bazTwo',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                    }
                }
            ,
                org.eclipse.esmf.metamodel.CollectionValue.CollectionType.SET, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string');
            ))

            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'MyEntityTwo',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, MetaMyEntityTwo.INSTANCE.getProperties(), Optional.empty());
    ))

    }
}
)
,
{
}
,
false,
    notInPayload;
:
false,
    payloadName;
:
'myPropertyTwo',
    isAbstract;
:
false,
})



public static readonly
MY_PROPERTY_THREE =

    new (class extends DefaultStaticProperty<AspectWithComplexCollectionEnum, ReplacedAspectArtifact> {


        getPropertyType(): string {
            return 'ReplacedAspectArtifact';
        }

        getContainingType(): string {
            return 'AspectWithComplexCollectionEnum';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'myPropertyThree',
                preferredNames: [],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultEnumeration({
                urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, DefaultEntity.createDefaultEntity({
                urn: this.NAMESPACE + 'MyEntityThree',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, MetaMyEntityThree.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>()
{
    {
        add(new DefaultEntityInstance({
            urn: this.NAMESPACE + 'entityInstanceThree',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaMyEntityThree.ENTITY_PROPERTY_THREE, new DefaultCollectionValue(new LinkedHashSet<>()
                {
                    {
                        add({
                            metaModelBaseAttributes: {},
                            value: 'fooThree',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                        add({
                            metaModelBaseAttributes: {},
                            value: 'barThree',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                        add({
                            metaModelBaseAttributes: {},
                            value: 'bazThree',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                    }
                }
            ,
                org.eclipse.esmf.metamodel.CollectionValue.CollectionType.SORTEDSET, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string');
            ))

            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'MyEntityThree',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, MetaMyEntityThree.INSTANCE.getProperties(), Optional.empty());
    ))

    }
}
)
,
{
}
,
false,
    notInPayload;
:
false,
    payloadName;
:
'myPropertyThree',
    isAbstract;
:
false,
})



public static readonly
MY_PROPERTY_FOUR =

    new (class extends DefaultStaticProperty<AspectWithComplexCollectionEnum, ReplacedAspectArtifact> {


        getPropertyType(): string {
            return 'ReplacedAspectArtifact';
        }

        getContainingType(): string {
            return 'AspectWithComplexCollectionEnum';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'myPropertyFour',
                preferredNames: [],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultEnumeration({
                urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, DefaultEntity.createDefaultEntity({
                urn: this.NAMESPACE + 'MyEntityFour',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, MetaMyEntityFour.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>()
{
    {
        add(new DefaultEntityInstance({
            urn: this.NAMESPACE + 'entityInstanceFour',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaMyEntityFour.ENTITY_PROPERTY_FOUR, new DefaultCollectionValue(new ArrayList<>()
                {
                    {
                        add({
                            metaModelBaseAttributes: {},
                            value: 'fooFour',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                        add({
                            metaModelBaseAttributes: {},
                            value: 'barFour',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                        add({
                            metaModelBaseAttributes: {},
                            value: 'bazFour',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                    }
                }
            ,
                org.eclipse.esmf.metamodel.CollectionValue.CollectionType.COLLECTION, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string');
            ))

            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'MyEntityFour',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, MetaMyEntityFour.INSTANCE.getProperties(), Optional.empty());
    ))

    }
}
)
,
{
}
,
false,
    notInPayload;
:
false,
    payloadName;
:
'myPropertyFour',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithComplexCollectionEnum';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithComplexCollectionEnum.MODEL_ELEMENT_URN;
}

getMetaModelVersion();
:
KnownVersion;
{
    return KnownVersionUtils.getLatest();
}

getName();
:
string;
{
    return 'AspectWithComplexCollectionEnum';
}

getProperties();
:
Array < StaticProperty < AspectWithComplexCollectionEnum, any >> {
    return [MetaAspectWithComplexCollectionEnum.MY_PROPERTY_ONE, MetaAspectWithComplexCollectionEnum.MY_PROPERTY_TWO, MetaAspectWithComplexCollectionEnum.MY_PROPERTY_THREE, MetaAspectWithComplexCollectionEnum.MY_PROPERTY_FOUR];
};

getAllProperties();
:
Array < StaticProperty < AspectWithComplexCollectionEnum, any >> {
    return this.getProperties();
};


}


