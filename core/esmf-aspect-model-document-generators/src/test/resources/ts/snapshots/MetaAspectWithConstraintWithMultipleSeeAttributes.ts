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

import { AspectWithConstraintWithMultipleSeeAttributes, } from './AspectWithConstraintWithMultipleSeeAttributes';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithConstraintWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstraintWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstraintWithMultipleSeeAttributes implements StaticMetaClass<AspectWithConstraintWithMultipleSeeAttributes>, PropertyContainer<AspectWithConstraintWithMultipleSeeAttributes> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstraintWithMultipleSeeAttributes';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithConstraintWithMultipleSeeAttributes();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithConstraintWithMultipleSeeAttributes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithConstraintWithMultipleSeeAttributes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultTrait({
                    urn: this.NAMESPACE + 'TestTrait',
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
                                    value: 'Test Constraint',
                                    languageTag: 'en',
                                },
                                ],
                                descriptions: [{
                                    value: 'Test Constraint',
                                    languageTag: 'en',
                                },
                                ],
                                see: ['http://example.com/',
                                    'http://example.com/me',
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
    return 'AspectWithConstraintWithMultipleSeeAttributes';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithConstraintWithMultipleSeeAttributes.MODEL_ELEMENT_URN;
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
    return 'AspectWithConstraintWithMultipleSeeAttributes';
}

getProperties();
:
Array < StaticProperty < AspectWithConstraintWithMultipleSeeAttributes, any >> {
    return [MetaAspectWithConstraintWithMultipleSeeAttributes.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithConstraintWithMultipleSeeAttributes, any >> {
    return this.getProperties();
};


}


