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


import { AspectWithEnumerationWithSeeAttribute, } from './AspectWithEnumerationWithSeeAttribute';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


import { string, } from 'asdas';


/*
* Generated class MetaAspectWithEnumerationWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumerationWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumerationWithSeeAttribute implements StaticMetaClass<AspectWithEnumerationWithSeeAttribute>, PropertyContainer<AspectWithEnumerationWithSeeAttribute> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumerationWithSeeAttribute';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEnumerationWithSeeAttribute();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEnumerationWithSeeAttribute, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEnumerationWithSeeAttribute';
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
                    preferredNames: [{
                        value: 'Test Enumeration',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Test Enumeration',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                    ],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'), new ArrayList<Value>() { {
    add({
            metaModelBaseAttributes: {},
            value: 'bar',
            type: new DefaultScalar

(
    'http://www.w3.org/2001/XMLSchema#string';
),
}

)
add({
    metaModelBaseAttributes: {},
    value: 'foo',
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
});
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



public static readonly
TEST_PROPERTY_TWO =

    new (class extends DefaultStaticProperty<AspectWithEnumerationWithSeeAttribute, ReplacedAspectArtifact> {


        getPropertyType(): string {
            return 'ReplacedAspectArtifact';
        }

        getContainingType(): string {
            return 'AspectWithEnumerationWithSeeAttribute';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'testPropertyTwo',
                preferredNames: [],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultEnumeration({
                urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                preferredNames: [{
                    value: 'Test Enumeration Two',
                    languageTag: 'en',
                },
                ],
                descriptions: [{
                    value: 'Test Enumeration Two',
                    languageTag: 'en',
                },
                ],
                see: ['http://example.com/',
                    'http://example.com/me',
                ],
            }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'), new ArrayList<Value>()
{
    {
        add({
            metaModelBaseAttributes: {},
            value: 'bar',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
        add({
            metaModelBaseAttributes: {},
            value: 'foo',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
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
'testPropertyTwo',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithEnumerationWithSeeAttribute';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEnumerationWithSeeAttribute.MODEL_ELEMENT_URN;
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
    return 'AspectWithEnumerationWithSeeAttribute';
}

getProperties();
:
Array < StaticProperty < AspectWithEnumerationWithSeeAttribute, any >> {
    return [MetaAspectWithEnumerationWithSeeAttribute.TEST_PROPERTY, MetaAspectWithEnumerationWithSeeAttribute.TEST_PROPERTY_TWO];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEnumerationWithSeeAttribute, any >> {
    return this.getProperties();
};


}


