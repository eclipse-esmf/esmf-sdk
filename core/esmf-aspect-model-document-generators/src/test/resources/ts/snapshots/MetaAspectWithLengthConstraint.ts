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

import { AspectWithLengthConstraint, } from './AspectWithLengthConstraint';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithLengthConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithLengthConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithLengthConstraint implements StaticMetaClass<AspectWithLengthConstraint>, PropertyContainer<AspectWithLengthConstraint> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithLengthConstraint';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithLengthConstraint();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithLengthConstraint, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithLengthConstraint';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [{
                        value: 'Test Property',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test property.',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                        'http://example.com/me',
                    ],
                },
                characteristic: new DefaultTrait({
                    urn: this.NAMESPACE + 'TestLengthConstraint',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, {
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#Text',
                        preferredNames: [{
                            value: 'Text',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.',
                            languageTag: 'en',
                        },
                        ],
                        see: [],
                    },
                }, new ArrayList<Constraint>() { {
    add(

    new;

    DefaultLengthConstraint({
                                isAnonymous: true,
                                preferredNames: [{
                                    value: 'Test Length Constraint',
                                    languageTag: 'en',
                                },
                                ],
                                descriptions: [{
                                    value: 'This is a test length constraint.',
                                    languageTag: 'en',
                                },
                                ],
                                see: ['http://example.com/',
                                ],
                            }, Optional

.

    of(

    new;

    BigInteger(

    '5';
)),
    Optional;
.

    of(

    new;

    BigInteger(

    '10';
))));
}
})
,
{
    {
    }
,
    'Test123',
        type;
:
    new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
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
    return 'AspectWithLengthConstraint';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithLengthConstraint.MODEL_ELEMENT_URN;
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
    return 'AspectWithLengthConstraint';
}

getProperties();
:
Array < StaticProperty < AspectWithLengthConstraint, any >> {
    return [MetaAspectWithLengthConstraint.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithLengthConstraint, any >> {
    return this.getProperties();
};


getPreferredNames();
:
Array < LangString > {
    return [
        {value: 'Test Aspect', languageTag: 'en'},
    ];
};


getDescriptions();
:
Array < LangString > {
    return [
        {value: 'This is a test description', languageTag: 'en'},
    ];
};

getSee();
:
Array < String > {
    return [
        'http://example.com/',
    ];
};

}


