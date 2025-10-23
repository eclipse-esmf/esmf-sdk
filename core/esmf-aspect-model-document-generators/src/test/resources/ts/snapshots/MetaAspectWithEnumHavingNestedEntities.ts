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


import { AspectWithEnumHavingNestedEntities, } from './AspectWithEnumHavingNestedEntities';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';

import { MetaDetailEntity, } from './MetaDetailEntity';
import { MetaEvaluationResult, } from './MetaEvaluationResult';

import { number, string, } from 'asdas';


/*
* Generated class MetaAspectWithEnumHavingNestedEntities (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumHavingNestedEntities).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumHavingNestedEntities implements StaticMetaClass<AspectWithEnumHavingNestedEntities>, PropertyContainer<AspectWithEnumHavingNestedEntities> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumHavingNestedEntities';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEnumHavingNestedEntities();


    public static readonly RESULT =

        new (class extends DefaultStaticProperty<AspectWithEnumHavingNestedEntities, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEnumHavingNestedEntities';
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
        put(MetaEvaluationResult.DETAILS, new DefaultEntityInstance({
            urn: this.NAMESPACE + 'NoStatusDetails',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaDetailEntity.DESCRIPTION, {
                    metaModelBaseAttributes: {},
                    value: 'No status',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
                put(MetaDetailEntity.MESSAGE, {
                    metaModelBaseAttributes: {},
                    value: 'No status available',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
                put(MetaDetailEntity.NUMERIC_CODE, {
                    metaModelBaseAttributes: {},
                    value: Short.parseShort('-10'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
                });
            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'DetailEntity',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, MetaDetailEntity.INSTANCE.getProperties(), Optional.empty());
    ))

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
        put(MetaEvaluationResult.DETAILS, new DefaultEntityInstance({
            urn: this.NAMESPACE + 'SucceededStatusDetails',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaDetailEntity.DESCRIPTION, {
                    metaModelBaseAttributes: {},
                    value: 'Result succeeded',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
                put(MetaDetailEntity.MESSAGE, {
                    metaModelBaseAttributes: {},
                    value: 'Evaluation succeeded.',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
                put(MetaDetailEntity.NUMERIC_CODE, {
                    metaModelBaseAttributes: {},
                    value: Short.parseShort('10'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
                });
            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'DetailEntity',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, MetaDetailEntity.INSTANCE.getProperties(), Optional.empty());
    ))

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

    new (class extends DefaultStaticProperty<AspectWithEnumHavingNestedEntities, ReplacedAspectArtifact> {


        getPropertyType(): string {
            return 'ReplacedAspectArtifact';
        }

        getContainingType(): string {
            return 'AspectWithEnumHavingNestedEntities';
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
    return 'AspectWithEnumHavingNestedEntities';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEnumHavingNestedEntities.MODEL_ELEMENT_URN;
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
    return 'AspectWithEnumHavingNestedEntities';
}

getProperties();
:
Array < StaticProperty < AspectWithEnumHavingNestedEntities, any >> {
    return [MetaAspectWithEnumHavingNestedEntities.RESULT, MetaAspectWithEnumHavingNestedEntities.SIMPLE_RESULT];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEnumHavingNestedEntities, any >> {
    return this.getProperties();
};


}


