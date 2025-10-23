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


import { AspectWithEnumerationWithoutScalarVariable, } from './AspectWithEnumerationWithoutScalarVariable';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';

import { MetaEvaluationResult, } from './MetaEvaluationResult';


/*
* Generated class MetaAspectWithEnumerationWithoutScalarVariable (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumerationWithoutScalarVariable).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumerationWithoutScalarVariable implements StaticMetaClass<AspectWithEnumerationWithoutScalarVariable>, PropertyContainer<AspectWithEnumerationWithoutScalarVariable> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumerationWithoutScalarVariable';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEnumerationWithoutScalarVariable();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEnumerationWithoutScalarVariable, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEnumerationWithoutScalarVariable';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
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
    'ResultGood';
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
'testProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithEnumerationWithoutScalarVariable';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEnumerationWithoutScalarVariable.MODEL_ELEMENT_URN;
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
    return 'AspectWithEnumerationWithoutScalarVariable';
}

getProperties();
:
Array < StaticProperty < AspectWithEnumerationWithoutScalarVariable, any >> {
    return [MetaAspectWithEnumerationWithoutScalarVariable.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEnumerationWithoutScalarVariable, any >> {
    return this.getProperties();
};


}


