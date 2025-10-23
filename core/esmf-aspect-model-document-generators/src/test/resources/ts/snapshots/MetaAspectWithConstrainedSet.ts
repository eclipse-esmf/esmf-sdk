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

import { AspectWithConstrainedSet, } from './AspectWithConstrainedSet';
import { LangString, } from './core/langString';
import { StaticContainerProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithConstrainedSet (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstrainedSet).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstrainedSet implements StaticMetaClass<AspectWithConstrainedSet>, PropertyContainer<AspectWithConstrainedSet> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstrainedSet';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithConstrainedSet();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithConstrainedSet, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithConstrainedSet';
            }

            getContainedType(): AspectWithConstrainedSet {
                return 'AspectWithConstrainedSet';
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
                    urn: this.NAMESPACE + 'TestTrait',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultSet({
                    urn: this.NAMESPACE + 'TestSet',
                    preferredNames: [{
                        value: 'Test Set',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test set.',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                    ],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#string')), Optional.empty()), new ArrayList<Constraint>() { {
    add(

    new;

    DefaultLengthConstraint({
                                urn: this.NAMESPACE

+
    'TestSetConstraint';
,
    preferredNames: [{
        value: 'TestSet Constraint',
        languageTag: 'en',
    },
    ];
,
    descriptions: [{
        value: 'Constraint for defining a non-empty set of identifiers.',
        languageTag: 'en',
    },
    ];
,
    see: ['http://example.com/',
    ];
,
}

,
Optional.of(new BigInteger('1')), Optional.empty();
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
    return 'AspectWithConstrainedSet';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithConstrainedSet.MODEL_ELEMENT_URN;
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
    return 'AspectWithConstrainedSet';
}

getProperties();
:
Array < StaticProperty < AspectWithConstrainedSet, any >> {
    return [MetaAspectWithConstrainedSet.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithConstrainedSet, any >> {
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


