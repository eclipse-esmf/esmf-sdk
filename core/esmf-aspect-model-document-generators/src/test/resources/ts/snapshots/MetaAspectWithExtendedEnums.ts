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


import { AspectWithExtendedEnums, } from './AspectWithExtendedEnums';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';

import { MetaEvaluationResult, } from './MetaEvaluationResult';
import { MetaNestedResult, } from './MetaNestedResult';

import { number, string, } from 'asdas';


/*
* Generated class MetaAspectWithExtendedEnums (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithExtendedEnums).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithExtendedEnums implements StaticMetaClass<AspectWithExtendedEnums>, PropertyContainer<AspectWithExtendedEnums> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithExtendedEnums';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithExtendedEnums();


    public static readonly RESULT =

        new (class extends DefaultStaticProperty<AspectWithExtendedEnums, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithExtendedEnums';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'result',
                    preferredNames: [{
                        value: 'result',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultEnumeration({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [{
                        value: 'Evaluation Results',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Possible values for the evaluation of a process',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'EvaluationResult',
                    preferredNames: [{
                        value: 'Evaluation Result',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Possible values for the evaluation of a process',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, MetaEvaluationResult.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>() { {
    add(

    new;

    DefaultEntityInstance({
                              urn: this.NAMESPACE

+
    'ResultBad';
,
    preferredNames: [];
,
    descriptions: [];
,
    see: [];
,
}

,
new HashMap<Property, Value>();
{
    {
        put(MetaEvaluationResult.AVERAGE, {
            metaModelBaseAttributes: {},
            value: new BigInteger('13'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
        });
        put(MetaEvaluationResult.NUMERIC_CODE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('2'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaEvaluationResult.DESCRIPTION, {
            metaModelBaseAttributes: {},
            value: 'Bad',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
        put(MetaEvaluationResult.NESTED_RESULT, new DefaultEntityInstance({
            urn: this.NAMESPACE + 'NestedResultGood',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaNestedResult.AVERAGE, {
                    metaModelBaseAttributes: {},
                    value: new BigInteger('1'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
                });
                put(MetaNestedResult.DESCRIPTION, {
                    metaModelBaseAttributes: {},
                    value: 'GOOD',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'NestedResult',
            preferredNames: [{
                value: 'Nested Result',
                languageTag: 'en',
            },
            ],
            descriptions: [{
                value: 'A nested result for testing',
                languageTag: 'en',
            },
            ],
            see: [],
        }, MetaNestedResult.INSTANCE.getProperties(), Optional.empty());
    ))

    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'EvaluationResult',
    preferredNames: [{
        value: 'Evaluation Result',
        languageTag: 'en',
    },
    ],
    descriptions: [{
        value: 'Possible values for the evaluation of a process',
        languageTag: 'en',
    },
    ],
    see: [],
}, MetaEvaluationResult.INSTANCE.getProperties(), Optional.empty());
))
add(new DefaultEntityInstance({
    urn: this.NAMESPACE + 'ResultGood',
    preferredNames: [],
    descriptions: [],
    see: [],
}, new HashMap<Property, Value>()
{
    {
        put(MetaEvaluationResult.AVERAGE, {
            metaModelBaseAttributes: {},
            value: new BigInteger('4'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
        });
        put(MetaEvaluationResult.NUMERIC_CODE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('1'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaEvaluationResult.DESCRIPTION, {
            metaModelBaseAttributes: {},
            value: 'Good',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
        put(MetaEvaluationResult.NESTED_RESULT, new DefaultEntityInstance({
            urn: this.NAMESPACE + 'NestedResultGood',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaNestedResult.AVERAGE, {
                    metaModelBaseAttributes: {},
                    value: new BigInteger('1'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
                });
                put(MetaNestedResult.DESCRIPTION, {
                    metaModelBaseAttributes: {},
                    value: 'GOOD',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'NestedResult',
            preferredNames: [{
                value: 'Nested Result',
                languageTag: 'en',
            },
            ],
            descriptions: [{
                value: 'A nested result for testing',
                languageTag: 'en',
            },
            ],
            see: [],
        }, MetaNestedResult.INSTANCE.getProperties(), Optional.empty());
    ))

    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'EvaluationResult',
    preferredNames: [{
        value: 'Evaluation Result',
        languageTag: 'en',
    },
    ],
    descriptions: [{
        value: 'Possible values for the evaluation of a process',
        languageTag: 'en',
    },
    ],
    see: [],
}, MetaEvaluationResult.INSTANCE.getProperties(), Optional.empty());
))
add(new DefaultEntityInstance({
    urn: this.NAMESPACE + 'ResultNoStatus',
    preferredNames: [],
    descriptions: [],
    see: [],
}, new HashMap<Property, Value>()
{
    {
        put(MetaEvaluationResult.AVERAGE, {
            metaModelBaseAttributes: {},
            value: new BigInteger('3'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
        });
        put(MetaEvaluationResult.NUMERIC_CODE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('-1'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaEvaluationResult.DESCRIPTION, {
            metaModelBaseAttributes: {},
            value: 'No status',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
        put(MetaEvaluationResult.NESTED_RESULT, new DefaultEntityInstance({
            urn: this.NAMESPACE + 'NestedResultGood',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaNestedResult.AVERAGE, {
                    metaModelBaseAttributes: {},
                    value: new BigInteger('1'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
                });
                put(MetaNestedResult.DESCRIPTION, {
                    metaModelBaseAttributes: {},
                    value: 'GOOD',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'NestedResult',
            preferredNames: [{
                value: 'Nested Result',
                languageTag: 'en',
            },
            ],
            descriptions: [{
                value: 'A nested result for testing',
                languageTag: 'en',
            },
            ],
            see: [],
        }, MetaNestedResult.INSTANCE.getProperties(), Optional.empty());
    ))

    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'EvaluationResult',
    preferredNames: [{
        value: 'Evaluation Result',
        languageTag: 'en',
    },
    ],
    descriptions: [{
        value: 'Possible values for the evaluation of a process',
        languageTag: 'en',
    },
    ],
    see: [],
}, MetaEvaluationResult.INSTANCE.getProperties(), Optional.empty());
))

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
'result',
    isAbstract;
:
false,
})



public static readonly
SIMPLE_RESULT =

    new (class extends DefaultStaticProperty<AspectWithExtendedEnums, ReplacedAspectArtifact> {


        getPropertyType(): string {
            return 'ReplacedAspectArtifact';
        }

        getContainingType(): string {
            return 'AspectWithExtendedEnums';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'simpleResult',
                preferredNames: [{
                    value: 'simpleResult',
                    languageTag: 'en',
                },
                ],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultEnumeration({
                urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                preferredNames: [{
                    value: 'ReplacedAspectArtifact Result',
                    languageTag: 'en',
                },
                ],
                descriptions: [],
                see: [],
            }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'), new ArrayList<Value>()
{
    {
        add({
            metaModelBaseAttributes: {},
            value: 'No',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
        add({
            metaModelBaseAttributes: {},
            value: 'Yes',
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
'simpleResult',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithExtendedEnums';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithExtendedEnums.MODEL_ELEMENT_URN;
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
    return 'AspectWithExtendedEnums';
}

getProperties();
:
Array < StaticProperty < AspectWithExtendedEnums, any >> {
    return [MetaAspectWithExtendedEnums.RESULT, MetaAspectWithExtendedEnums.SIMPLE_RESULT];
};

getAllProperties();
:
Array < StaticProperty < AspectWithExtendedEnums, any >> {
    return this.getProperties();
};


}


