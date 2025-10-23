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


import { AspectWithEnumeration, } from './AspectWithEnumeration';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';

import { string, } from 'asdas';


/*
* Generated class MetaAspectWithEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumeration implements StaticMetaClass<AspectWithEnumeration>, PropertyContainer<AspectWithEnumeration> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumeration';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEnumeration();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEnumeration, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEnumeration';
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
                characteristic: new DefaultEnumeration({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [{
                        value: 'Test Enumeration',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test for enumeration.',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                    ],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'), new ArrayList<Value>() { {
    add({
            metaModelBaseAttributes: {},
            value: new BigInteger

(
    '1';
),
    type: new DefaultScalar;
(
    'http://www.w3.org/2001/XMLSchema#integer';
),
}

)
add({
    metaModelBaseAttributes: {},
    value: new BigInteger('2'),
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
});
add({
    metaModelBaseAttributes: {},
    value: new BigInteger('3'),
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
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
'testProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithEnumeration';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEnumeration.MODEL_ELEMENT_URN;
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
    return 'AspectWithEnumeration';
}

getProperties();
:
Array < StaticProperty < AspectWithEnumeration, any >> {
    return [MetaAspectWithEnumeration.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEnumeration, any >> {
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


