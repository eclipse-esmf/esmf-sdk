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

import { AspectWithNumericRegularExpressionConstraint, } from './AspectWithNumericRegularExpressionConstraint';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithNumericRegularExpressionConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNumericRegularExpressionConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithNumericRegularExpressionConstraint implements StaticMetaClass<AspectWithNumericRegularExpressionConstraint>, PropertyContainer<AspectWithNumericRegularExpressionConstraint> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNumericRegularExpressionConstraint';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithNumericRegularExpressionConstraint();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithNumericRegularExpressionConstraint, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithNumericRegularExpressionConstraint';
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

    DefaultRegularExpressionConstraint({
                                           isAnonymous: true,
                                           preferredNames: [],
                                           descriptions: [],
                                           see: [],
                                       },

    '\\d*|x';
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
'testProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithNumericRegularExpressionConstraint';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithNumericRegularExpressionConstraint.MODEL_ELEMENT_URN;
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
    return 'AspectWithNumericRegularExpressionConstraint';
}

getProperties();
:
Array < StaticProperty < AspectWithNumericRegularExpressionConstraint, any >> {
    return [MetaAspectWithNumericRegularExpressionConstraint.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithNumericRegularExpressionConstraint, any >> {
    return this.getProperties();
};


}


