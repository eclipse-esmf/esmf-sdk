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


import { AspectWithEntityEnumerationAndLangString, } from './AspectWithEntityEnumerationAndLangString';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from 'org.eclipse.esmf.metamodel.datatype';
import { Locale, } from 'java.util';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';


/*
* Generated class MetaAspectWithEntityEnumerationAndLangString (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityEnumerationAndLangString).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityEnumerationAndLangString implements StaticMetaClass<AspectWithEntityEnumerationAndLangString>, PropertyContainer<AspectWithEntityEnumerationAndLangString> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityEnumerationAndLangString';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEntityEnumerationAndLangString();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEntityEnumerationAndLangString, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEntityEnumerationAndLangString';
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
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [{
                        value: 'Test Entity',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test entity',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>() { {
    add(

    new;

    DefaultEntityInstance({
                              urn: this.NAMESPACE

+
    'entityInstance';
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
        put(MetaReplacedAspectArtifact.ENTITY_PROPERTY, {
            metaModelBaseAttributes: {},
            value: new LangString('This is a test.', Locale.forLanguageTag('en')),
            type: new DefaultScalar('http://www.w3.org/1999/02/22-rdf-syntax-ns#langString'),
        });
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
    preferredNames: [{
        value: 'Test Entity',
        languageTag: 'en',
    },
    ],
    descriptions: [{
        value: 'This is a test entity',
        languageTag: 'en',
    },
    ],
    see: [],
}, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty());
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
    return 'AspectWithEntityEnumerationAndLangString';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEntityEnumerationAndLangString.MODEL_ELEMENT_URN;
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
    return 'AspectWithEntityEnumerationAndLangString';
}

getProperties();
:
Array < StaticProperty < AspectWithEntityEnumerationAndLangString, any >> {
    return [MetaAspectWithEntityEnumerationAndLangString.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEntityEnumerationAndLangString, any >> {
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


