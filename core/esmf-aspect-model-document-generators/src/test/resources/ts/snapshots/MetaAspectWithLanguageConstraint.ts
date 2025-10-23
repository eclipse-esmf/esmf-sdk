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

import { AspectWithLanguageConstraint, } from './AspectWithLanguageConstraint';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithLanguageConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithLanguageConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithLanguageConstraint implements StaticMetaClass<AspectWithLanguageConstraint>, PropertyContainer<AspectWithLanguageConstraint> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithLanguageConstraint';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithLanguageConstraint();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithLanguageConstraint, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithLanguageConstraint';
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
                    urn: this.NAMESPACE + 'TestLanguageConstraint',
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

    DefaultLanguageConstraint({
                                  isAnonymous: true,
                                  preferredNames: [{
                                      value: 'Test Language Constraint',
                                      languageTag: 'en',
                                  },
                                  ],
                                  descriptions: [{
                                      value: 'This is a test language constraint.',
                                      languageTag: 'en',
                                  },
                                  ],
                                  see: ['http://example.com/',
                                  ],
                              }, Locale

.

    forLanguageTag(

    'de';
)));
}
})
,
{
    {
    }
,
    'Example Value',
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
    return 'AspectWithLanguageConstraint';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithLanguageConstraint.MODEL_ELEMENT_URN;
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
    return 'AspectWithLanguageConstraint';
}

getProperties();
:
Array < StaticProperty < AspectWithLanguageConstraint, any >> {
    return [MetaAspectWithLanguageConstraint.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithLanguageConstraint, any >> {
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


