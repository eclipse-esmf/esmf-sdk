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


import { AspectWithComplexEnum, } from './AspectWithComplexEnum';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';

import { MetaEvaluationResult, } from './MetaEvaluationResult';

import { number, string, } from 'asdas';


/*
* Generated class MetaAspectWithComplexEnum (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithComplexEnum).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithComplexEnum implements StaticMetaClass<AspectWithComplexEnum>, PropertyContainer<AspectWithComplexEnum> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithComplexEnum';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithComplexEnum();


    public static readonly RESULT =

        new (class extends DefaultStaticProperty<AspectWithComplexEnum, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithComplexEnum';
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
                        value: 'Evalution Result',
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
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'EvaluationResult',
    preferredNames: [{
        value: 'Evalution Result',
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
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'EvaluationResult',
    preferredNames: [{
        value: 'Evalution Result',
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
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'EvaluationResult',
    preferredNames: [{
        value: 'Evalution Result',
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

    new (class extends DefaultStaticProperty<AspectWithComplexEnum, ReplacedAspectArtifact> {


        getPropertyType(): string {
            return 'ReplacedAspectArtifact';
        }

        getContainingType(): string {
            return 'AspectWithComplexEnum';
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
    return 'AspectWithComplexEnum';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithComplexEnum.MODEL_ELEMENT_URN;
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
    return 'AspectWithComplexEnum';
}

getProperties();
:
Array < StaticProperty < AspectWithComplexEnum, any >> {
    return [MetaAspectWithComplexEnum.RESULT, MetaAspectWithComplexEnum.SIMPLE_RESULT];
};

getAllProperties();
:
Array < StaticProperty < AspectWithComplexEnum, any >> {
    return this.getProperties();
};


}


