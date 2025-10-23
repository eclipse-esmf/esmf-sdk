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

import { AspectWithRangeConstraintWithoutMinMaxIntegerValue, } from './AspectWithRangeConstraintWithoutMinMaxIntegerValue';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithoutMinMaxIntegerValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue implements StaticMetaClass<AspectWithRangeConstraintWithoutMinMaxIntegerValue>, PropertyContainer<AspectWithRangeConstraintWithoutMinMaxIntegerValue> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithoutMinMaxIntegerValue';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue();


    public static readonly TEST_INT =

        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithoutMinMaxIntegerValue, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithRangeConstraintWithoutMinMaxIntegerValue';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testInt',
                    preferredNames: [{
                        value: 'Numerischer Wert',
                        languageTag: 'de',
                    },
                        {
                            value: 'Test Integer Property',
                            languageTag: 'en',
                        },
                    ],
                    descriptions: [{
                        value: 'Eine Property mit einem numerischen Wert.',
                        languageTag: 'de',
                    },
                        {
                            value: 'A property with a numeric value.',
                            languageTag: 'en',
                        },
                    ],
                    see: [],
                },
                characteristic: new DefaultTrait({
                    urn: this.NAMESPACE + 'TestRangeConstraint',
                    preferredNames: [{
                        value: 'Test Range Constraint',
                        languageTag: 'de',
                    },
                        {
                            value: 'Test Range Constraint',
                            languageTag: 'en',
                        },
                    ],
                    descriptions: [{
                        value: 'Beschränkt einen numerischen Wert auf Werte zwischen 0 und 100.',
                        languageTag: 'de',
                    },
                        {
                            value: 'Restricts a numeric value to values between 0 and 100.',
                            languageTag: 'en',
                        },
                    ],
                    see: [],
                }, {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'IntegerCharacteristic',
                        preferredNames: [{
                            value: 'Numerische Charakteristik',
                            languageTag: 'de',
                        },
                            {
                                value: 'Integer Characteristic',
                                languageTag: 'en',
                            },
                        ],
                        descriptions: [{
                            value: 'Positive Zahlen',
                            languageTag: 'de',
                        },
                        ],
                        see: [],
                    },
                }, new ArrayList<Constraint>() { {
    add(

    new;

    DefaultRangeConstraint({
                               isAnonymous: true,
                               preferredNames: [],
                               descriptions: [],
                               see: [],
                           }, Optional

.

    empty()

,
    Optional;
.

    empty()

,
    BoundDefinition;
.
    OPEN;
,
    BoundDefinition;
.
    OPEN;
));
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
'testInt',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithRangeConstraintWithoutMinMaxIntegerValue';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue.MODEL_ELEMENT_URN;
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
    return 'AspectWithRangeConstraintWithoutMinMaxIntegerValue';
}

getProperties();
:
Array < StaticProperty < AspectWithRangeConstraintWithoutMinMaxIntegerValue, any >> {
    return [MetaAspectWithRangeConstraintWithoutMinMaxIntegerValue.TEST_INT];
};

getAllProperties();
:
Array < StaticProperty < AspectWithRangeConstraintWithoutMinMaxIntegerValue, any >> {
    return this.getProperties();
};


getPreferredNames();
:
Array < LangString > {
    return [
        {value: 'Test Aspect', languageTag: 'en'},
        {value: 'Test Aspekt', languageTag: 'de'},
    ];
};


}


