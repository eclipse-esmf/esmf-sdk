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

import { AspectWithRegularExpressionConstraint, } from './AspectWithRegularExpressionConstraint';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithRegularExpressionConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRegularExpressionConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRegularExpressionConstraint implements StaticMetaClass<AspectWithRegularExpressionConstraint>, PropertyContainer<AspectWithRegularExpressionConstraint> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRegularExpressionConstraint';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithRegularExpressionConstraint();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithRegularExpressionConstraint, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithRegularExpressionConstraint';
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
                    urn: this.NAMESPACE + 'TestRegularExpressionConstraint',
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
                                           preferredNames: [{
                                               value: 'Test Regular Expression Constraint',
                                               languageTag: 'en',
                                           },
                                           ],
                                           descriptions: [{
                                               value: 'This is a test regular expression constraint.',
                                               languageTag: 'en',
                                           },
                                           ],
                                           see: ['http://example.com/',
                                           ],
                                       },

    '^[0-9]*$';
));
}
})
,
{
    {
    }
,
    '3',
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
    return 'AspectWithRegularExpressionConstraint';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithRegularExpressionConstraint.MODEL_ELEMENT_URN;
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
    return 'AspectWithRegularExpressionConstraint';
}

getProperties();
:
Array < StaticProperty < AspectWithRegularExpressionConstraint, any >> {
    return [MetaAspectWithRegularExpressionConstraint.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithRegularExpressionConstraint, any >> {
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


