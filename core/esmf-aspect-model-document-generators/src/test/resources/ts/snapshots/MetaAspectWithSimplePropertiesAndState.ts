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


import { AspectWithSimplePropertiesAndState, } from './AspectWithSimplePropertiesAndState';

import { Date, number, string, } from 'asdas';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithSimplePropertiesAndState (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimplePropertiesAndState).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSimplePropertiesAndState implements StaticMetaClass<AspectWithSimplePropertiesAndState>, PropertyContainer<AspectWithSimplePropertiesAndState> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSimplePropertiesAndState';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithSimplePropertiesAndState();


    public static readonly TEST_STRING =

        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimplePropertiesAndState';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testString',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
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
                }
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: 'Example Value Test',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testString',
                isAbstract: false,
            });


    public static readonly TEST_INT =

        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimplePropertiesAndState';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testInt',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'Int',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: Integer.valueOf('3'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#int'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testInt',
                isAbstract: false,
            });


    public static readonly TEST_FLOAT =

        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimplePropertiesAndState';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testFloat',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'Float',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: Float.valueOf('2.25'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#float'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testFloat',
                isAbstract: false,
            });


    public static readonly TEST_LOCAL_DATE_TIME =

        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimplePropertiesAndState';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testLocalDateTime',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'LocalDateTime',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: _datatypeFactory.newXMLGregorianCalendar('2018-02-28T14:23:32.918'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#dateTime'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testLocalDateTime',
                isAbstract: false,
            });


    public static readonly RANDOM_VALUE =

        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimplePropertiesAndState';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'randomValue',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
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
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'randomValue',
                isAbstract: false,
            });


    public static readonly AUTOMATION_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimplePropertiesAndState, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithSimplePropertiesAndState';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'automationProperty',
                    preferredNames: [{
                        value: 'Aktiviert/Deaktiviert',
                        languageTag: 'de',
                    },
                        {
                            value: 'Enabled/Disabled',
                            languageTag: 'en',
                        },
                    ],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultState({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [{
                        value: 'Return status for the Set Configuration Operation',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'), new ArrayList<Value>() { {
    add({
            metaModelBaseAttributes: {},
            value: 'ReplacedAspectArtifact Default Prop',
            type: new DefaultScalar

(
    'http://www.w3.org/2001/XMLSchema#string';
),
}

)
add({
    metaModelBaseAttributes: {},
    value: 'ReplacedAspectArtifact2',
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
});
add({
    metaModelBaseAttributes: {},
    value: 'ReplacedAspectArtifact3',
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
});
}
},
{
    {
    }
,
    'ReplacedAspectArtifact Default Prop',
        type;
:
    new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
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
'automationProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithSimplePropertiesAndState';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithSimplePropertiesAndState.MODEL_ELEMENT_URN;
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
    return 'AspectWithSimplePropertiesAndState';
}

getProperties();
:
Array < StaticProperty < AspectWithSimplePropertiesAndState, any >> {
    return [MetaAspectWithSimplePropertiesAndState.TEST_STRING, MetaAspectWithSimplePropertiesAndState.TEST_INT, MetaAspectWithSimplePropertiesAndState.TEST_FLOAT, MetaAspectWithSimplePropertiesAndState.TEST_LOCAL_DATE_TIME, MetaAspectWithSimplePropertiesAndState.RANDOM_VALUE, MetaAspectWithSimplePropertiesAndState.AUTOMATION_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithSimplePropertiesAndState, any >> {
    return this.getProperties();
};


}


