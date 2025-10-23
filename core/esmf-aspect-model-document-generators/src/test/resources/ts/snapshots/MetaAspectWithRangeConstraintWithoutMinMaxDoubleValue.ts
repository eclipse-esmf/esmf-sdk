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

import { AspectWithRangeConstraintWithoutMinMaxDoubleValue, } from './AspectWithRangeConstraintWithoutMinMaxDoubleValue';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithoutMinMaxDoubleValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue implements StaticMetaClass<AspectWithRangeConstraintWithoutMinMaxDoubleValue>, PropertyContainer<AspectWithRangeConstraintWithoutMinMaxDoubleValue> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithoutMinMaxDoubleValue';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue();


    public static readonly DOUBLE_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithoutMinMaxDoubleValue, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithRangeConstraintWithoutMinMaxDoubleValue';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'doubleProperty',
                    preferredNames: [{
                        value: 'Numerischer Wert',
                        languageTag: 'de',
                    },
                        {
                            value: 'Test Double Property',
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
                        urn: this.NAMESPACE + 'DoubleCharacteristic',
                        preferredNames: [{
                            value: 'Numerische Charakteristik',
                            languageTag: 'de',
                        },
                            {
                                value: 'Double Characteristic',
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
'doubleProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithRangeConstraintWithoutMinMaxDoubleValue';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue.MODEL_ELEMENT_URN;
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
    return 'AspectWithRangeConstraintWithoutMinMaxDoubleValue';
}

getProperties();
:
Array < StaticProperty < AspectWithRangeConstraintWithoutMinMaxDoubleValue, any >> {
    return [MetaAspectWithRangeConstraintWithoutMinMaxDoubleValue.DOUBLE_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithRangeConstraintWithoutMinMaxDoubleValue, any >> {
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


