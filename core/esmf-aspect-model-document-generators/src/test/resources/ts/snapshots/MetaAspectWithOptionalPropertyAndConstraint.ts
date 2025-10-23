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

import { AspectWithOptionalPropertyAndConstraint, } from './AspectWithOptionalPropertyAndConstraint';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithOptionalPropertyAndConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithOptionalPropertyAndConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithOptionalPropertyAndConstraint implements StaticMetaClass<AspectWithOptionalPropertyAndConstraint>, PropertyContainer<AspectWithOptionalPropertyAndConstraint> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithOptionalPropertyAndConstraint';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithOptionalPropertyAndConstraint();


    public static readonly STRING_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithOptionalPropertyAndConstraint, string, string> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithOptionalPropertyAndConstraint';
            }

            getContainedType(): AspectWithOptionalPropertyAndConstraint {
                return 'AspectWithOptionalPropertyAndConstraint';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'stringProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
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
                                preferredNames: [],
                                descriptions: [],
                                see: [],
                            }, Optional

.

    empty()

,
    Optional;
.

    of(

    new;

    BigInteger(

    '3';
))));
}
})
,
{
}
,
true,
    notInPayload;
:
false,
    payloadName;
:
'stringProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithOptionalPropertyAndConstraint';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithOptionalPropertyAndConstraint.MODEL_ELEMENT_URN;
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
    return 'AspectWithOptionalPropertyAndConstraint';
}

getProperties();
:
Array < StaticProperty < AspectWithOptionalPropertyAndConstraint, any >> {
    return [MetaAspectWithOptionalPropertyAndConstraint.STRING_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithOptionalPropertyAndConstraint, any >> {
    return this.getProperties();
};


}


