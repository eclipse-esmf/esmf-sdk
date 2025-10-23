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


import { AspectWithEntityInstanceWithNestedEntityListProperty, } from './AspectWithEntityInstanceWithNestedEntityListProperty';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { MetaNestedEntity, } from './MetaNestedEntity';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';

import { number, string, } from 'asdas';


/*
* Generated class MetaAspectWithEntityInstanceWithNestedEntityListProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityInstanceWithNestedEntityListProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityInstanceWithNestedEntityListProperty implements StaticMetaClass<AspectWithEntityInstanceWithNestedEntityListProperty>, PropertyContainer<AspectWithEntityInstanceWithNestedEntityListProperty> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityInstanceWithNestedEntityListProperty';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEntityInstanceWithNestedEntityListProperty();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEntityInstanceWithNestedEntityListProperty, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEntityInstanceWithNestedEntityListProperty';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
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
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>() { {
    add(

    new;

    DefaultEntityInstance({
                              urn: this.NAMESPACE

+
    'ReplacedAspectArtifactInstance';
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
        put(MetaReplacedAspectArtifact.CODE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('3'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaReplacedAspectArtifact.TEST_LIST, new DefaultCollectionValue(new ArrayList<>()
        {
            {
                add(new DefaultEntityInstance({
                    urn: this.NAMESPACE + 'NestedEntityInstance',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new HashMap<Property, Value>()
                {
                    {
                        put(MetaNestedEntity.NESTED_ENTITY_PROPERTY, {
                            metaModelBaseAttributes: {},
                            value: 'bar',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                    }
                }
            ,
                DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'NestedEntity',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaNestedEntity.INSTANCE.getProperties(), Optional.empty());
            ))
                add(new DefaultEntityInstance({
                urn: this.NAMESPACE + 'NestedEntityInstanceTwo',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, new HashMap<Property, Value>()
                {
                    {
                        put(MetaNestedEntity.NESTED_ENTITY_PROPERTY, {
                            metaModelBaseAttributes: {},
                            value: 'baz',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
                    }
                }
            ,
                DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'NestedEntity',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaNestedEntity.INSTANCE.getProperties(), Optional.empty());
            ))

            }
        }
    ,
        org.eclipse.esmf.metamodel.CollectionValue.CollectionType.LIST, DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'NestedEntity',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, MetaNestedEntity.INSTANCE.getProperties(), Optional.empty());
    ))

    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
    preferredNames: [],
    descriptions: [],
    see: [],
}, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty());
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
'testProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithEntityInstanceWithNestedEntityListProperty';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEntityInstanceWithNestedEntityListProperty.MODEL_ELEMENT_URN;
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
    return 'AspectWithEntityInstanceWithNestedEntityListProperty';
}

getProperties();
:
Array < StaticProperty < AspectWithEntityInstanceWithNestedEntityListProperty, any >> {
    return [MetaAspectWithEntityInstanceWithNestedEntityListProperty.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEntityInstanceWithNestedEntityListProperty, any >> {
    return this.getProperties();
};


}


