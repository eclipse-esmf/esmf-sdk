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


import { AspectWithAnyValueDeclarations, } from './AspectWithAnyValueDeclarations';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';

import { string, } from 'asdas';


/*
* Generated class MetaAspectWithAnyValueDeclarations (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithAnyValueDeclarations).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithAnyValueDeclarations implements StaticMetaClass<AspectWithAnyValueDeclarations>, PropertyContainer<AspectWithAnyValueDeclarations> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithAnyValueDeclarations';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithAnyValueDeclarations();


    public static readonly PROPERTY1 =

        new (class extends DefaultStaticProperty<AspectWithAnyValueDeclarations, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithAnyValueDeclarations';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'property1',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultEnumeration({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [{
                        value: 'Warning Level',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Represents if speed of position change is within specification (green), within tolerance (yellow), or outside specification (red).',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'), new ArrayList<Value>() { {
    add({
            metaModelBaseAttributes: {
                isAnonymous: true,
                preferredNames: [{
                    value: 'Normal',
                    languageTag: 'en',
                },
                ],
                descriptions: [{
                    value: 'Indicates that the speed of position change is within specification.',
                    languageTag: 'en',
                },
                ],
                see: ['https://en.wikipedia.org/wiki/Traffic_light',
                ],
            },
            value: 'green',
            type: new DefaultScalar

(
    'http://www.w3.org/2001/XMLSchema#string';
),
}

)
add({
    metaModelBaseAttributes: {},
    value: 'red',
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
});
add({
    metaModelBaseAttributes: {},
    value: 'yellow',
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
});
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
'property1',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithAnyValueDeclarations';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithAnyValueDeclarations.MODEL_ELEMENT_URN;
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
    return 'AspectWithAnyValueDeclarations';
}

getProperties();
:
Array < StaticProperty < AspectWithAnyValueDeclarations, any >> {
    return [MetaAspectWithAnyValueDeclarations.PROPERTY1];
};

getAllProperties();
:
Array < StaticProperty < AspectWithAnyValueDeclarations, any >> {
    return this.getProperties();
};


}


