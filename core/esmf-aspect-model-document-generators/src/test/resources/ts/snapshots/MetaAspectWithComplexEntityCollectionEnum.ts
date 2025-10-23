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


import { AspectWithComplexEntityCollectionEnum, } from './AspectWithComplexEntityCollectionEnum';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { MetaMyEntityOne, } from './MetaMyEntityOne';
import { MetaMyEntityTwo, } from './MetaMyEntityTwo';

import { string, } from 'asdas';


/*
* Generated class MetaAspectWithComplexEntityCollectionEnum (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexEntityCollectionEnum).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithComplexEntityCollectionEnum implements StaticMetaClass<AspectWithComplexEntityCollectionEnum>, PropertyContainer<AspectWithComplexEntityCollectionEnum> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithComplexEntityCollectionEnum';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithComplexEntityCollectionEnum();


    public static readonly MY_PROPERTY_ONE =

        new (class extends DefaultStaticProperty<AspectWithComplexEntityCollectionEnum, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithComplexEntityCollectionEnum';
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
                    descriptions: [{
                        value: 'This is my enumeration one',
                        languageTag: 'en',
                    },
                    ],
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
                add(new DefaultEntityInstance({
                    urn: this.NAMESPACE + 'entityInstanceTwo',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new HashMap<Property, Value>()
                {
                    {
                        put(MetaMyEntityTwo.ENTITY_PROPERTY_TWO, {
                            metaModelBaseAttributes: {},
                            value: 'foo',
                            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                        });
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
    ,
        org.eclipse.esmf.metamodel.CollectionValue.CollectionType.LIST, DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'MyEntityTwo',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, MetaMyEntityTwo.INSTANCE.getProperties(), Optional.empty());
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



getModelClass();
:
string;
{
    return 'AspectWithComplexEntityCollectionEnum';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithComplexEntityCollectionEnum.MODEL_ELEMENT_URN;
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
    return 'AspectWithComplexEntityCollectionEnum';
}

getProperties();
:
Array < StaticProperty < AspectWithComplexEntityCollectionEnum, any >> {
    return [MetaAspectWithComplexEntityCollectionEnum.MY_PROPERTY_ONE];
};

getAllProperties();
:
Array < StaticProperty < AspectWithComplexEntityCollectionEnum, any >> {
    return this.getProperties();
};


}


